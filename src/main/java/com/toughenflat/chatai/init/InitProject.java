package com.toughenflat.chatai.init;

import com.toughenflat.chatai.api.openai.ChatGPTApi;
import com.toughenflat.chatai.api.openai.enums.Role;
import com.toughenflat.chatai.api.openai.req.ChatGPTReq;
import com.toughenflat.chatai.api.openai.req.ContextMessage;
import com.toughenflat.chatai.api.openai.resp.ChatGPTResp;
import com.toughenflat.chatai.entity.UserApiKeyEntity;
import com.toughenflat.chatai.enums.ApiType;
import com.toughenflat.chatai.service.UserApiKeyService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
public class InitProject implements ApplicationRunner {
    @Resource
    private UserApiKeyService userApiKeyService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        UserApiKeyEntity userApiKeyEntity = userApiKeyService.getByUserIdAndType("1", ApiType.OPENAI);
        ChatGPTResp resp = ChatGPTApi.sessionReq(
                ChatGPTReq.builder().messages(Arrays.asList(new ContextMessage(Role.USER.name, "请问如何评价秦始皇?"))).build(),
                userApiKeyEntity.getApikey());
        if (resp != null) {
            System.out.println(resp.getChoices().get(0).getMessage().toString());
        }
        System.out.println("初始化项目");
    }
}
