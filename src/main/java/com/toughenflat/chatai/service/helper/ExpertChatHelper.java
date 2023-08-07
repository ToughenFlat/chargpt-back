package com.toughenflat.chatai.service.helper;

import com.toughenflat.chatai.api.openai.ChatGPTApi;
import com.toughenflat.chatai.api.openai.enums.Role;
import com.toughenflat.chatai.entity.SessionChatRecordEntity;
import com.toughenflat.chatai.entity.UserSessionEntity;
import com.toughenflat.chatai.service.PromptService;
import com.toughenflat.chatai.service.SessionChatRecordService;
import com.toughenflat.chatai.service.UserSessionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ExpertChatHelper {
    @Resource
    SessionChatRecordService sessionChatRecordService;
    @Resource
    PromptService promptService;
    @Resource
    UserSessionService userSessionService;

    private ExpertChatHelper() {}
    public boolean handleSessionSystemRecord(UserSessionEntity userSessionEntity) {
        String[] split = userSessionEntity.getSessionName().split(":");
        String prompt = promptService.getByTopic(split[split.length - 2]);
        SessionChatRecordEntity sessionChatRecordEntity = new SessionChatRecordEntity(
                userSessionEntity.getSessionId(), Role.SYSTEM.name, prompt, ChatGPTApi.getMessageTokenNum(prompt));
        return sessionChatRecordService.save(sessionChatRecordEntity);
    }

    public String getExpertChatLanguage(Integer sessionId) {
        UserSessionEntity userSessionEntity = userSessionService.getById(sessionId);
        String[] split = userSessionEntity.getSessionName().split(":");
        return split[split.length - 1];
    }
}
