package com.machugit.feign;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Auth 服务 Feign 接口
 * 其他服务通过此接口调用 bil-auth 验证 Token
 */
@FeignClient(name = "bil-auth", path = "/auth")
public interface AuthFeignClient {

    /**
     * 校验 Token
     * @param token 用户 token
     * @return TokenUserInfoDto（null 表示无效 token）
     */
    @PostMapping("/verify")
    ResponseVO verifyToken(@RequestParam("token") String token);
}