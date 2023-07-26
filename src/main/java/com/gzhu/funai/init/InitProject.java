package com.gzhu.funai.init;

import com.gzhu.funai.api.openai.ChatGPTApi;
import com.gzhu.funai.api.openai.enums.Role;
import com.gzhu.funai.api.openai.req.ChatGPTReq;
import com.gzhu.funai.api.openai.req.ContextMessage;
import com.gzhu.funai.api.openai.resp.ChatGPTResp;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class InitProject implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ChatGPTResp resp = ChatGPTApi.sessionReq(
                ChatGPTReq.builder().messages(Arrays.asList(new ContextMessage(Role.USER.name, "请问如何评价秦始皇?"))).build(),
                "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");

        //System.out.println(resp.getChoices().get(0).getMessage().toString());
        System.out.println("初始化项目");
    }
}
