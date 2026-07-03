package com.machugit.web.contorller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.machugit.entity.query.UserFocusQuery;
import com.machugit.exception.BusinessException;
import com.machugit.entity.query.UserInfoQuery;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.UserCollectionServiceImpl;
import com.machugit.service.impl.UserFocusServiceImpl;
import com.machugit.es.EsSearchService;
import com.machugit.entity.es.UserDoc;
import com.machugit.service.impl.UserInfoServiceImpl;
import com.machugit.service.impl.VideoInfoServiceImpl;

@RestController
@RequestMapping("/uhome")
@Validated
public class UHomeController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(UHomeController.class);

    @Resource
    private EsSearchService esSearchService;

    @Resource
    private UserInfoServiceImpl userInfoService;

    @Resource
    private UserFocusServiceImpl userFocusService;

    @Resource
    private UserCollectionServiceImpl userCollectionService;

    @Resource
    private VideoInfoServiceImpl videoInfoService;

    @RequestMapping("/updateUserInfo")
    public ResponseVO updateUserInfo(String useName,
                                     String personProfile,
                                     String school,
                                     String birthday,
                                     String sex,
                                     String avatar,
                                     String bannerImage) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        UserInfo userInfo = new UserInfo();
        if (useName != null && !useName.isEmpty()) userInfo.setUseName(useName);
        userInfo.setPersonProfile(personProfile);
        userInfo.setSchool(school);
        userInfo.setBirthday(birthday);
        userInfo.setAvatar(avatar);
        userInfo.setBannerImage(bannerImage);
        if (sex != null && !sex.isEmpty()) {
            try {
                userInfo.setSex(Integer.valueOf(sex));
            } catch (NumberFormatException e) {
                throw new BusinessException("性别请传入数字（0：未知 1：男 2：女）");
            }
        }
        userInfoService.updateUserInfoByUserId(userInfo, tokenUserInfoDto.getUserId());
        // 同期到 Elasticsearch
        UserInfo updated = userInfoService.getUserInfoByUserId(tokenUserInfoDto.getUserId());
        if (updated != null) esSearchService.indexUser(updated);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/getUserCountInfo")
    public ResponseVO getUserCountInfo() {
        Map<String, Object> result = new HashMap<>();
        Integer userCount = userInfoService.findCountByParam(new UserInfoQuery());
        result.put("userCount", userCount);
        VideoInfoQuery videoQuery = new VideoInfoQuery();
        videoQuery.setPageNo(1);
        PaginationResultVO<VideoInfo> videoResult = videoInfoService.loadVideoList(videoQuery);
        result.put("videoCount", videoResult.getTotalCount());
        result.put("likeCount", 0);
        result.put("commentCount", 0);
        result.put("danmuCount", 0);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(String userId,
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
        if (userInfo != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userInfo.getUserId());
            result.put("useName", userInfo.getUseName());
            result.put("email", userInfo.getEmail());
            result.put("avatar", userInfo.getAvatar());
            result.put("bannerImage", userInfo.getBannerImage());
            result.put("personProfile", userInfo.getPersonProfile());
            result.put("school", userInfo.getSchool());
            result.put("birthday", userInfo.getBirthday());
            result.put("sex", userInfo.getSex());
            result.put("joinTime", userInfo.getJoinTime());
            // 自动重算等级（已有用户数据可能未升级）
            int exp = userInfo.getExp() == null ? 0 : userInfo.getExp();
            int[] upgradeExps = {0, 100, 500, 1500, 5000, 15000};
            int level = 1;
            for (int i = upgradeExps.length - 1; i > 0; i--) {
                if (exp >= upgradeExps[i]) { level = i + 1; break; }
            }
            if (level > userInfo.getLevel()) {
                UserInfo fixLevel = new UserInfo();
                fixLevel.setLevel(level);
                userInfoService.updateUserInfoByUserId(fixLevel, userId);
                userInfo.setLevel(level);
            }
            result.put("level", userInfo.getLevel());
            result.put("exp", exp);
            result.put("vipType", userInfo.getVipType());
            result.put("theme", userInfo.getTheme());
            UserFocusQuery fq = new UserFocusQuery();
            fq.setUserId(userId);
            result.put("followCount", userFocusService.loadFocusList(userId).size());
            fq.setUserId(null);
            fq.setFocusUserId(userId);
            result.put("fansCount", userFocusService.loadFansList(userId).size());

            // Aggregate total playCount and likeCount from all user's videos
            Long totalPlayCount = 0L;
            Long totalLikeCount = 0L;
            VideoInfoQuery videoQuery = new VideoInfoQuery();
            videoQuery.setUserId(userId);
            videoQuery.setPageNo(1);
            videoQuery.setPageSize(10000);
            videoQuery.setIsDeleted(0);
            PaginationResultVO<VideoInfo> videoResult = videoInfoService.loadVideoList(videoQuery);
            if (videoResult != null && videoResult.getList() != null) {
                for (VideoInfo v : videoResult.getList()) {
                    totalPlayCount += (v.getPlayCount() != null ? v.getPlayCount() : 0L);
                    totalLikeCount += (v.getLikeCount() != null ? v.getLikeCount() : 0L);
                }
            }
            result.put("totalCoinCount", userInfo.getTotalCoinCount());
            result.put("currentCoinCount", userInfo.getCurrentCoinCount());
            result.put("playCount", totalPlayCount);
            result.put("likeCount", totalLikeCount);

            return getSuccessResponseVO(result);
        }
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/focus")
    public ResponseVO focus(String userId,
                            @NotEmpty String focusUserId) {
        TokenUserInfoDto user = getTokenUserInfoDto();
        if (user == null) throw new BusinessException("请先登录");
        String uid = (userId != null && !userId.isEmpty()) ? userId : user.getUserId();
        userFocusService.focus(uid, focusUserId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/cancelFocus")
    public ResponseVO cancelFocus(String userId,
                                  @NotEmpty String focusUserId) {
        TokenUserInfoDto user = getTokenUserInfoDto();
        if (user == null) throw new BusinessException("请先登录");
        String uid = (userId != null && !userId.isEmpty()) ? userId : user.getUserId();
        userFocusService.cancelFocus(uid, focusUserId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadFocusList")
    public ResponseVO loadFocusList(@NotEmpty String userId) {
        List<UserFocus> list = userFocusService.loadFocusList(userId);
        // Enrich with user info (name + avatar)
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (UserFocus f : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("userId", f.getFocusUserId());
            item.put("focusUserId", f.getFocusUserId());
            item.put("createTime", f.getCreateTime());
            UserInfo u = userInfoService.getUserInfoByUserId(f.getFocusUserId());
            if (u != null) {
                item.put("useName", u.getUseName());
                item.put("avatar", u.getAvatar());
            }
            result.add(item);
        }
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/loadFansList")
    public ResponseVO loadFansList(@NotEmpty String userId) {
        List<UserFocus> list = userFocusService.loadFansList(userId);
        // Enrich with user info (name + avatar)
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (UserFocus f : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("userId", f.getUserId());
            item.put("focusUserId", f.getUserId());
            item.put("createTime", f.getCreateTime());
            UserInfo u = userInfoService.getUserInfoByUserId(f.getUserId());
            if (u != null) {
                item.put("useName", u.getUseName());
                item.put("avatar", u.getAvatar());
            }
            result.add(item);
        }
        return getSuccessResponseVO(result);
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

    @RequestMapping("/searchUsers")
    public ResponseVO searchUsers(@NotEmpty String keyword) {
        List<UserDoc> docs = esSearchService.searchUser(keyword, 0, 20);
        if (docs != null && !docs.isEmpty()) {
            List<Map<String, Object>> results = new java.util.ArrayList<>();
            for (UserDoc doc : docs) {
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("userId", doc.getUserId());
                item.put("useName", doc.getUseName());
                item.put("avatar", doc.getAvatar());
                item.put("personProfile", doc.getPersonProfile());
                item.put("school", doc.getSchool());
                item.put("sex", doc.getSex());
                item.put("fansCount", doc.getFansCount());
                item.put("followCount", doc.getFollowCount());
                results.add(item);
            }
            return getSuccessResponseVO(results);
        }
        return getSuccessResponseVO(userInfoService.searchUsers(keyword));
    }
}
