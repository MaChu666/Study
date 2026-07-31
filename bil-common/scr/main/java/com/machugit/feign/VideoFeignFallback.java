package com.machugit.feign;

import com.machugit.entity.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class VideoFeignFallback implements FallbackFactory<VideoFeignClient> {
    private static final Logger log = LoggerFactory.getLogger(VideoFeignFallback.class);

    @Override
    public VideoFeignClient create(Throwable cause) {
        log.error("VideoFeignClient fallback: {}", cause.getMessage());
        return (videoId, userId) -> ResponseVO.fail("Video service unavailable");
    }
}