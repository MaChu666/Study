package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.UserFocus;


/**
 * 用户关注表 业务接口
 */
public interface UserFocusService {

    /**
     * 关注用户
     */
    void focus(String userId, String focusUserId);

    /**
     * 取消关注
     */
    void cancelFocus(String userId, String focusUserId);

    /**
     * 获取用户关注列表
     */
    List<UserFocus> loadFocusList(String userId);

    /**
     * 获取用户粉丝列表
     */
    List<UserFocus> loadFansList(String userId);

}
