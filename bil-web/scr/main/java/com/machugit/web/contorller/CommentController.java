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
import com.machugit.entity.po.CommentInfo;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.CommentInfoServiceImpl;

@RestController
@RequestMapping("/comment")
@Validated
public class CommentController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Resource
    private CommentInfoServiceImpl commentInfoService;

    @RequestMapping("/postComment")
    public ResponseVO postComment(@NotEmpty String videoId,
                                  @NotEmpty String content,
                                  String replyCommentId,
                                  String imgPath) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        Integer replyCommentIdInt = (replyCommentId != null && !replyCommentId.isEmpty()) ? Integer.parseInt(replyCommentId) : 0;
        commentInfoService.postComment(tokenUserInfoDto.getUserId(), videoId, content, replyCommentIdInt, imgPath);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadComment")
    public ResponseVO loadComment(@NotEmpty String videoId,
                                  @NotEmpty String pageNo,
                                  @NotEmpty String orderType) {
        Integer pageNoInt = Integer.parseInt(pageNo);
        Integer orderTypeInt = Integer.parseInt(orderType);
        List<CommentInfo> list = commentInfoService.loadComment(videoId, pageNoInt, orderTypeInt);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/topComment")
    public ResponseVO topComment(@NotEmpty String commentId) {
        Integer commentIdInt = Integer.parseInt(commentId);
        commentInfoService.topComment(commentIdInt);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/cancelTopComment")
    public ResponseVO cancelTopComment(@NotEmpty String commentId) {
        Integer commentIdInt = Integer.parseInt(commentId);
        commentInfoService.cancelTopComment(commentIdInt);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/userDelComment")
    public ResponseVO userDelComment(@NotEmpty String commentId) {
        Integer commentIdInt = Integer.parseInt(commentId);
        commentInfoService.userDelComment(commentIdInt);
        return getSuccessResponseVO(null);
    }
}
