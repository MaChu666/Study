package com.machugit.admin.controller;

import java.util.List;

import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.po.VideoInfoFile;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.VideoInfoServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/videoInfo")
@Validated
public class AdminVideoController extends ABaseAdminController {

    @Resource
    private VideoInfoServiceImpl videoInfoService;

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(@NotEmpty String status,
                                    @NotEmpty String pageNo,
                                    String videoNameFuzzy) {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setStatus(Integer.parseInt(status));
        query.setPageNo(Integer.parseInt(pageNo));
        query.setVideoNameFuzzy(videoNameFuzzy);
        query.setOrderBy("create_time desc");
        PaginationResultVO<VideoInfo> result = videoInfoService.loadVideoList(query);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/auditVideo")
    public ResponseVO auditVideo(@NotEmpty String videoId,
                                 @NotEmpty String status,
                                 @NotEmpty String reason) {
        videoInfoService.auditVideo(videoId, Integer.parseInt(status), reason);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/deleteVideo")
    public ResponseVO deleteVideo(@NotEmpty String videoId) {
        videoInfoService.deleteVideo(videoId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/recommendVideo")
    public ResponseVO recommendVideo(@NotEmpty String videoId) {
        videoInfoService.recommendVideo(videoId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideoPList")
    public ResponseVO loadVideoPList(@NotEmpty String videoId) {
        List<VideoInfoFile> list = videoInfoService.loadVideoPList(videoId);
        return getSuccessResponseVO(list);
    }
}
