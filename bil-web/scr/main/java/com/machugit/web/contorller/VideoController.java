package com.machugit.web.contorller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.po.VideoInfoFile;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.VideoInfoServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/video")
@Validated
public class VideoController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Resource
    private VideoInfoServiceImpl videoInfoService;

    /**
     * 加载推荐视频
     */
    @RequestMapping("/loadRecommendVideo")
    public ResponseVO loadRecommendVideo() {
        List<VideoInfo> list = videoInfoService.loadRecommendVideo();
        return getSuccessResponseVO(list);
    }

    /**
     * 分页加载视频
     */
    @RequestMapping("/loadVideo")
    public ResponseVO loadVideo(String pCategoryId, String categoryId, @NotEmpty String pageNo) {
        Integer pCat = (pCategoryId != null && !pCategoryId.isEmpty()) ? Integer.valueOf(pCategoryId) : null;
        Integer cat = (categoryId != null && !categoryId.isEmpty()) ? Integer.valueOf(categoryId) : null;
        PaginationResultVO<VideoInfo> result = videoInfoService.loadVideo(pCat, cat, Integer.valueOf(pageNo));
        return getSuccessResponseVO(result);
    }

    /**
     * 获取视频详情
     */
    @RequestMapping("/getVideoInfo")
    public ResponseVO getVideoInfo(@NotEmpty String videoId) {
        VideoInfo videoInfo = videoInfoService.getVideoInfo(videoId);
        return getSuccessResponseVO(videoInfo);
    }

    /**
     * 加载视频文件列表
     */
    @RequestMapping("/loadVideoPList")
    public ResponseVO loadVideoPList(@NotEmpty String videoId) {
        List<VideoInfoFile> list = videoInfoService.loadVideoPList(videoId);
        return getSuccessResponseVO(list);
    }

    /**
     * 搜索视频
     */
    @RequestMapping("/search")
    public ResponseVO search(@NotEmpty String keyword) {
        List<VideoInfo> list = videoInfoService.search(keyword);
        return getSuccessResponseVO(list);
    }

    /**
     * 获取搜索关键词排行
     */
    @RequestMapping("/getSearchKeywordTop")
    public ResponseVO getSearchKeywordTop() {
        List<String> list = videoInfoService.getSearchKeywordTop();
        return getSuccessResponseVO(list);
    }

    /**
     * 获取视频推荐
     */
    @RequestMapping("/getVideoRecommend")
    public ResponseVO getVideoRecommend(@NotEmpty String videoId) {
        List<VideoInfo> list = videoInfoService.getVideoRecommend(videoId);
        return getSuccessResponseVO(list);
    }

    /**
     * 加载热门视频列表
     */
    @RequestMapping("/loadHotVideoList")
    public ResponseVO loadHotVideoList() {
        List<VideoInfo> list = videoInfoService.loadHotVideoList();
        return getSuccessResponseVO(list);
    }

    /**
     * 上报视频在线播放
     */
    @RequestMapping("/reportVideoPlayOnline")
    public ResponseVO reportVideoPlayOnline(@NotEmpty String fileId, @NotEmpty String deviceId) {
        videoInfoService.reportVideoPlayOnline(fileId, deviceId);
        return getSuccessResponseVO(null);
    }
}
