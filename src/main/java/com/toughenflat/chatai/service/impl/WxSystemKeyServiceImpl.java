package com.toughenflat.chatai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.toughenflat.chatai.entity.WxSystemKeyEntity;
import com.toughenflat.chatai.mapper.WxSystemKeyMapper;
import com.toughenflat.chatai.service.WxSystemKeyService;
import org.springframework.stereotype.Service;

@Service
public class WxSystemKeyServiceImpl extends ServiceImpl<WxSystemKeyMapper, WxSystemKeyEntity> implements WxSystemKeyService {
    @Override
    public WxSystemKeyEntity findAll() {
        return baseMapper.selectById(1);
    }
}
