package com.toughenflat.chatai.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginRequest {
    @NotEmpty(message = "微信登录临时凭证code不能NULL")
    private String code;

    @NotEmpty(message = "微信昵称")
    private String nickname;

    @NotEmpty(message = "微信头像Base64编码")
    private String avatar;

    /**
     * 登录类型, 详情见LoginType枚举类
     */
    private Integer loginType;
}
