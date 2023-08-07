package com.toughenflat.chatai.api.openai.resp;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
public class ChatGPTResp {
    private String id;

    private String object;

    private Long created;

    /**
     * 模型名字
     */
    private String model;

    /**
     * 请求使用的Tokens
     */
    private Usage usage;

    /**
     * ChatGPT返回的结果列表, 通常只有1个返回, 因此获取数据只需choices.get(0)
     */
    private List<ChoiceMessages> choices;

    /**
     * 获取结果内容
     */
    public String getMessage() {
        if (!CollectionUtils.isEmpty(choices)) {
            return choices.get(0).getMessage().getContent();
        }
        return "";
    }
}
