package com.machugit.auth.controller;

import com.machugit.component.RedisComponent;
import com.machugit.entity.constants.Constants;
import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.query.UserInfoQuery;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.UserInfoServiceImpl;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.machugit.sentinel.SentinelBlockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController extends AuthBaseController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Resource
    private UserInfoServiceImpl userInfoService;

    @Resource
    private RedisComponent redisComponent;

    private static final Random RANDOM;

    static {
        try {
            RANDOM = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get captcha code (Java 21 compatible, no Nashorn ScriptEngine)
     */
    @RequestMapping("/checkCode")
    @SentinelResource(value = "auth:checkCode", blockHandlerClass = SentinelBlockHandler.class, blockHandler = "handleBlock")
    public ResponseVO checkCode() {
        try {
            int a = RANDOM.nextInt(50) + 1;
            int b = RANDOM.nextInt(50) + 1;
            int op = RANDOM.nextInt(2);
            String expr;
            int result;
            if (op == 0) {
                expr = a + " + " + b + " = ?";
                result = a + b;
            } else {
                if (a < b) { int t = a; a = b; b = t; }
                expr = a + " - " + b + " = ?";
                result = a - b;
            }

            int width = 100, height = 40;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 5; i++) {
                int x1 = RANDOM.nextInt(width);
                int y1 = RANDOM.nextInt(height);
                int x2 = RANDOM.nextInt(width);
                int y2 = RANDOM.nextInt(height);
                g.drawLine(x1, y1, x2, y2);
            }

            g.setColor(new Color(50 + RANDOM.nextInt(100), 50 + RANDOM.nextInt(100), 50 + RANDOM.nextInt(100)));
            g.setFont(new Font("Arial", Font.BOLD, 22));
            String displayText = a + (op == 0 ? "+" : "-") + b + "=?";
            g.drawString(displayText, 8, 28);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            String checkCodeBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);

            String code = String.valueOf(result);
            String checkCodeKey = redisComponent.saveChackCode(code);

            Map<String, String> map = new HashMap<>();
            map.put("checkCode", checkCodeBase64);
            map.put("checkCodeKey", checkCodeKey);
            return getSuccessResponseVO(map);
        } catch (Exception e) {
            logger.error("Generate captcha failed", e);
            throw new BusinessException("Captcha generation failed");
        }
    }

    @RequestMapping("/register")
    @SentinelResource(value = "auth:register", blockHandlerClass = SentinelBlockHandler.class, blockHandler = "handleBlock")
    public ResponseVO register(
            @NotEmpty @Email @Size(max = 150) String email,
            @NotEmpty @Size(max = 20) String useName,
            @NotEmpty @Size(min = 6, max = 32)
            @Pattern(regexp = Constants.REGEX_PASSWORD, message = "Password must contain letters and numbers, length 6-20")
            String registerPassword,
            @NotEmpty String checkCodeKey,
            @NotEmpty String checkCode) {
        try {
            String codeFromRedis = redisComponent.getCheckCode(checkCodeKey);
            if (codeFromRedis == null || !checkCode.trim().equalsIgnoreCase(codeFromRedis)) {
                throw new BusinessException("Invalid captcha code");
            }
            userInfoService.register(email, useName, registerPassword);
            return getSuccessResponseVO(null);
        } finally {
            redisComponent.clearCheckCode(checkCodeKey);
        }
    }

    @RequestMapping("/login")
    @SentinelResource(value = "auth:login", blockHandlerClass = SentinelBlockHandler.class, blockHandler = "handleBlock")
    public ResponseVO login(
            HttpServletRequest request,
            HttpServletResponse response,
            @NotEmpty @Email @Size(max = 150) String email,
            @NotEmpty String password,
            @NotEmpty String checkCodeKey,
            @NotEmpty String checkCode) {
        try {
            String codeFromRedis = redisComponent.getCheckCode(checkCodeKey);
            if (codeFromRedis == null || !checkCode.trim().equalsIgnoreCase(codeFromRedis)) {
                throw new BusinessException("Invalid captcha code");
            }
            String ip = getIpAddr();
            TokenUserInfoDto tokenUserInfoDto = userInfoService.login(email, password, ip);
            saveToken2Cookie(response, tokenUserInfoDto.getToken());
            return getSuccessResponseVO(tokenUserInfoDto);
        } finally {
            redisComponent.clearCheckCode(checkCodeKey);
        }
    }

    @RequestMapping("/getUserCountInfo")
    public ResponseVO getUserCountInfo() {
        UserInfoQuery query = new UserInfoQuery();
        Integer userCount = userInfoService.findCountByParam(query);
        Map<String, Integer> result = new HashMap<>();
        result.put("userCount", userCount);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/verify")
    public ResponseVO verifyToken(@RequestParam("token") String token) {
        if (token == null || token.isEmpty()) {
            return getSuccessResponseVO(null);
        }
        TokenUserInfoDto userInfo = redisComponent.getTokenUserInfo(token);
        return getSuccessResponseVO(userInfo);
    }

    @RequestMapping("/autologin")
    public ResponseVO autologin(HttpServletResponse response) {
        return autoLogin(response);
    }

    @RequestMapping("/autoLogin")
    public ResponseVO autoLogin(HttpServletResponse response) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getSuccessResponseVO(null);
        }
        if (tokenUserInfoDto.getExpireAt() - System.currentTimeMillis() < Constants.REDIS_KEY_EXPIRE_TIME_ONE_DAY) {
            redisComponent.saveTokenInfo(tokenUserInfoDto);
            saveToken2Cookie(response, tokenUserInfoDto.getToken());
        }
        saveToken2Cookie(response, tokenUserInfoDto.getToken());
        return getSuccessResponseVO(tokenUserInfoDto);
    }

    @RequestMapping("/logout")
    public ResponseVO logout(HttpServletResponse response) {
        removeTokenFromCookie(response);
        return getSuccessResponseVO(null);
    }
}