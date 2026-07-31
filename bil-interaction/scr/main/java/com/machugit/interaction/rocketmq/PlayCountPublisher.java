package com.machugit.interaction.rocketmq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class PlayCountPublisher {
    private static final Logger log = LoggerFactory.getLogger(PlayCountPublisher.class);
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void publishPlayCount(String videoId) {
        Message msg = MessageBuilder.withPayload(videoId).build();
        rocketMQTemplate.send("bil-play-count", msg);
        log.info("Play count event published: videoId={}", videoId);
    }
}