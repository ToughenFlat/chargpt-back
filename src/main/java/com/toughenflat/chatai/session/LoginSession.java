package com.toughenflat.chatai.session;

import com.toughenflat.chatai.enums.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginSession {

    private String loginAcct;

    private String password;

    private LoginType loginType;

    private String ip;

    private String socialUid;

    /**
     * 微信登录-头像经Base64编码的值
     */
    private String avatar;

    /**
     * 微信临时登录凭证
     */
    private String code;

    /**
     * 微信小程序ID
     */
    private String appId;

    /**
     * 微信小程序密钥
     */
    private String appSecret;
}
