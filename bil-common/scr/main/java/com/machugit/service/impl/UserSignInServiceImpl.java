package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.entity.po.UserSignIn;
import com.machugit.entity.query.UserSignInQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.UserSignInMapper;
import com.machugit.service.UserSignInService;
import com.machugit.utils.DateUtil;

/**
 * 用户签到 业务接口实现
 */
@Service("userSignInService")
public class UserSignInServiceImpl implements UserSignInService {

    @Resource
    private UserSignInMapper<UserSignIn, UserSignInQuery> userSignInMapper;

    /**
     * 签到
     */
    @Override
    public void signIn(String userId) {
        String today = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());

        UserSignInQuery checkQuery = new UserSignInQuery();
        checkQuery.setUserId(userId);
        checkQuery.setSignDate(today);
        List<UserSignIn> todayList = this.userSignInMapper.selectList(checkQuery);
        if (todayList != null && !todayList.isEmpty()) {
            throw new BusinessException("今日已签到");
        }

        Integer prevContinuousDays = 0;
        UserSignInQuery prevQuery = new UserSignInQuery();
        prevQuery.setUserId(userId);
        prevQuery.setOrderBy("sign_date desc");
        List<UserSignIn> prevList = this.userSignInMapper.selectList(prevQuery);
        if (prevList != null && !prevList.isEmpty()) {
            prevContinuousDays = prevList.get(0).getContinuousDays();
        }

        UserSignIn signIn = new UserSignIn();
        signIn.setUserId(userId);
        signIn.setSignDate(new Date());
        signIn.setContinuousDays(prevContinuousDays + 1);
        signIn.setCreateTime(new Date());
        this.userSignInMapper.insert(signIn);
    }

    /**
     * 查询今日签到记录
     */
    @Override
    public UserSignIn getTodaySign(String userId) {
        String today = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        UserSignInQuery query = new UserSignInQuery();
        query.setUserId(userId);
        query.setSignDate(today);
        List<UserSignIn> list = this.userSignInMapper.selectList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取连续签到天数
     */
    @Override
    public Integer getContinuousDays(String userId) {
        UserSignIn todaySign = getTodaySign(userId);
        if (todaySign != null) {
            return todaySign.getContinuousDays();
        }
        return 0;
    }

}
