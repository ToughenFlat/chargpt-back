package com.toughenflat.chatai.handler;

import com.toughenflat.chatai.session.LoginSession;

import java.util.Map;

public interface LoginHandler {

    /**
     * 处理不同的登录情况
     * @param loginSession
     * @return
     */
    Map<String, Object> login(LoginSession loginSession);
}
