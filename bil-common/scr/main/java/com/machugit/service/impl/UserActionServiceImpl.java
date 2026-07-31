package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.UserAction;
import com.machugit.entity.po.UserInfo;
import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.query.UserActionQuery;
import com.machugit.entity.query.UserInfoQuery;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.UserActionMapper;
import com.machugit.mappers.UserInfoMapper;
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

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private CoinTransactionLogServiceImpl coinTransactionLogService;

    @Resource
    private UserCollectionServiceImpl userCollectionService;

    @Resource
    private UserExpLogServiceImpl userExpLogService;

    /**
     * 查找用户对指定视频的指定行为类型的最新非取消记录
     */
    private UserAction findActiveAction(String videoId, String userId, Integer actionType) {
        UserActionQuery query = new UserActionQuery();
        query.setVideoId(videoId);
        query.setUserId(userId);
        query.setActionType(actionType);
        query.setIsCancel(0);
        query.setOrderBy("create_time desc");
        List<UserAction> list = userActionMapper.selectList(query);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    /**
     * 执行用户行为
     * actionType: 1=点赞 2=投币 3=收藏
     * 点赞和收藏支持 toggle（再点击取消），投币只能操作一次
     */
    @Override
    public void doAction(String videoId, String userId, Integer actionType, Integer actionCount, Integer commentId) {
        if (actionType == null) {
            return;
        }
        VideoInfo videoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException("视频信息不存在");
        }
        int count = (actionCount == null ? 1 : actionCount);

        // ---- 点赞 (actionType=1): toggle logic ----
        if (actionType == 1) {
            UserAction existing = findActiveAction(videoId, userId, 1);
            if (existing != null) {
                // Toggle off: cancel the existing like
                UserAction update = new UserAction();
                update.setIsCancel(1);
                userActionMapper.updateByActionId(update, existing.getActionId());

                Long likeCount = videoInfo.getLikeCount() == null ? 0L : videoInfo.getLikeCount();
                VideoInfo updateInfo = new VideoInfo();
                updateInfo.setLikeCount(Math.max(0, likeCount - 1));
                this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
            } else {
                // New like
                UserAction userAction = new UserAction();
                userAction.setVideoId(videoId);
                userAction.setUserId(userId);
                userAction.setActionType(1);
                userAction.setActionCount(count);
                userAction.setCommentId(commentId);
                userAction.setIsCancel(0);
                userAction.setCreateTime(new Date());
                this.userActionMapper.insert(userAction);

                Long likeCount = videoInfo.getLikeCount() == null ? 0L : videoInfo.getLikeCount();
                VideoInfo updateInfo = new VideoInfo();
                updateInfo.setLikeCount(likeCount + count);
            try { userExpLogService.addExp(userId, 10, 2, videoId); } catch (Exception ignored) {}
            try { userExpLogService.addExp(videoInfo.getUserId(), 1, 2, videoId); } catch (Exception ignored) {}
                this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
            }
        }

        // ---- 投币 (actionType=2): one-time only ----
        if (actionType == 2) {
            UserAction existing = findActiveAction(videoId, userId, 2);
            if (existing != null) {
                throw new BusinessException("已投币，无法再次投币");
            }
            // New coin
            UserAction userAction = new UserAction();
            userAction.setVideoId(videoId);
            userAction.setUserId(userId);
            userAction.setActionType(2);
            userAction.setActionCount(count);
            userAction.setCoinCount(count);
            userAction.setCommentId(commentId);
            userAction.setIsCancel(0);
            userAction.setCreateTime(new Date());
            this.userActionMapper.insert(userAction);

            Long coinCount = videoInfo.getCoinCount() == null ? 0L : videoInfo.getCoinCount();
            VideoInfo updateInfo = new VideoInfo();
            updateInfo.setCoinCount(coinCount + count);
            try { userExpLogService.addExp(userId, 10, 2, videoId); } catch (Exception ignored) {}
            try { userExpLogService.addExp(videoInfo.getUserId(), 1, 2, videoId); } catch (Exception ignored) {}
            this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
            this.coinTransactionLogService.addCoinTransaction(userId, videoId, count);

            // Transfer coins to the video owner
            String ownerUserId = videoInfo.getUserId();
            if (ownerUserId != null && !ownerUserId.equals(userId)) {
                UserInfo owner = this.userInfoMapper.selectByUserId(ownerUserId);
                if (owner != null) {
                    UserInfo ownerUpdate = new UserInfo();
                    Integer currentTotal = owner.getTotalCoinCount() == null ? 0 : owner.getTotalCoinCount();
                    Integer currentCoins = owner.getCurrentCoinCount() == null ? 0 : owner.getCurrentCoinCount();
                    ownerUpdate.setTotalCoinCount(currentTotal + count);
                    ownerUpdate.setCurrentCoinCount(currentCoins + count);
                    this.userInfoMapper.updateByUserId(ownerUpdate, ownerUserId);
                }
            }
        }

        // ---- 收藏 (actionType=3): toggle logic ----
        if (actionType == 3) {
            UserAction existing = findActiveAction(videoId, userId, 3);
            if (existing != null) {
                // Toggle off: cancel the existing collect
                UserAction update = new UserAction();
                update.setIsCancel(1);
                userActionMapper.updateByActionId(update, existing.getActionId());

                Long collectCount = videoInfo.getCollectCount() == null ? 0L : videoInfo.getCollectCount();
                VideoInfo updateInfo = new VideoInfo();
                updateInfo.setCollectCount(Math.max(0, collectCount - 1));
                this.videoInfoMapper.updateByVideoId(updateInfo, videoId);

                this.userCollectionService.cancelCollect(userId, videoId);
            } else {
                // New collect
                UserAction userAction = new UserAction();
                userAction.setVideoId(videoId);
                userAction.setUserId(userId);
                userAction.setActionType(3);
                userAction.setActionCount(count);
                userAction.setCommentId(commentId);
                userAction.setIsCancel(0);
                userAction.setCreateTime(new Date());
                this.userActionMapper.insert(userAction);

                Long collectCount = videoInfo.getCollectCount() == null ? 0L : videoInfo.getCollectCount();
                VideoInfo updateInfo = new VideoInfo();
                updateInfo.setCollectCount(collectCount + count);
                this.videoInfoMapper.updateByVideoId(updateInfo, videoId);

                this.userCollectionService.collect(userId, videoId);
            }
        }
    }

    @Override
    public java.util.Map<String, Boolean> checkStatus(String videoId, String userId) {
        java.util.Map<String, Boolean> result = new java.util.HashMap<>();
        result.put("liked", findActiveAction(videoId, userId, 1) != null);
        result.put("coined", findActiveAction(videoId, userId, 2) != null);
        result.put("collected", findActiveAction(videoId, userId, 3) != null);
        return result;
    }
}