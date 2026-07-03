package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.CoinTransactionLog;

/**
 * 硬币交易日志 业务接口
 */
public interface CoinTransactionLogService {

    /**
     * 添加硬币交易记录
     */
    void addCoinTransaction(String userId, String videoId, Integer coinAmount);

    /**
     * 加载用户硬币交易记录
     */
    List<CoinTransactionLog> loadTransactions(String userId);

    /**
     * 获取今日消耗硬币数
     */
    Integer getTodayCoinCount(String userId);

}
