package com.toughenflat.chatai;

import com.toughenflat.chatai.api.openai.enums.Role;
import com.toughenflat.chatai.api.openai.ChatGPTApi;
import com.toughenflat.chatai.api.openai.req.ChatGPTReq;
import com.toughenflat.chatai.api.openai.req.ContextMessage;
import com.toughenflat.chatai.api.openai.resp.ChatGPTResp;
import com.toughenflat.chatai.api.openai.resp.EmbeddingResp;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;

/**
 * @Author: huangpenglong
 * @Date: 2023/3/9 11:15
 */

@SpringBootTest
public class TestChatGPT {

    @Test
    public void testOneShot(){

        ChatGPTResp resp = ChatGPTApi.sessionReq(
                ChatGPTReq.builder().messages(Arrays.asList(new ContextMessage(Role.USER.name, "你好"))).build(),
                "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");

        System.out.println(resp.getChoices().get(0).getMessage().toString());
    }

    @Test
    public void testEmbed(){

        EmbeddingResp embeddingResp = ChatGPTApi.embeddings(Arrays.asList("this is a", "this is bb"), "");

        System.out.println("test embed");
    }

    @Test
    public void testExpertToken() {
        int tokenNum = ChatGPTApi.getTokenNum("用英文回答");
        System.out.println(tokenNum);
    }
}
