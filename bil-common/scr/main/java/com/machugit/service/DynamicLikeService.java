package com.machugit.service;

/**
 * 动态点赞 业务接口
 */
public interface DynamicLikeService {

    /**
     * 点赞
     */
    void like(Integer dynamicId, String userId);

    /**
     * 取消点赞
     */
    void unlike(Integer dynamicId, String userId);

    /**
     * 是否已点赞
     */
    Boolean isLiked(Integer dynamicId, String userId);

}
