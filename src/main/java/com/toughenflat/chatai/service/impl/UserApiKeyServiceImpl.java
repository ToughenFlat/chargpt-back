package com.toughenflat.chatai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.toughenflat.chatai.entity.UserApiKeyEntity;
import com.toughenflat.chatai.enums.ApiType;
import com.toughenflat.chatai.mapper.UserApiKeyMapper;
import com.toughenflat.chatai.service.UserApiKeyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: huangpenglong
 * @Date: 2023/4/20 19:47
 */

@Service
public class UserApiKeyServiceImpl extends ServiceImpl<UserApiKeyMapper, UserApiKeyEntity> implements UserApiKeyService {

    @Resource
    private UserApiKeyMapper userApiKeyMapper;

    @Override
    public UserApiKeyEntity getByUserIdAndType(String userId, ApiType type) {

        return baseMapper.selectOne(new QueryWrapper<UserApiKeyEntity>()
                .eq("user_id", userId)
                .eq("type", type.typeNo));
    }

    @Override
    public void insertOrUpdate(UserApiKeyEntity userApiKeyEntity) {
        userApiKeyMapper.insertOrUpdate(userApiKeyEntity.getUserId(), userApiKeyEntity.getType(), userApiKeyEntity.getApikey());
    }
}
