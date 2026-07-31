package com.machugit.interaction.interceptor;

import com.machugit.component.RedisComponent;
import com.machugit.entity.constants.Constants;
import com.machugit.entity.enums.ResponseCodeEnum;
import com.machugit.exception.BusinessException;
import com.machugit.utils.StringTools;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AppInterceptor implements HandlerInterceptor {

    private static final String[] PUBLIC_PATHS = {
        "/danmu/loadDanmu",
    };

    @Resource
    private RedisComponent redisComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        for (String path : PUBLIC_PATHS) {
            if (uri.startsWith(path)) {
                return true;
            }
        }

        String token = request.getHeader(Constants.TOKEN_WEB);
        if (StringTools.isEmpty(token)) {
            token = getTokenFromCookie(request);
        }
        if (StringTools.isEmpty(token)) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        if (null == redisComponent.getTokenUserInfo(token)) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return true;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (Constants.TOKEN_WEB.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}