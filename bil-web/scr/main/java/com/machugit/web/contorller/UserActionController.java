package com.machugit.web.contorller;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.UserActionServiceImpl;

@RestController
@RequestMapping("/userAction")
@Validated
public class UserActionController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserActionController.class);

    @Resource
    private UserActionServiceImpl userActionService;

    @RequestMapping("/checkStatus")
    public ResponseVO checkStatus(@NotEmpty String videoId) {
        TokenUserInfoDto user = getTokenUserInfoDto();
        if (user == null) return getSuccessResponseVO(null);
        return getSuccessResponseVO(userActionService.checkStatus(videoId, user.getUserId()));
    }

    @RequestMapping("/doAction")
    public ResponseVO doAction(@NotEmpty String videoId,
                               @NotEmpty String actionType,
                               @NotEmpty String actionCount,
                               @NotEmpty String commentId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("用户未登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        Integer actionTypeInt = Integer.parseInt(actionType);
        Integer actionCountInt = Integer.parseInt(actionCount);
        Integer commentIdInt = Integer.parseInt(commentId);
        userActionService.doAction(videoId, userId, actionTypeInt, actionCountInt, commentIdInt);
        return getSuccessResponseVO(null);
    }
}
