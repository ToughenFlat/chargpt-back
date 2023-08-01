package com.toughenflat.chatai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.toughenflat.chatai.dto.PromptQueryRequest;
import com.toughenflat.chatai.entity.PromptEntity;
import com.toughenflat.chatai.enums.PromptType;

import java.util.List;

/**
 * @Author: huangpenglong
 * @Date: 2023/4/11 16:38
 */
public interface PromptService extends IService<PromptEntity> {
    /**
     * 查询prompt列表
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    IPage<PromptEntity> list(int page, int limit, PromptQueryRequest hospitalQueryVo);

    /**
     * 根据 `主题` 获取提示
     * 实现：缓存
     * @param topic
     * @return
     */
    String getByTopic(String topic);

    /**
     * 根据枚举类`PromptType`获取提示列表
     * @param promptType
     * @return
     */
    List<PromptEntity> getByType(PromptType promptType);

    /**
     * 加载缓存
     */
    void load();
}
