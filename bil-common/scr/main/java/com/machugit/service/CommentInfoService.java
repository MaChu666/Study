package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.CommentInfo;
import com.machugit.entity.query.CommentInfoQuery;
import com.machugit.entity.vo.CommentVO;


/**
 * 评论信息 业务接口
 */
public interface CommentInfoService {

    /**
     * 加载评论
     */
    List<CommentInfo> loadComment(String videoId, Integer pageNo, Integer orderType);

    /**
     * 发布评论
     */
    void postComment(String userId, String videoId, String content, Integer replyCommentId, String imgPath);

    /**
     * 置顶评论
     */
    void topComment(Integer commentId);

    /**
     * 取消置顶评论
     */
    void cancelTopComment(Integer commentId);

    /**
     * 用户删除评论
     */
    void userDelComment(Integer commentId);

    /**
     * 加载评论（管理员）
     */
    List<CommentVO> loadCommentAdmin(CommentInfoQuery query);

    /**
     * 删除评论（管理员）
     */
    void delComment(Integer commentId);
}
