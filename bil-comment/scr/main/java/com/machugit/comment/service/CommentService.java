package com.machugit.comment.service;

import com.machugit.entity.po.CommentInfo;
import com.machugit.entity.vo.PaginationResultVO;

public interface CommentService {
    String postComment(String videoId, String userId, String content, String replyId);
    PaginationResultVO loadComment(String videoId, Integer pageNo, Integer pageSize);
}