package com.toughenflat.chatai.controller;

import com.google.common.collect.ImmutableList;
import com.toughenflat.chatai.api.openai.constant.OpenAIConst;
import com.toughenflat.chatai.api.openai.enums.Role;
import com.toughenflat.chatai.api.openai.req.ChatGPTReq;
import com.toughenflat.chatai.api.openai.req.ContextMessage;
import com.toughenflat.chatai.api.openai.resp.ChatGPTResp;
import com.toughenflat.chatai.dto.TranslationRequest;
import com.toughenflat.chatai.entity.UserApiKeyEntity;
import com.toughenflat.chatai.enums.ApiType;
import com.toughenflat.chatai.enums.Prompt;
import com.toughenflat.chatai.service.AdminApiKeyService;
import com.toughenflat.chatai.service.ChatService;
import com.toughenflat.chatai.service.PromptService;
import com.toughenflat.chatai.service.UserApiKeyService;
import com.toughenflat.chatai.utils.ResultCode;
import com.toughenflat.chatai.utils.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/translation")
public class TranslationController {
    private static final String NAME_MESSAGE = "translateResult";

    @Resource
    private ChatService chatService;
    @Resource
    private AdminApiKeyService adminApiKeyService;
    @Resource
    private PromptService promptService;
    @Resource
    private UserApiKeyService userApiKeyService;

    /**
     * 翻译
     *
     * @param req
     * @return
     */
    @PostMapping("/translate")
    public ReturnResult translate(@RequestBody @Valid TranslationRequest req) {
        if (StringUtils.isEmpty(req.getMessage()) || req.getUserId() == null) {
            return ReturnResult.error().codeAndMessage(ResultCode.EMPTY_PARAM);
        }

        String reqMsg = String.format(promptService.getByTopic(Prompt.TRANSLATE.topic), req.getLanguage(), req.getMessage());

        // 若用户上传了apikey则使用用户的，否则采用本系统的
        UserApiKeyEntity userApiKeyEntity = userApiKeyService.getByUserIdAndType(req.getUserId(), ApiType.OPENAI);
        String apiKey = userApiKeyEntity != null && !StringUtils.isEmpty(userApiKeyEntity.getApikey())
                ? userApiKeyEntity.getApikey()
                : adminApiKeyService.roundRobinGetByType(ApiType.OPENAI);
        if (apiKey == null) {
            return ReturnResult.error().codeAndMessage(ResultCode.ADMIN_APIKEY_NULL);
        }

        // 调用对话接口
        ChatGPTReq gptReq = ChatGPTReq.builder()
                .messages(ImmutableList.of(new ContextMessage(Role.USER.name, reqMsg)))
                .model(OpenAIConst.MODEL_NAME_CHATGPT_3_5)
                .build();

        ChatGPTResp resp = chatService.oneShotChat(req.getUserId(), gptReq, apiKey);

        if (resp.getMessage() == null) {
            return ReturnResult.error();
        }
        return ReturnResult.ok().data(NAME_MESSAGE, resp.getMessage());
    }

}
