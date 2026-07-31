package com.machugit.video.rocketmq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class VideoEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(VideoEventPublisher.class);
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void publishVideoUploadEvent(String videoId, String userId) {
        String payload = videoId + ":" + userId;
        Message msg = MessageBuilder.withPayload(payload).build();
        rocketMQTemplate.send("bil-video-upload", msg);
        log.info("Published upload: {}", videoId);
    }
}