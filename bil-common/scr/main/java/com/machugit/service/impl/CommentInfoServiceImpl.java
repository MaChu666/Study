package com.machugit.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.machugit.entity.po.UserInfo;
import com.machugit.entity.query.UserInfoQuery;
import com.machugit.entity.vo.CommentVO;
import com.machugit.mappers.UserInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.machugit.entity.po.CommentInfo;
import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.query.CommentInfoQuery;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.CommentInfoMapper;
import com.machugit.mappers.VideoInfoMapper;
import com.machugit.service.CommentInfoService;


/**
 * 评论信息 业务接口实现
 */
@Service("commentInfoService")
public class CommentInfoServiceImpl implements CommentInfoService {

    @Resource
    private CommentInfoMapper<CommentInfo, CommentInfoQuery> commentInfoMapper;

    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    /**
     * 加载评论
     */
    @Override
    public List<CommentInfo> loadComment(String videoId, Integer pageNo, Integer orderType) {
        CommentInfoQuery query = new CommentInfoQuery();
        query.setVideoId(videoId);
        query.setPageNo(pageNo);
        query.setOrderBy(orderType == 0 ? "create_time desc" : "create_time asc");
        return this.commentInfoMapper.selectList(query);
    }

    /**
     * 发布评论
     */
    @Override
    public void postComment(String userId, String videoId, String content, Integer replyCommentId, String imgPath) {
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setUserId(userId);
        commentInfo.setVideoId(videoId);
        commentInfo.setContent(content);
        commentInfo.setReplyCommentId(replyCommentId);
        commentInfo.setImgPath(imgPath);
        commentInfo.setTopType(0);
        // B站楼中楼: 设置root
        if (replyCommentId == null || replyCommentId == 0) {
            commentInfo.setRoot(0);
        } else {
            CommentInfo parent = this.commentInfoMapper.selectByCommentId(replyCommentId);
            if (parent != null && parent.getRoot() != null && parent.getRoot() > 0) {
                commentInfo.setRoot(parent.getRoot());
            } else {
                commentInfo.setRoot(replyCommentId);
            }
        }
        commentInfo.setCreateTime(new Date());
        this.commentInfoMapper.insert(commentInfo);

        // Increment video comment count
        VideoInfo videoInfo = this.videoInfoMapper.selectByVideoId(videoId);
        if (videoInfo == null) {
            throw new BusinessException("视频信息不存在");
        }
        Long commentCount = videoInfo.getCommentCount() == null ? 0L : videoInfo.getCommentCount();
        VideoInfo updateInfo = new VideoInfo();
        updateInfo.setCommentCount(commentCount + 1);
        this.videoInfoMapper.updateByVideoId(updateInfo, videoId);
    }

    /**
     * 置顶评论
     */
    @Override
    public void topComment(Integer commentId) {
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setTopType(1);
        this.commentInfoMapper.updateByCommentId(commentInfo, commentId);
    }

    /**
     * 取消置顶评论
     */
    @Override
    public void cancelTopComment(Integer commentId) {
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setTopType(0);
        this.commentInfoMapper.updateByCommentId(commentInfo, commentId);
    }

    /**
     * 用户删除评论
     */
    @Override
    public void userDelComment(Integer commentId) {
        this.commentInfoMapper.deleteByCommentId(commentId);
    }

    /**
     * 加载评论（管理员）
     */
    @Override
    public List<CommentVO> loadCommentAdmin(CommentInfoQuery query) {
        // 1. 查询原始评论列表
        List<CommentInfo> commentList = this.commentInfoMapper.selectList(query);
        if (commentList == null || commentList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 收集 videoId 和 userId
        Set<String> videoIds = commentList.stream()
                .map(CommentInfo::getVideoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> userIds = commentList.stream()
                .map(CommentInfo::getUserId)
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
            // 注意：用户名字段根据您的实体调整，可能是 getUserName() 或 getNickName()
            userNameMap = userList.stream()
                    .collect(Collectors.toMap(UserInfo::getUserId, UserInfo::getUseName));
        }

        // 5. 组装 VO
        List<CommentVO> result = new ArrayList<>();
        for (CommentInfo comment : commentList) {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(comment, vo);
            vo.setVideoName(videoNameMap.getOrDefault(comment.getVideoId(), "视频已删除"));
            vo.setUserName(userNameMap.getOrDefault(comment.getUserId(), "用户已注销"));
            result.add(vo);
        }
        return result;
    }

    /**
     * 删除评论（管理员）
     */
    @Override
    public void delComment(Integer commentId) {
        this.commentInfoMapper.deleteByCommentId(commentId);
    }
}
