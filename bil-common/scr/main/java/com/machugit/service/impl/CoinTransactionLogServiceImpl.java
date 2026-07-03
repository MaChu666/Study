package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.entity.po.CoinTransactionLog;
import com.machugit.entity.query.CoinTransactionLogQuery;
import com.machugit.mappers.CoinTransactionLogMapper;
import com.machugit.service.CoinTransactionLogService;
import com.machugit.utils.DateUtil;

/**
 * 硬币交易日志 业务接口实现
 */
@Service("coinTransactionLogService")
public class CoinTransactionLogServiceImpl implements CoinTransactionLogService {

    @Resource
    private CoinTransactionLogMapper<CoinTransactionLog, CoinTransactionLogQuery> coinTransactionLogMapper;

    /**
     * 添加硬币交易记录
     */
    @Override
    public void addCoinTransaction(String userId, String videoId, Integer coinAmount) {
        CoinTransactionLog log = new CoinTransactionLog();
        log.setUserId(userId);
        log.setVideoId(videoId);
        log.setCoinAmount(coinAmount);
        log.setCreateTime(new Date());
        this.coinTransactionLogMapper.insert(log);
    }

    /**
     * 加载用户硬币交易记录
     */
    @Override
    public List<CoinTransactionLog> loadTransactions(String userId) {
        CoinTransactionLogQuery query = new CoinTransactionLogQuery();
        query.setUserId(userId);
        query.setOrderBy("create_time desc");
        return this.coinTransactionLogMapper.selectList(query);
    }

    /**
     * 获取今日消耗硬币数
     */
    @Override
    public Integer getTodayCoinCount(String userId) {
        String today = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        CoinTransactionLogQuery query = new CoinTransactionLogQuery();
        query.setUserId(userId);
        query.setCreateTime(today);
        Long count = this.coinTransactionLogMapper.selectCount(query).longValue();
        return count.intValue();
    }

}
