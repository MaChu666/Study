package com.machugit.admin.controller;

import java.util.List;

import com.machugit.entity.po.CommentInfo;
import com.machugit.entity.po.DanmuInfo;
import com.machugit.entity.query.CommentInfoQuery;
import com.machugit.entity.query.DanmuInfoQuery;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.CommentInfoServiceImpl;
import com.machugit.service.impl.DanmuInfoServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/interact")
@Validated
public class AdminInteractController extends ABaseAdminController {

    @Resource
    private DanmuInfoServiceImpl danmuInfoService;

    @Resource
    private CommentInfoServiceImpl commentInfoService;

    @RequestMapping("/loadDanmu")
    public ResponseVO loadDanmu(@NotEmpty String pageNo,
                                String videoNameFuzzy) {
        DanmuInfoQuery query = new DanmuInfoQuery();
        query.setPageNo(Integer.parseInt(pageNo));
        query.setVideoIdFuzzy(videoNameFuzzy);
        query.setOrderBy("post_time desc");
        List<DanmuInfo> list = danmuInfoService.loadDanmuAdmin(query);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/delDanmu")
    public ResponseVO delDanmu(@NotEmpty String danmuId) {
        danmuInfoService.delDanmu(Integer.parseInt(danmuId));
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadComment")
    public ResponseVO loadComment(@NotEmpty String pageNo,
                                  String videoNameFuzzy) {
        CommentInfoQuery query = new CommentInfoQuery();
        query.setPageNo(Integer.parseInt(pageNo));
        query.setVideoIdFuzzy(videoNameFuzzy);
        query.setOrderBy("create_time desc");
        List<CommentInfo> list = commentInfoService.loadCommentAdmin(query);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/delComment")
    public ResponseVO delComment(@NotEmpty String commentId) {
        commentInfoService.delComment(Integer.parseInt(commentId));
        return getSuccessResponseVO(null);
    }
}
