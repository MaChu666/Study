package com.machugit.web.contorller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.po.VideoInfo;
import com.machugit.exception.BusinessException;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.VideoInfoServiceImpl;
import com.machugit.es.EsSearchService;

/**
 * 创作中心
 */
@RestController
@RequestMapping("/ucenter")
@Validated
public class UCenterController extends ABaseController {

    @Resource
    private EsSearchService esSearchService;

    @Resource
    private VideoInfoServiceImpl videoInfoService;

    @RequestMapping("/postVideo")
    public ResponseVO postVideo(String videoCover,
                                 @NotEmpty String videoName,
                                 @NotEmpty String pCategoryId,
                                 @NotEmpty String categoryId,
                                 String postType,
                                 String tags,
                                 String introduction,
                                 String interaction,
                                 String uploadFileList) {
        VideoInfo videoInfo = new VideoInfo();
        if (videoCover != null && !videoCover.isEmpty()) videoInfo.setVideoCover(videoCover);
        videoInfo.setVideoName(videoName);
        videoInfo.setPCategoryId(Integer.valueOf(pCategoryId));
        videoInfo.setCategoryId(Integer.valueOf(categoryId));
        if (postType != null && !postType.isEmpty()) videoInfo.setPostType(Integer.valueOf(postType));
        videoInfo.setTags(tags != null ? tags : "");
        videoInfo.setIntroduction(introduction != null ? introduction : "");
        videoInfo.setInteraction(interaction != null ? interaction : "");
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        videoInfo.setUserId(tokenUserInfoDto.getUserId());
        List<String> fileList = new java.util.ArrayList<>();
        if (uploadFileList != null && !uploadFileList.trim().isEmpty()) {
            for (String fid : uploadFileList.split(",")) {
                if (!fid.trim().isEmpty()) {
                    fileList.add(fid.trim());
                }
            }
        }
        videoInfoService.postVideo(videoInfo, fileList);
        // 同期到 Elasticsearch
        esSearchService.indexVideo(videoInfo);
        return getSuccessResponseVO(videoInfo);
    }

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(String status,
                                    @NotEmpty String pageNo,
                                    String videoNameFuzzy) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoInfoQuery query = new VideoInfoQuery();
        if (status != null && !status.isEmpty()) {
            int s = Integer.parseInt(status);
            if (s >= 0) query.setStatus(s);
        }
        if (tokenUserInfoDto != null) {
            query.setUserId(tokenUserInfoDto.getUserId());
        }
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
        TokenUserInfoDto user = getTokenUserInfoDto();
        if (user == null) throw new BusinessException("请先登录");
        VideoInfo v = videoInfoService.getVideoInfo(videoId);
        if (v == null) throw new BusinessException("视频不存在");
        if (!user.getUserId().equals(v.getUserId())) throw new BusinessException("只能删除自己的视频");
        videoInfoService.deleteVideo(videoId);
        return getSuccessResponseVO(null);
    }
}
