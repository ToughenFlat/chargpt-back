package com.toughenflat.chatai.service;

import com.toughenflat.chatai.api.openai.req.ChatGPTReq;
import com.toughenflat.chatai.api.openai.resp.ChatGPTResp;
import com.toughenflat.chatai.api.openai.resp.CreditGrantsResp;
import com.toughenflat.chatai.enums.SessionType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatService {

    /**
     * 单轮聊天-普通输出
     */
    ChatGPTResp oneShotChat(String userId, ChatGPTReq chatGPTReq, String apiKey);

    /**
     * 单轮聊天-流式输出
     */
    void streamOneShotChat(String userId, ChatGPTReq chatGPTReq, String apiKey, SseEmitter sseEmitter);

    /**
     * 多轮聊天-普通输出
     */
    ChatGPTResp sessionChat(String userId, Integer sessionId, ChatGPTReq chatGPTReq,
                            String message, String apiKey, SessionType sessionType);

    /**
     * 单轮聊天-流式输出
     */
    void streamSessionChat(String userId,
                           Integer sessionId,
                           ChatGPTReq chatGPTReq,
                           String message,
                           String apiKey,
                           SseEmitter sseEmitter,
                           SessionType sessionType);

    /**
     * 清除用户所有聊天会话的缓存
     */
    void clearUserCache(String userId);

    /**
     * 手动刷新缓存
     */
    void refreshWindowRecordCache(Integer sessionId);

    /**
     * 获取API-Key的额度
     */
    CreditGrantsResp creditGrants(String apiKey);

}
