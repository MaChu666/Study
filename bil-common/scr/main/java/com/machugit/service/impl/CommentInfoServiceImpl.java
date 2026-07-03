package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

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
    public List<CommentInfo> loadCommentAdmin(CommentInfoQuery query) {
        return this.commentInfoMapper.selectList(query);
    }

    /**
     * 删除评论（管理员）
     */
    @Override
    public void delComment(Integer commentId) {
        this.commentInfoMapper.deleteByCommentId(commentId);
    }
}
