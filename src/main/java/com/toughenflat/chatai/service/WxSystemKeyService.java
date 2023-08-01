package com.toughenflat.chatai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.toughenflat.chatai.entity.WxSystemKeyEntity;

public interface WxSystemKeyService extends IService<WxSystemKeyEntity> {
    /**
     * 获取微信小程序的系统Key信息
     * @return
     */
    WxSystemKeyEntity findAll();
}
