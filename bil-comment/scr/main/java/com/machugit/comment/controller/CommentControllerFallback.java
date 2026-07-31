package com.machugit.comment.controller;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.machugit.entity.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentControllerFallback {
    private static final Logger log = LoggerFactory.getLogger(CommentControllerFallback.class);
    public static ResponseVO postCommentBlockHandler(String videoId, String userId, String content, String replyId, BlockException e) {
        log.warn("postComment blocked for video {}: {}", videoId, e.getMessage());
        return ResponseVO.fail("Comment too frequent, please try later");
    }
}