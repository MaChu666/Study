package com.machugit.service;

import com.machugit.entity.po.UserSignIn;

/**
 * 用户签到 业务接口
 */
public interface UserSignInService {

    /**
     * 签到
     */
    void signIn(String userId);

    /**
     * 查询今日签到记录
     */
    UserSignIn getTodaySign(String userId);

    /**
     * 获取连续签到天数
     */
    Integer getContinuousDays(String userId);

}
