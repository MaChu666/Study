package com.machugit.interaction.service.impl;

import com.machugit.interaction.rocketmq.PlayCountPublisher;
import com.machugit.interaction.service.InteractionService;
import io.seata.spring.annotation.GlobalTransactional;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class InteractionServiceImpl implements InteractionService {
    private static final Logger log = LoggerFactory.getLogger(InteractionServiceImpl.class);

    @Resource
    private com.machugit.service.VideoInfoService videoInfoService;

    @Resource
    private com.machugit.service.UserInfoService userInfoService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private PlayCountPublisher playCountPublisher;

    @Resource
    private com.machugit.service.DynamicLikeService dynamicLikeService;

    @Override
    @GlobalTransactional(name = "addCoin-tx", rollbackFor = Exception.class)
    public void addCoin(String userId, String videoId, Integer count) {
        String lockKey = "user:coin:lock:" + userId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                try {
                    videoInfoService.addCoinCount(videoId, count);
                    userInfoService.deductCoin(userId, count);
                    log.info("addCoin success: userId={}, videoId={}, count={}", userId, videoId, count);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to acquire lock for addCoin", e);
        }
    }

    @Override
    public void recordPlay(String videoId) {
        playCountPublisher.publishPlayCount(videoId);
    }

    @Override
    public void like(String userId, String videoId) {
        com.machugit.service.DynamicLikeService likeService = dynamicLikeService;
        if (likeService != null) {
            likeService.like(Integer.parseInt(videoId), userId);
        }
    }
}