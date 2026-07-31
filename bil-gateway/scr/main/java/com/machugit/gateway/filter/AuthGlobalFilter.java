package com.machugit.gateway.filter;

import com.machugit.component.RedisComponent;
import com.machugit.entity.constants.Constants;
import com.machugit.entity.dto.TokenUserInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthGlobalFilter.class);

    private static final String[] PUBLIC_PATHS = {
        "/account/",
        "/auth/",
        "/admin/account/",
        "/category/",
        "/banner/",
        "/video/",
        "/series/",
        "/sysSetting/",
        "/file/getResource",
        "/file/videoResource",
        "/comment/loadComment",
        "/danmu/loadDanmu",
        "/uhome/getUserInfo",
        "/uhome/loadVideoList",
        "/uhome/loadUserCollection",
        "/uhome/loadFocusList",
        "/uhome/loadFansList",
        "/uhome/searchUsers",
        "/uhome/series/loadVideoSeries",
        "/uhome/series/loadAllVideo",
        "/uhome/series/loadVideoSeriesWithVideo",
        "/signIn/getTodaySign",
        "/videos/",
        "/images/",
    };

    @Resource
    private RedisComponent redisComponent;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Allow public paths
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return chain.filter(exchange);
            }
        }

        // Extract token from header or cookie
        String token = extractToken(request);
        if (token == null || token.isEmpty()) {
            return unauthorizedResponse(exchange, "Please login first");
        }

        // Validate token
        TokenUserInfoDto userInfo = redisComponent.getTokenUserInfo(token);
        if (userInfo == null) {
            // Also try admin token
            Object adminInfo = redisComponent.getAdminTokenUserInfo(token);
            if (adminInfo == null) {
                return unauthorizedResponse(exchange, "Login expired, please login again");
            }
        }

        return chain.filter(exchange);
    }

    private String extractToken(ServerHttpRequest request) {
        // Try header first
        List<String> tokenHeaders = request.getHeaders().get(Constants.TOKEN_WEB);
        if (tokenHeaders != null && !tokenHeaders.isEmpty()) {
            return tokenHeaders.get(0);
        }
        // Try admin token header
        List<String> adminHeaders = request.getHeaders().get(Constants.TOKEN_ADMIN);
        if (adminHeaders != null && !adminHeaders.isEmpty()) {
            return adminHeaders.get(0);
        }
        // Try cookie
        HttpCookie cookie = request.getCookies().getFirst(Constants.TOKEN_WEB);
        if (cookie != null) {
            return cookie.getValue();
        }
        // Try admin cookie
        HttpCookie adminCookie = request.getCookies().getFirst(Constants.TOKEN_ADMIN);
        if (adminCookie != null) {
            return adminCookie.getValue();
        }
        return null;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"status\":\"error\",\"code\":901,\"info\":\"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}