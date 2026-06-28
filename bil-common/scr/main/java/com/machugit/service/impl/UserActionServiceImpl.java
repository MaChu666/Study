package com.machugit.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.UserAction;
import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.query.UserActionQuery;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.UserActionMapper;
import com.machugit.mappers.VideoInfoMapper;
import com.machugit.service.UserActionService;


/**
 * 用户行为 业务接口实现
 */
@Service("userActionService")
public class UserActionServiceImpl implements UserActionService {

    @Resource
    private UserActionMapper<UserAction, UserActionQuery> userActionMapper;

    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    /**
     * 执行用户行为
     */
    @Override
    public void doAction(String videoId, String userId, Integer actionType, Integer actionCount, Integer commentId) {
        UserAction userAction = new UserAction();
        userAction.setVideoId(videoId);
        userAction.setUserId(userId);
        userAction.setActionType(actionType);
        userAction.setActionCount(actionCount);
        userAction.setCommentId(commentId);
        userAction.setCreateTime(new Date());
        this.userActionMapper.insert(userAction);

        if (actionType == null) {
            return;
        }
        VideoInfo videoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException("视频信息不存在");
        }
        VideoInfo updateInfo = new VideoInfo();
        int count = (actionCount == null ? 1 : actionCount);
        if (actionType == 1) {
            Long likeCount = videoInfo.getLikeCount() == null ? 0L : videoInfo.getLikeCount();
            updateInfo.setLikeCount(likeCount + count);
            this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
        }
        if (actionType == 2) {
            Long coinCount = videoInfo.getCoinCount() == null ? 0L : videoInfo.getCoinCount();
            updateInfo.setCoinCount(coinCount + count);
            this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
        }
    }
}
