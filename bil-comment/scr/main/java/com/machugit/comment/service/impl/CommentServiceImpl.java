package com.machugit.comment.service.impl;

import com.machugit.comment.service.CommentService;
import com.machugit.entity.vo.CommentVO;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.service.CommentInfoService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentInfoService commentInfoService;

    @Override
    public String postComment(String videoId, String userId, String content, String replyId) {
        Integer replyCommentId = (replyId != null && !replyId.isEmpty()) ? Integer.parseInt(replyId) : null;
        commentInfoService.postComment(userId, videoId, content, replyCommentId, null);
        return null;
    }

    @Override
    public PaginationResultVO loadComment(String videoId, Integer pageNo, Integer pageSize) {
        java.util.List<CommentVO> list = commentInfoService.loadComment(videoId, pageNo, 0);
        PaginationResultVO result = new PaginationResultVO();
        result.setList(list);
        return result;
    }
}