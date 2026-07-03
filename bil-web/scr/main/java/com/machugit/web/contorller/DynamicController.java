package com.machugit.web.contorller;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.po.UserDynamic;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.DynamicLikeServiceImpl;
import com.machugit.service.impl.UserDynamicServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dynamic")
@Validated
public class DynamicController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(DynamicController.class);

    @Resource
    private UserDynamicServiceImpl userDynamicService;

    @Resource
    private DynamicLikeServiceImpl dynamicLikeService;

    @RequestMapping("/loadDynamics")
    public ResponseVO loadDynamics(Integer pageNo, String userId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        String targetUserId = (userId != null && !userId.isEmpty()) ? userId
                : (tokenUserInfoDto != null ? tokenUserInfoDto.getUserId() : null);
        if (targetUserId == null) {
            throw new BusinessException("请先登录");
        }
        return getSuccessResponseVO(userDynamicService.loadDynamics(targetUserId, pageNo));
    }

    @RequestMapping("/postDynamic")
    public ResponseVO postDynamic(String content, Integer dynamicType, String videoId, String images) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        UserDynamic dynamic = new UserDynamic();
        dynamic.setUserId(userId);
        dynamic.setContent(content);
        dynamic.setDynamicType(dynamicType);
        dynamic.setVideoId(videoId);
        dynamic.setImages(images);
        userDynamicService.postDynamic(dynamic);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/deleteDynamic")
    public ResponseVO deleteDynamic(Integer dynamicId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        userDynamicService.deleteDynamic(dynamicId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/likeDynamic")
    public ResponseVO likeDynamic(Integer dynamicId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        dynamicLikeService.like(dynamicId, userId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/unlikeDynamic")
    public ResponseVO unlikeDynamic(Integer dynamicId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        dynamicLikeService.unlike(dynamicId, userId);
        return getSuccessResponseVO(null);
    }
}
