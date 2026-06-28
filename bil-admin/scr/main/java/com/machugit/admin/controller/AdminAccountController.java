package com.machugit.admin.controller;

import java.util.HashMap;
import java.util.Map;

import com.machugit.component.RedisComponent;
import com.machugit.entity.constants.Constants;
import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.UserInfoServiceImpl;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/account")
@Validated
public class AdminAccountController extends ABaseAdminController {

    @Resource
    private UserInfoServiceImpl userInfoService;

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping("/checkCode")
    public ResponseVO checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 40);
        String code = captcha.text();
        String checkCodeKey = redisComponent.saveChackCode(code);
        String checkCodeBase64 = captcha.toBase64();

        Map<String, String> checkmap = new HashMap<>();
        checkmap.put("checkCode", checkCodeBase64);
        checkmap.put("checkCodeKey", checkCodeKey);
        return getSuccessResponseVO(checkmap);
    }

    @RequestMapping("/login")
    public ResponseVO login(HttpServletRequest request,
                            HttpServletResponse response,
                            @NotEmpty String email,
                            @NotEmpty String password,
                            @NotEmpty String checkCodeKey,
                            @NotEmpty String checkCode) {
        try {
            String codeFromRedis = redisComponent.getCheckCode(checkCodeKey);
            if (codeFromRedis == null || !checkCode.trim().equalsIgnoreCase(codeFromRedis)) {
                throw new BusinessException("验证码错误");
            }
            String ip = getIpAddr();
            TokenUserInfoDto tokenUserInfoDto = userInfoService.login(email, password, ip);
            if (!Constants.isAdmin(tokenUserInfoDto.getUserId())) {
                redisComponent.cleanTokenInfo(tokenUserInfoDto.getToken());
                throw new BusinessException("非管理员账号，无法登录后台");
            }
            saveToken2Cookie(response, tokenUserInfoDto.getToken());
            return getSuccessResponseVO(tokenUserInfoDto);
        } finally {
            redisComponent.clearCheckCode(checkCodeKey);
        }
    }

    @RequestMapping("/logout")
    public ResponseVO logout(HttpServletResponse response) {
        removeTokenFromCookie(response);
        return getSuccessResponseVO(null);
    }
}
