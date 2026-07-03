package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.DynamicLike;
import com.machugit.entity.po.UserDynamic;
import com.machugit.entity.query.DynamicLikeQuery;
import com.machugit.entity.query.UserDynamicQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.DynamicLikeMapper;
import com.machugit.mappers.UserDynamicMapper;
import com.machugit.service.DynamicLikeService;

/**
 * 动态点赞 业务接口实现
 */
@Service("dynamicLikeService")
public class DynamicLikeServiceImpl implements DynamicLikeService {

    @Resource
    private DynamicLikeMapper<DynamicLike, DynamicLikeQuery> dynamicLikeMapper;

    @Resource
    private UserDynamicMapper<UserDynamic, UserDynamicQuery> userDynamicMapper;

    /**
     * 点赞
     */
    @Override
    public void like(Integer dynamicId, String userId) {
        DynamicLikeQuery checkQuery = new DynamicLikeQuery();
        checkQuery.setDynamicId(dynamicId);
        checkQuery.setUserId(userId);
        List<DynamicLike> existingList = this.dynamicLikeMapper.selectList(checkQuery);
        if (existingList != null && !existingList.isEmpty()) {
            throw new BusinessException("已点赞");
        }

        DynamicLike like = new DynamicLike();
        like.setDynamicId(dynamicId);
        like.setUserId(userId);
        like.setCreateTime(new Date());
        this.dynamicLikeMapper.insert(like);

        UserDynamic dynamic = this.userDynamicMapper.selectByDynamicId(dynamicId);
        if (dynamic != null) {
            UserDynamic updateDynamic = new UserDynamic();
            Integer currentLikeCount = dynamic.getLikeCount();
            if (currentLikeCount == null) {
                currentLikeCount = 0;
            }
            updateDynamic.setLikeCount(currentLikeCount + 1);
            this.userDynamicMapper.updateByDynamicId(updateDynamic, dynamicId);
        }
    }

    /**
     * 取消点赞
     */
    @Override
    public void unlike(Integer dynamicId, String userId) {
        DynamicLikeQuery query = new DynamicLikeQuery();
        query.setDynamicId(dynamicId);
        query.setUserId(userId);
        this.dynamicLikeMapper.deleteByParam(query);

        UserDynamic dynamic = this.userDynamicMapper.selectByDynamicId(dynamicId);
        if (dynamic != null) {
            UserDynamic updateDynamic = new UserDynamic();
            Integer currentLikeCount = dynamic.getLikeCount();
            if (currentLikeCount != null && currentLikeCount > 0) {
                updateDynamic.setLikeCount(currentLikeCount - 1);
            }
            this.userDynamicMapper.updateByDynamicId(updateDynamic, dynamicId);
        }
    }

    /**
     * 是否已点赞
     */
    @Override
    public Boolean isLiked(Integer dynamicId, String userId) {
        DynamicLikeQuery query = new DynamicLikeQuery();
        query.setDynamicId(dynamicId);
        query.setUserId(userId);
        List<DynamicLike> list = this.dynamicLikeMapper.selectList(query);
        return list != null && !list.isEmpty();
    }

}
