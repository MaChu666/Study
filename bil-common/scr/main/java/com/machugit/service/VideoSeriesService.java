package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.SeriesVideo;
import com.machugit.entity.po.VideoSeries;


/**
 * 视频系列表 业务接口
 */
public interface VideoSeriesService {

    /**
     * 获取用户系列列表
     */
    List<VideoSeries> loadVideoSeries(String userId);

    /**
     * 获取系列下所有视频
     */
    List<SeriesVideo> loadAllVideo(Integer seriesId);

    /**
     * 修改系列排序
     */
    void changeVideoSeriesSort(String seriesIds);

    /**
     * 删除系列及系列视频
     */
    void delVideoSeries(Integer seriesId);

    /**
     * 新增或更新系列
     */
    void saveVideoSeries(VideoSeries series);

    /**
     * 添加视频到系列
     */
    void saveSeriesVideo(Integer seriesId, String videoId);

    /**
     * 从系列中移除视频
     */
    void delSeriesVideo(Integer id);

    /**
     * 获取所有系列及系列视频
     */
    List<VideoSeries> loadVideoSeriesWithVideo(String userId);

    /**
     * 获取系列详情及系列视频
     */
    VideoSeries getVideoSeriesDetail(Integer seriesId);

}
