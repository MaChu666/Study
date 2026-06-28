package com.machugit.web.contorller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.VideoInfoServiceImpl;

/**
 * 创作中心
 */
@RestController
@RequestMapping("/ucenter")
@Validated
public class UCenterController extends ABaseController {

    @Resource
    private VideoInfoServiceImpl videoInfoService;

    @RequestMapping("/postVideo")
    public ResponseVO postVideo(@NotEmpty String videoCover,
                                 @NotEmpty String videoName,
                                 @NotEmpty String pCategoryId,
                                 @NotEmpty String categoryId,
                                 @NotEmpty String postType,
                                 @NotEmpty String tags,
                                 @NotEmpty String introduction,
                                 @NotEmpty String interaction,
                                 @NotEmpty String uploadFileList) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setVideoCover(videoCover);
        videoInfo.setVideoName(videoName);
        videoInfo.setPCategoryId(Integer.valueOf(pCategoryId));
        videoInfo.setCategoryId(Integer.valueOf(categoryId));
        videoInfo.setPostType(Integer.valueOf(postType));
        videoInfo.setTags(tags);
        videoInfo.setIntroduction(introduction);
        videoInfo.setInteraction(interaction);
        // TODO 从token获取当前用户userId
        videoInfo.setUserId("currentUser");
        // TODO 解析uploadFileList为文件ID列表
        videoInfoService.postVideo(videoInfo, null);
        return getSuccessResponseVO(videoInfo);
    }

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(@NotEmpty String status,
                                    @NotEmpty String pageNo,
                                    String videoNameFuzzy) {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setStatus(Integer.valueOf(status));
        query.setPageNo(Integer.valueOf(pageNo));
        if (videoNameFuzzy != null && !videoNameFuzzy.isEmpty()) {
            query.setVideoNameFuzzy(videoNameFuzzy);
        }
        query.setOrderBy("create_time desc");
        PaginationResultVO<VideoInfo> result = videoInfoService.loadVideoList(query);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/getVideoCountInfo")
    public ResponseVO getVideoCountInfo() {
        VideoInfoQuery query = new VideoInfoQuery();
        Map<String, Integer> countInfo = new HashMap<>();
        query.setStatus(0);
        countInfo.put("auditCount", videoInfoService.loadVideoList(query).getTotalCount());
        query.setStatus(1);
        countInfo.put("passCount", videoInfoService.loadVideoList(query).getTotalCount());
        query.setStatus(2);
        countInfo.put("rejectCount", videoInfoService.loadVideoList(query).getTotalCount());
        return getSuccessResponseVO(countInfo);
    }

    @RequestMapping("/getVideoByVideoId")
    public ResponseVO getVideoByVideoId(@NotEmpty String videoId) {
        VideoInfo videoInfo = videoInfoService.getVideoInfo(videoId);
        return getSuccessResponseVO(videoInfo);
    }

    @RequestMapping("/saveVideoInteraction")
    public ResponseVO saveVideoInteraction(@NotEmpty String videoId,
                                            @NotEmpty String interaction) {
        VideoInfo updateInfo = new VideoInfo();
        updateInfo.setInteraction(interaction);
        // TODO 更新互动设置
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/deleteVideo")
    public ResponseVO deleteVideo(@NotEmpty String videoId) {
        videoInfoService.deleteVideo(videoId);
        return getSuccessResponseVO(null);
    }
}
