package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.machugit.entity.po.UserFocus;
import com.machugit.entity.query.UserFocusQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.UserFocusMapper;

import org.springframework.stereotype.Service;

import com.machugit.service.UserFocusService;


/**
 * 用户关注表 业务接口实现
 */
@Service("userFocusService")
public class UserFocusServiceImpl implements UserFocusService {

    @Resource
    private UserFocusMapper<UserFocus, UserFocusQuery> userFocusMapper;

    /**
     * 关注用户
     */
    @Override
    public void focus(String userId, String focusUserId) {
        UserFocusQuery query = new UserFocusQuery();
        query.setUserId(userId);
        query.setFocusUserId(focusUserId);
        List<UserFocus> list = this.userFocusMapper.selectList(query);
        if (list != null && !list.isEmpty()) {
            throw new BusinessException("已关注该用户");
        }
        UserFocus userFocus = new UserFocus();
        userFocus.setUserId(userId);
        userFocus.setFocusUserId(focusUserId);
        userFocus.setCreateTime(new Date());
        this.userFocusMapper.insert(userFocus);
    }

    /**
     * 取消关注
     */
    @Override
    public void cancelFocus(String userId, String focusUserId) {
        UserFocusQuery query = new UserFocusQuery();
        query.setUserId(userId);
        query.setFocusUserId(focusUserId);
        this.userFocusMapper.deleteByParam(query);
    }

    /**
     * 获取用户关注列表
     */
    @Override
    public List<UserFocus> loadFocusList(String userId) {
        UserFocusQuery query = new UserFocusQuery();
        query.setUserId(userId);
        return this.userFocusMapper.selectList(query);
    }

    /**
     * 获取用户粉丝列表
     */
    @Override
    public List<UserFocus> loadFansList(String userId) {
        UserFocusQuery query = new UserFocusQuery();
        query.setFocusUserId(userId);
        return this.userFocusMapper.selectList(query);
    }

}
