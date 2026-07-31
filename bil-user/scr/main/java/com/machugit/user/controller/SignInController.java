package com.machugit.user.controller;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.UserExpLogServiceImpl;
import com.machugit.service.impl.UserSignInServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/signIn")
@Validated
public class SignInController extends com.machugit.controller.ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(SignInController.class);

    @Resource
    private UserSignInServiceImpl userSignInService;

    @Resource
    private UserExpLogServiceImpl userExpLogService;

    @RequestMapping("/signIn")
    public ResponseVO signIn() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        userSignInService.signIn(userId);
        userExpLogService.addExp(userId, 5, 6, null);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/getTodaySign")
    public ResponseVO getTodaySign() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        return getSuccessResponseVO(userSignInService.getTodaySign(userId));
    }

    @RequestMapping("/getContinuousDays")
    public ResponseVO getContinuousDays() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        return getSuccessResponseVO(userSignInService.getContinuousDays(userId));
    }
}
