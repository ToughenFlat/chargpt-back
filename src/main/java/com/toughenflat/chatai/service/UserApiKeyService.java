package com.toughenflat.chatai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.toughenflat.chatai.entity.UserApiKeyEntity;
import com.toughenflat.chatai.enums.ApiType;

public interface UserApiKeyService extends IService<UserApiKeyEntity> {

    /**
     * 获取用户的ApiKey
     * @param userId
     * @param type
     * @return
     */
    UserApiKeyEntity getByUserIdAndType(String userId, ApiType type);

    /**
     * 根据唯一键 userId 和 type来决定插入还是更新数据
     * @param userApiKeyEntity
     */
    void insertOrUpdate(UserApiKeyEntity userApiKeyEntity);
}
