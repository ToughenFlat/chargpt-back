package com.toughenflat.chatai.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.ImmutableList;
import com.toughenflat.chatai.api.openai.constant.OpenAIConst;
import com.toughenflat.chatai.api.openai.enums.Role;
import com.toughenflat.chatai.api.openai.req.ChatGPTReq;
import com.toughenflat.chatai.api.openai.req.ContextMessage;
import com.toughenflat.chatai.api.openai.resp.ChatGPTResp;
import com.toughenflat.chatai.entity.UserApiKeyEntity;
import com.toughenflat.chatai.enums.ApiType;
import com.toughenflat.chatai.exception.BaseException;
import com.toughenflat.chatai.service.AdminApiKeyService;
import com.toughenflat.chatai.service.PromptService;
import com.toughenflat.chatai.service.UserApiKeyService;
import com.toughenflat.chatai.utils.OkHttpClientUtil;
import com.toughenflat.chatai.utils.ResultCode;
import com.toughenflat.chatai.utils.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 用WebSocket方式实现流式输出
 */
@Component
@ServerEndpoint("/websocket/{uid}")
@Slf4j
public class WebSocketServer {
    private static final String AUTHORIZATION_STR = "Authorization";

    @Resource
    private AdminApiKeyService adminApiKeyService;

    @Resource
    private PromptService promptService;

    @Resource
    private UserApiKeyService userApiKeyService;

    // 在线总数
    private static int onlineCount;

    // 当前会话
    private Session session;

    // 用户id
    private String uid;

    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 用来存放每个客户端对应的WebSocketServer对象
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap();

    /**
     * 为了保存在线用户信息，在方法中新建一个list存储一下【实际项目依据复杂度，可以存储到数据库或者缓存】
     */
    private final static List<Session> SESSIONS = Collections.synchronizedList(new ArrayList<>());

    /**
     * 建立连接
     * @param session
     * @param uid
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) {
        this.session = session;
        this.uid = uid;
        webSocketSet.add(this);
        SESSIONS.add(session);
        if (webSocketMap.containsKey(uid)) {
            webSocketMap.remove(uid);
            webSocketMap.put(uid, this);
        } else {
            webSocketMap.put(uid, this);
            addOnlineCount();
        }
        log.info("[连接ID:{}] 建立连接, 当前连接数:{}", this.uid, getOnlineCount());
    }

    /**
     * 断开连接
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        if (webSocketMap.containsKey(uid)) {
            webSocketMap.remove(uid);
            subOnlineCount();
        }
        log.info("[连接ID:{}] 断开连接, 当前连接数:{}", uid, getOnlineCount());
    }

    /**
     * 发送错误
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.info("[连接ID:{}] 错误原因:{}", this.uid, error.getMessage());
        error.printStackTrace();
    }

    /**
     * 接收到客户端消息
     * @param msg
     */
    @OnMessage
    public void onMessage(String msg) {
        log.info("[连接ID:{}] 收到消息:{}", this.uid, msg);
        ChatGPTReq chatGPTReq = ChatGPTReq.builder()
                .model(OpenAIConst.MODEL_NAME_CHATGPT_3_5)
                .messages(ImmutableList.of(new ContextMessage(Role.USER.name, msg)))
                .max_tokens(OpenAIConst.MAX_TOKENS)
                .stream(true)
                .build();
        // 若用户上传了apikey则使用用户的，否则采用本系统的
        UserApiKeyEntity userApiKeyEntity = userApiKeyService.getByUserIdAndType(uid, ApiType.OPENAI);
        String apiKey = userApiKeyEntity != null && !StringUtils.isEmpty(userApiKeyEntity.getApikey())
                ? userApiKeyEntity.getApikey()
                : adminApiKeyService.roundRobinGetByType(ApiType.OPENAI);
        if (apiKey == null) {
            return;
        }
        // 接受参数
        OpenAIWebSocketEventSourceListener eventSourceListener = new OpenAIWebSocketEventSourceListener(this.session);

        try {
            EventSource.Factory factory = EventSources.createFactory(OkHttpClientUtil.getClient());
            String requestBody = JSONUtil.parseObj(chatGPTReq).toString();
            Request request = new Request.Builder()
                    .url(OpenAIConst.HOST + OpenAIConst.CHATGPT_MAPPING)
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .header(AUTHORIZATION_STR, "Bearer " + apiKey)
                    .build();

            // 绑定请求 和 事件监听器
            factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求参数解析异常：{}", e);
        }
    }


    /**
     * 获取当前连接数
     *
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 当前连接数加一
     */
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    /**
     * 当前连接数减一
     */
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}

