package com.machugit.admin.controller;

import java.util.HashMap;
import java.util.Map;

import com.machugit.component.RedisComponent;
import com.machugit.entity.config.AdminConfig;
import com.machugit.entity.constants.Constants;
import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.utils.StringTools;
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
    private RedisComponent redisComponent;

    @Resource
    private AdminConfig adminConfig;

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
                            @NotEmpty String account,
                            @NotEmpty String password,
                            @NotEmpty String checkCodeKey,
                            @NotEmpty String checkCode) {
        try {
            String codeFromRedis = redisComponent.getCheckCode(checkCodeKey);
            if (codeFromRedis == null || !checkCode.trim().equalsIgnoreCase(codeFromRedis)) {
                throw new BusinessException("验证码错误");
            }
            if (!adminConfig.getAccount().equals(account) || !StringTools.encodeByMd5(adminConfig.getPassword()).equals(password)) {
                throw new BusinessException("账号或密码错误");
            }
            String token = redisComponent.saveAdminTokenInfo(account);
            saveToken2Cookie(response, token);
            Map<String, String> result = new HashMap<>();
            result.put("token", token);
            result.put("account", account);
            return getSuccessResponseVO(result);
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
