package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.VideoInfo;
import com.machugit.entity.po.VideoInfoFile;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;

/**
 * 视频信息 业务接口
 */
public interface VideoInfoService {

    /**
     * 加载推荐视频
     */
    List<VideoInfo> loadRecommendVideo();

    /**
     * 分页加载视频
     */
    PaginationResultVO<VideoInfo> loadVideo(Integer pCategoryId, Integer categoryId, Integer pageNo);

    /**
     * 分页加载视频（支持排序，orderBy 仅接受白名单值）
     */
    PaginationResultVO<VideoInfo> loadVideo(Integer pCategoryId, Integer categoryId, Integer pageNo, String orderBy);

    /**
     * 获取视频详情
     */
    VideoInfo getVideoInfo(String videoId);

    /**
     * 加载视频文件列表
     */
    List<VideoInfoFile> loadVideoPList(String videoId);

    /**
     * 搜索视频
     */
    List<VideoInfo> search(String keyword);

    /**
     * 获取搜索关键词排行
     */
    List<String> getSearchKeywordTop();

    /**
     * 获取视频推荐
     */
    List<VideoInfo> getVideoRecommend(String videoId);

    /**
     * 加载热门视频列表
     */
    List<VideoInfo> loadHotVideoList();

    /**
     * 上报视频在线播放
     */
    void reportVideoPlayOnline(String fileId, String deviceId);

    /**
     * 分页查询视频列表（后台管理）
     */
    PaginationResultVO<VideoInfo> loadVideoList(VideoInfoQuery query);

    /**
     * 审核视频
     */
    void auditVideo(String videoId, Integer status, String reason);

    /**
     * 删除视频
     */
    void deleteVideo(String videoId);

    /**
     * 发布视频
     */
    void postVideo(VideoInfo videoInfo, List<String> uploadFileList);

    /**
     * 推荐视频
     */
    void recommendVideo(String videoId);

    /**
     * 投币增加
     */
    void addCoinCount(String videoId, Integer count);

}

