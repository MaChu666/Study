package com.machugit.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.machugit.entity.vo.ResponseVO;

/**
 * Sentinel 限流降级处理类
 * 被 @SentinelResource 的 blockHandler / fallback 引用
 */
public class SentinelBlockHandler {

    /**
     * 通用限流处理
     */
    public static ResponseVO handleBlock(String msg, BlockException e) {
        ResponseVO vo = new ResponseVO();
        vo.setStatus("error");
        vo.setCode(429);
        vo.setInfo("请求太频繁，请稍后重试");
        return vo;
    }

    /**
     * 通用降级处理
     */
    public static ResponseVO handleFallback(String msg, Throwable e) {
        ResponseVO vo = new ResponseVO();
        vo.setStatus("error");
        vo.setCode(500);
        vo.setInfo("服务繁忙，请稍后重试");
        return vo;
    }
}