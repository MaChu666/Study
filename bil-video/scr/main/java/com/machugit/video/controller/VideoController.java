package com.machugit.video.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.po.VideoInfoFile;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.vo.ResponseVO;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.machugit.sentinel.SentinelBlockHandler;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.VideoInfoServiceImpl;
import com.machugit.es.EsSearchService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import com.machugit.entity.es.VideoDoc;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/video")
@Validated
public class VideoController extends com.machugit.controller.ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Resource
    private EsSearchService esSearchService;

    @Resource
    private VideoInfoServiceImpl videoInfoService;

    /**
     * 加载推荐视频
     */
    @RequestMapping("/loadRecommendVideo")
    @SentinelResource(value = "video:loadRecommend", blockHandlerClass = SentinelBlockHandler.class, blockHandler = "handleBlock")
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
    @SentinelResource(value = "video:search", blockHandlerClass = SentinelBlockHandler.class, blockHandler = "handleBlock")
    public ResponseVO search(@NotEmpty String keyword) {
        List<VideoDoc> docs = esSearchService.searchVideo(keyword, 0, 20);
        if (docs != null && !docs.isEmpty()) {
            List<Map<String, Object>> results = new java.util.ArrayList<>();
            for (VideoDoc doc : docs) {
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("videoId", doc.getVideoId());
                item.put("videoName", doc.getVideoName());
                item.put("videoCover", doc.getVideoCover());
                item.put("userId", doc.getUserId());
                item.put("userName", doc.getUserName());
                item.put("userAvatar", doc.getUserAvatar());
                item.put("playCount", doc.getPlayCount());
                item.put("likeCount", doc.getLikeCount());
                item.put("danmuCount", doc.getDanmuCount());
                item.put("commentCount", doc.getCommentCount());
                item.put("tags", doc.getTags());
                item.put("introduction", doc.getIntroduction());
                item.put("createTime", doc.getCreateTime());
                results.add(item);
            }
            return getSuccessResponseVO(results);
        }
        // Fallback to MySQL
        List<VideoInfo> list = videoInfoService.search(keyword);
        return getSuccessResponseVO(list);
    }

    /**
    /**
     * 搜索联想建议
     */
    @RequestMapping("/suggest")
    public ResponseVO suggest(@NotEmpty String keyword) {
        List<String> suggestions = esSearchService.suggestVideo(keyword, 10);
        return getSuccessResponseVO(suggestions);
    }
    /**
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
