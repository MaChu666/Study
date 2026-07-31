package com.machugit.interaction.rocketmq;

import com.machugit.service.VideoPlayHistoryService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@RocketMQMessageListener(topic = "bil-play-count", consumerGroup = "play-count-group")
public class PlayCountConsumer implements RocketMQListener<String> {
    private static final Logger log = LoggerFactory.getLogger(PlayCountConsumer.class);
    private static final String IDEM_KEY = "idem:play:";

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private VideoPlayHistoryService videoPlayHistoryService;

    @Override
    public void onMessage(String videoId) {
        String idempotentKey = IDEM_KEY + videoId;
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(idempotentKey, "1", 10, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(ok)) {
            videoPlayHistoryService.addPlayCount(videoId);
            log.info("Play count processed: {}", videoId);
        } else {
            log.debug("Duplicate play msg: {}", videoId);
        }
    }
}