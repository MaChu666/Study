package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.UserCollection;


/**
 * 用户收藏表 业务接口
 */
public interface UserCollectionService {

    /**
     * 获取用户收藏列表
     */
    List<UserCollection> loadUserCollection(String userId);

    /**
     * 收藏视频
     */
    void collect(String userId, String videoId);

    /**
     * 取消收藏
     */
    void cancelCollect(String userId, String videoId);

}
