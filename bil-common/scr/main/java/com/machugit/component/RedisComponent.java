package com.machugit.component;

import com.machugit.entity.constants.Constants;
import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.exception.BusinessException;
import com.machugit.redis.RedisUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    public String saveChackCode(String code) {
        String checkCodeKey = UUID.randomUUID().toString();
        if (!redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey,code,Constants.REDIS_KEY_EXPIRE_TIME_ONE_MIN *10)) {
            throw new BusinessException("验证码保存失败");
        }
        return checkCodeKey;
    }

    public String getCheckCode(String checkCodeKey) {
        return (String)redisUtils.get(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey);
    }

    //清除验证码
    public void clearCheckCode(String checkCodeKey) {
        redisUtils.delete(Constants.REDIS_KEY_CHECK_CODE+checkCodeKey);
    }

    //保存token
    public void saveTokenInfo(TokenUserInfoDto tokenUserInfoDto){
        String token = UUID.randomUUID().toString();
        tokenUserInfoDto.setExpireAt(System.currentTimeMillis()+Constants.REDIS_KEY_EXPIRE_TIME_ONE_DAY *30);
        tokenUserInfoDto.setToken(token);
        redisUtils.setex(Constants.REDIS_KEY_TOKEN_WEB+token,tokenUserInfoDto,Constants.REDIS_KEY_EXPIRE_TIME_ONE_DAY *30);
    }
}