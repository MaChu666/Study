package com.machugit.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.machugit.entity.po.UserInfo;
import com.machugit.entity.query.UserInfoQuery;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.DanmuVO;
import com.machugit.mappers.UserInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.machugit.entity.po.DanmuInfo;
import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.query.DanmuInfoQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.DanmuInfoMapper;
import com.machugit.mappers.VideoInfoMapper;
import com.machugit.service.DanmuInfoService;


/**
 * 弹幕信息 业务接口实现
 */
@Service("danmuInfoService")
public class DanmuInfoServiceImpl implements DanmuInfoService {

    @Resource
    private DanmuInfoMapper<DanmuInfo, DanmuInfoQuery> danmuInfoMapper;

    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    /**
     * 发布弹幕
     */
    @Override
    public void postDanmu(String userId, String videoId, String fileId, String text, Integer mode, String color, Integer fontSize, Integer isPrior, Integer danmuType, Long time, Long jumpTime) {
        DanmuInfo danmuInfo = new DanmuInfo();
        danmuInfo.setUserId(userId);
        danmuInfo.setVideoId(videoId);
        danmuInfo.setFileId(fileId);
        danmuInfo.setText(text);
        danmuInfo.setMode(mode);
        danmuInfo.setColor(color);
        danmuInfo.setFontSize(fontSize);
        danmuInfo.setIsPrior(isPrior);
        danmuInfo.setDanmuType(danmuType);
        danmuInfo.setTime(time);
        danmuInfo.setJumpTime(jumpTime);
        danmuInfo.setPostTime(new Date());
        this.danmuInfoMapper.insert(danmuInfo);

        // Increment video danmu count
        VideoInfo videoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException("视频信息不存在");
        }
        Long danmuCount = videoInfo.getDanmuCount() == null ? 0L : videoInfo.getDanmuCount();
        VideoInfo updateInfo = new VideoInfo();
        updateInfo.setDanmuCount(danmuCount + 1);
        this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
    }

    /**
     * 加载弹幕
     */
    @Override
    public List<DanmuInfo> loadDanmu(String fileId, String videoId) {
        DanmuInfoQuery query = new DanmuInfoQuery();
        query.setFileId(fileId);
        query.setVideoId(videoId);
        return this.danmuInfoMapper.selectList(query);
    }

    /**
     * 加载弹幕（管理员）
     */
    @Override
    public List<DanmuVO> loadDanmuAdmin(DanmuInfoQuery query) {
        // 1. 查询原始弹幕列表
        List<DanmuInfo> danmuList = this.danmuInfoMapper.selectList(query);
        if (danmuList == null || danmuList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 收集 videoId 和 userId
        Set<String> videoIds = danmuList.stream()
                .map(DanmuInfo::getVideoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> userIds = danmuList.stream()
                .map(DanmuInfo::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 批量查询视频名称
        Map<String, String> videoNameMap = new HashMap<>();
        if (!videoIds.isEmpty()) {
            List<VideoInfo> videoList = videoInfoMapper.selectByVideoIds(videoIds);
            videoNameMap = videoList.stream()
                    .collect(Collectors.toMap(VideoInfo::getVideoId, VideoInfo::getVideoName));
        }

        // 4. 批量查询用户昵称
        Map<String, String> userNameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserInfo> userList = userInfoMapper.selectByUserIds(userIds);
            // 注意：UserInfo 中字段可能是 nickName，请确认
            userNameMap = userList.stream()
                    .collect(Collectors.toMap(UserInfo::getUserId, UserInfo::getUseName));
        }

        // 5. 组装 VO
        List<DanmuVO> result = new ArrayList<>();
        for (DanmuInfo danmu : danmuList) {
            DanmuVO vo = new DanmuVO();
            BeanUtils.copyProperties(danmu, vo);
            vo.setVideoName(videoNameMap.getOrDefault(danmu.getVideoId(), "视频已删除"));
            vo.setUserName(userNameMap.getOrDefault(danmu.getUserId(), "用户已注销"));
            result.add(vo);
        }
        return result;
    }
    /**
     * 删除弹幕
     */
    @Override
    public void delDanmu(Integer danmuId) {
        this.danmuInfoMapper.deleteByDanmuId(danmuId);
    }
}
