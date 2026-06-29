package com.machugit.admin.interceptor;

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
public class Appinterceptor implements HandlerInterceptor {
    public static final String ACCOUNT_URL = "/admin/account";

    @Resource
    private RedisComponent redisComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(null==handler) {
            return false;
        }
        if(request.getRequestURI().startsWith(ACCOUNT_URL)) {
            return true;
        }
        String token = getTokenFromCookie(request);
        if(StringTools.isEmpty(token)) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        Object sessionObj = redisComponent.getAdminTokenUserInfo(token);
        if(null==sessionObj) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return true;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies==null) {
            return null;
        }
        for(Cookie cookie:cookies) {
            if(cookie.getName().equals(Constants.TOKEN_ADMIN)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
