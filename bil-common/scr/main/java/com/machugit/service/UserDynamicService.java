package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.UserDynamic;

/**
 * 用户动态 业务接口
 */
public interface UserDynamicService {

    /**
     * 分页加载用户动态
     */
    List<UserDynamic> loadDynamics(String userId, Integer pageNo);

    /**
     * 发布动态
     */
    void postDynamic(UserDynamic dynamic);

    /**
     * 删除动态
     */
    void deleteDynamic(Integer dynamicId);

}
