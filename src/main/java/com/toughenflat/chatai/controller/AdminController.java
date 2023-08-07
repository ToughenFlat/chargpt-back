package com.toughenflat.chatai.controller;

import com.toughenflat.chatai.service.AdminApiKeyService;
import com.toughenflat.chatai.utils.ReturnResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Resource
    private AdminApiKeyService adminApiKeyService;

    /**
     * 刷新系统用的ApiKey缓存
     * @return
     */
    @GetMapping("/flushApiKey")
    public ReturnResult flushApiKey(){
        adminApiKeyService.load();
        return ReturnResult.ok();
    }
}
