package com.toughenflat.chatai.config;

import com.toughenflat.chatai.interceptor.*;
import com.toughenflat.chatai.redis.ChatRedisHelper;
import com.toughenflat.chatai.service.UserApiKeyService;
import com.toughenflat.chatai.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ChatRedisHelper chatRedisHelper;

    @Resource
    private UserApiKeyService userApiKeyService;

    @Resource
    private UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 携带token认证, 拦截除登录、注册、发送验证码、重置密码的所有请求
        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/user/sendCode",
                        "/user/resetPwd"
                );

        // 接口防刷
        registry.addInterceptor(new AccessLimitInterceptor(this.redisTemplate))
                .addPathPatterns("/user/sendCode");

        // 聊天功能次数限制
        registry.addInterceptor(new UserChatLimitInterceptor(this.chatRedisHelper, this.userApiKeyService, this.userService))
                .addPathPatterns(
                        "/translation/translate",
                        "/chat/game/startGameSession",
                        "/chat/session",
                        "/chat/oneShot",
                        "/chat/streamSessionChat",
                        "/chat/streamOneShotChat",
                        "/file/chatWithFile",
                        "/file/streamChatWithFile"
                );

        // 文件上传功能次数限制
        registry.addInterceptor(new UserFileUploadLimitInterceptor(this.chatRedisHelper, this.userApiKeyService, this.userService))
                .addPathPatterns("/file/chatPdfUpload");

        // 管理员权限接口禁用
        registry.addInterceptor(new AdminOperateInterceptor())
                .addPathPatterns("/**/admin/**");
    }
}
