package com.machugit.web.contorller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.po.UserCollection;
import com.machugit.entity.po.UserFocus;
import com.machugit.entity.po.UserInfo;
import com.machugit.entity.po.VideoInfo;
import com.machugit.exception.BusinessException;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.UserCollectionServiceImpl;
import com.machugit.service.impl.UserFocusServiceImpl;
import com.machugit.service.impl.UserInfoServiceImpl;
import com.machugit.service.impl.VideoInfoServiceImpl;

@RestController
@RequestMapping("/uhome")
@Validated
public class UHomeController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(UHomeController.class);

    @Resource
    private UserInfoServiceImpl userInfoService;

    @Resource
    private UserFocusServiceImpl userFocusService;

    @Resource
    private UserCollectionServiceImpl userCollectionService;

    @Resource
    private VideoInfoServiceImpl videoInfoService;

    @RequestMapping("/updateUserInfo")
    public ResponseVO updateUserInfo(@NotEmpty String useName,
                                     String personProfile,
                                     String school,
                                     String birthday,
                                     String sex) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUseName(useName);
        userInfo.setPersonProfile(personProfile);
        userInfo.setSchool(school);
        userInfo.setBirthday(birthday);
        if (sex != null && !sex.isEmpty()) {
            try {
                userInfo.setSex(Integer.valueOf(sex));
            } catch (NumberFormatException e) {
                throw new BusinessException("性别请传入数字（0：未知 1：男 2：女）");
            }
        }
        userInfoService.updateUserInfoByUserId(userInfo, tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(@NotEmpty String userId,
                                    @NotEmpty String pageNo) {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setUserId(userId);
        query.setPageNo(Integer.parseInt(pageNo));
        query.setOrderBy("create_time desc");
        PaginationResultVO<VideoInfo> result = videoInfoService.loadVideoList(query);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/getUserInfo")
    public ResponseVO getUserInfo(@NotEmpty String userId) {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        return getSuccessResponseVO(userInfo);
    }

    @RequestMapping("/focus")
    public ResponseVO focus(@NotEmpty String userId,
                            @NotEmpty String focusUserId) {
        userFocusService.focus(userId, focusUserId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/cancelFocus")
    public ResponseVO cancelFocus(@NotEmpty String userId,
                                  @NotEmpty String focusUserId) {
        userFocusService.cancelFocus(userId, focusUserId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadFocusList")
    public ResponseVO loadFocusList(@NotEmpty String userId) {
        List<UserFocus> list = userFocusService.loadFocusList(userId);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/loadFansList")
    public ResponseVO loadFansList(@NotEmpty String userId) {
        List<UserFocus> list = userFocusService.loadFansList(userId);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/loadUserCollection")
    public ResponseVO loadUserCollection(@NotEmpty String userId) {
        List<UserCollection> list = userCollectionService.loadUserCollection(userId);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/saveTheme")
    public ResponseVO saveTheme(@NotEmpty String userId,
                                @NotEmpty String theme) {
        UserInfo userInfo = new UserInfo();
        userInfo.setTheme(Integer.valueOf(theme));
        userInfoService.updateUserInfoByUserId(userInfo, userId);
        return getSuccessResponseVO(null);
    }
}
