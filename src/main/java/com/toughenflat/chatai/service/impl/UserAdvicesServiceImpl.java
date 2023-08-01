package com.toughenflat.chatai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.toughenflat.chatai.entity.UserAdvicesEntity;
import com.toughenflat.chatai.mapper.UserAdvicesMapper;
import com.toughenflat.chatai.service.UserAdvicesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserAdvicesServiceImpl extends ServiceImpl<UserAdvicesMapper, UserAdvicesEntity>
        implements UserAdvicesService {
}
