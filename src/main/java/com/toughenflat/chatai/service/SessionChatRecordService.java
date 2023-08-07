package com.toughenflat.chatai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.toughenflat.chatai.entity.SessionChatRecordEntity;

import java.util.List;

public interface SessionChatRecordService extends IService<SessionChatRecordEntity> {
    /**
     * 获取聊天记录
     */
    List<SessionChatRecordEntity> getSessionRecord(Integer sessionId);

    /**
     * 清空聊天记录
     */
    void truncateSessionChatRecord(Integer sessionId);
}
