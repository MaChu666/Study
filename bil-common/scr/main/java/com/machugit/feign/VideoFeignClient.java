package com.machugit.feign;

import com.machugit.entity.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bil-video", path = "/video", fallbackFactory = VideoFeignFallback.class)
public interface VideoFeignClient {
    @GetMapping("/getVideoDetail")
    ResponseVO getVideoDetail(@RequestParam("videoId") String videoId, @RequestParam(value = "userId", required = false) String userId);
}