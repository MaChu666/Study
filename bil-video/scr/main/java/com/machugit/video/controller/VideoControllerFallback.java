package com.machugit.video.controller;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.machugit.entity.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoControllerFallback {
    private static final Logger log = LoggerFactory.getLogger(VideoControllerFallback.class);
    public static ResponseVO getFeedBlockHandler(Integer pageNo, Integer pageSize, BlockException e) {
        log.warn("getFeed blocked by Sentinel: {}", e.getMessage());
        return ResponseVO.fail("System is busy, please try again later");
    }
}