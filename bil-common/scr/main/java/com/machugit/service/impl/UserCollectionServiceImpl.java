package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.machugit.entity.po.UserCollection;
import com.machugit.entity.query.UserCollectionQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.UserCollectionMapper;

import org.springframework.stereotype.Service;

import com.machugit.service.UserCollectionService;


/**
 * 用户收藏表 业务接口实现
 */
@Service("userCollectionService")
public class UserCollectionServiceImpl implements UserCollectionService {

    @Resource
    private UserCollectionMapper<UserCollection, UserCollectionQuery> userCollectionMapper;

    /**
     * 获取用户收藏列表
     */
    @Override
    public List<UserCollection> loadUserCollection(String userId) {
        UserCollectionQuery query = new UserCollectionQuery();
        query.setUserId(userId);
        return this.userCollectionMapper.selectList(query);
    }

    /**
     * 收藏视频
     */
    @Override
    public void collect(String userId, String videoId) {
        UserCollection userCollection = new UserCollection();
        userCollection.setUserId(userId);
        userCollection.setVideoId(videoId);
        userCollection.setCreateTime(new Date());
        this.userCollectionMapper.insert(userCollection);
    }

    /**
     * 取消收藏
     */
    @Override
    public void cancelCollect(String userId, String videoId) {
        UserCollectionQuery query = new UserCollectionQuery();
        query.setUserId(userId);
        query.setVideoId(videoId);
        this.userCollectionMapper.deleteByParam(query);
    }

}
