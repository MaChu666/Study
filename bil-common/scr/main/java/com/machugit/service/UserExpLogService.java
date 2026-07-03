package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.UserExpLog;

/**
 * 用户经验日志 业务接口
 */
public interface UserExpLogService {

    /**
     * 加载用户经验日志
     */
    List<UserExpLog> loadExpLog(String userId);

    /**
     * 添加经验值
     */
    void addExp(String userId, Integer expAmount, Integer sourceType, String sourceId);

}
