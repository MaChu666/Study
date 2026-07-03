package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.UserExpLog;
import com.machugit.entity.po.UserInfo;
import com.machugit.entity.query.UserExpLogQuery;
import com.machugit.entity.query.UserInfoQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.UserExpLogMapper;
import com.machugit.mappers.UserInfoMapper;
import com.machugit.service.UserExpLogService;
import com.machugit.utils.StringTools;

/**
 * 用户经验日志 业务接口实现
 */
@Service("userExpLogService")
public class UserExpLogServiceImpl implements UserExpLogService {

    // 升级所需经验: Lv1→Lv2需100, Lv2→Lv3需500, Lv3→Lv4需1500, Lv4→Lv5需5000, Lv5→Lv6需15000
    private static final int[] LEVEL_UP_EXP = {0, 100, 500, 1500, 5000, 15000};
    private static final int LV6_MAX_EXP = 15000;
    private static final int DAILY_LIKE_LIMIT = 5;
    private static final int DAILY_COIN_LIMIT = 5;

    @Resource
    private UserExpLogMapper<UserExpLog, UserExpLogQuery> userExpLogMapper;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Override
    public List<UserExpLog> loadExpLog(String userId) {
        UserExpLogQuery query = new UserExpLogQuery();
        query.setUserId(userId);
        query.setOrderBy("create_time desc");
        return this.userExpLogMapper.selectList(query);
    }

    private int countTodayBySource(String userId, Integer sourceType) {
        UserExpLogQuery query = new UserExpLogQuery();
        query.setUserId(userId);
        query.setSourceType(sourceType);
        List<UserExpLog> list = this.userExpLogMapper.selectList(query);
        java.util.Calendar today = java.util.Calendar.getInstance();
        int count = 0;
        if (list != null) {
            for (UserExpLog log : list) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(log.getCreateTime());
                if (cal.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR)
                    && cal.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void addExp(String userId, Integer expAmount, Integer sourceType, String sourceId) {
        // 每日点赞/投币经验上限
        if (sourceType == 2) {
            int todayLikeCount = countTodayBySource(userId, 2);
            if (todayLikeCount >= DAILY_LIKE_LIMIT) return;
        }

        UserInfo userInfo = this.userInfoMapper.selectByUserId(userId);
        if (userInfo == null) return;

        // Lv6 不再增加经验
        Integer currentLevel = userInfo.getLevel();
        if (currentLevel != null && currentLevel >= 6) return;

        UserExpLog expLog = new UserExpLog();
        expLog.setUserId(userId);
        expLog.setExpAmount(expAmount);
        expLog.setSourceType(sourceType);
        expLog.setSourceId(sourceId);
        expLog.setCreateTime(new Date());
        this.userExpLogMapper.insert(expLog);

        Integer currentExp = userInfo.getExp();
        if (currentExp == null) currentExp = 0;
        Integer newExp = currentExp + expAmount;

        // 达到 Lv6 上限后不再增加
        if (newExp > LV6_MAX_EXP) newExp = LV6_MAX_EXP;

        // 计算新等级: exp达到哪一级的升级线就升到那一级
        Integer newLevel = 1;
        for (int i = LEVEL_UP_EXP.length - 1; i > 0; i--) {
            if (newExp >= LEVEL_UP_EXP[i]) { newLevel = i + 1; break; }
        }

        UserInfo updateInfo = new UserInfo();
        updateInfo.setExp(newExp);
        updateInfo.setLevel(newLevel);
        this.userInfoMapper.updateByUserId(updateInfo, userId);
    }

}
