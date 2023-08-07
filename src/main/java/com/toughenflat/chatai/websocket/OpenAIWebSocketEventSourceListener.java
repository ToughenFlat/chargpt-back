package com.toughenflat.chatai.websocket;

import cn.hutool.json.JSONUtil;
import com.toughenflat.chatai.api.openai.enums.OpenAiRespError;
import com.toughenflat.chatai.api.openai.resp.ChatGPTResp;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.util.Objects;

/**
 * 描述：OpenAI流式输出Socket接收
 */
@Slf4j
public class OpenAIWebSocketEventSourceListener extends EventSourceListener {
    private static final String DONE_SIGNAL = "[DONE]";

    private Session session;

    public OpenAIWebSocketEventSourceListener(Session session) {
        this.session = session;
    }

    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立sse连接...");
    }

    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        if (data.equals(DONE_SIGNAL)) {
            log.info("OpenAI返回数据结束");
            session.getBasicRemote().sendText(DONE_SIGNAL);
            return;
        }
        log.info(data);

        ChatGPTResp resp = JSONUtil.toBean(data, ChatGPTResp.class);
        if (StringUtils.isEmpty(resp)) {
            return;
        }
        String content = resp.getChoices().get(0).getDelta().getContent();

        if (StringUtils.isEmpty(content)) {
            return;
        }
        content = content.replace(" ", "「`」");
        content = content.replace("\n", "「·」");
        content = content.replace("\t", "「~」");
        session.getBasicRemote().sendText(content);
    }


    @Override
    public void onClosed(EventSource eventSource) {
        log.info("OpenAI关闭sse连接...");
    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            log.error("OpenAI  sse连接异常:{}", t);
            eventSource.cancel();
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            OpenAiRespError openAiRespError = OpenAiRespError.get(response.code());
            log.error("OpenAI  sse连接异常data：{}，异常：{}", body.string(), openAiRespError.msg);
            session.getBasicRemote().sendText(openAiRespError.msg);
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
        }
        response.close();
        eventSource.cancel();
    }
}