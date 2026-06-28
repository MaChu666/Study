package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.machugit.entity.po.SeriesVideo;
import com.machugit.entity.po.VideoSeries;
import com.machugit.entity.query.SeriesVideoQuery;
import com.machugit.entity.query.VideoSeriesQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.SeriesVideoMapper;
import com.machugit.mappers.VideoSeriesMapper;

import org.springframework.stereotype.Service;

import com.machugit.service.VideoSeriesService;


/**
 * 视频系列表 业务接口实现
 */
@Service("videoSeriesService")
public class VideoSeriesServiceImpl implements VideoSeriesService {

    @Resource
    private VideoSeriesMapper<VideoSeries, VideoSeriesQuery> videoSeriesMapper;

    @Resource
    private SeriesVideoMapper<SeriesVideo, SeriesVideoQuery> seriesVideoMapper;

    /**
     * 获取用户系列列表
     */
    @Override
    public List<VideoSeries> loadVideoSeries(String userId) {
        VideoSeriesQuery query = new VideoSeriesQuery();
        query.setUserId(userId);
        query.setOrderBy("sort asc");
        return this.videoSeriesMapper.selectList(query);
    }

    /**
     * 获取系列下所有视频
     */
    @Override
    public List<SeriesVideo> loadAllVideo(Integer seriesId) {
        SeriesVideoQuery query = new SeriesVideoQuery();
        query.setSeriesId(seriesId);
        query.setOrderBy("sort asc");
        return this.seriesVideoMapper.selectList(query);
    }

    /**
     * 修改系列排序
     */
    @Override
    public void changeVideoSeriesSort(String seriesIds) {
        String[] ids = seriesIds.split(",");
        for (int i = 0; i < ids.length; i++) {
            Integer seriesId = Integer.parseInt(ids[i].trim());
            VideoSeries updateInfo = new VideoSeries();
            updateInfo.setSort(i + 1);
            this.videoSeriesMapper.updateBySeriesId(updateInfo, seriesId);
        }
    }

    /**
     * 删除系列及系列视频
     */
    @Override
    public void delVideoSeries(Integer seriesId) {
        SeriesVideoQuery seriesVideoQuery = new SeriesVideoQuery();
        seriesVideoQuery.setSeriesId(seriesId);
        this.seriesVideoMapper.deleteByParam(seriesVideoQuery);
        this.videoSeriesMapper.deleteBySeriesId(seriesId);
    }

    /**
     * 新增或更新系列
     */
    @Override
    public void saveVideoSeries(VideoSeries series) {
        series.setUpdateTime(new Date());
        if (series.getSeriesId() == null) {
            this.videoSeriesMapper.insert(series);
        } else {
            this.videoSeriesMapper.updateBySeriesId(series, series.getSeriesId());
        }
    }

    /**
     * 添加视频到系列
     */
    @Override
    public void saveSeriesVideo(Integer seriesId, String videoId) {
        SeriesVideoQuery query = new SeriesVideoQuery();
        query.setSeriesId(seriesId);
        query.setOrderBy("sort desc");
        List<SeriesVideo> list = this.seriesVideoMapper.selectList(query);
        Integer maxSort = 0;
        if (list != null && !list.isEmpty()) {
            maxSort = list.get(0).getSort() == null ? 0 : list.get(0).getSort();
        }
        SeriesVideo seriesVideo = new SeriesVideo();
        seriesVideo.setSeriesId(seriesId);
        seriesVideo.setVideoId(videoId);
        seriesVideo.setSort(maxSort + 1);
        this.seriesVideoMapper.insert(seriesVideo);
    }

    /**
     * 从系列中移除视频
     */
    @Override
    public void delSeriesVideo(Integer id) {
        this.seriesVideoMapper.deleteById(id);
    }

    /**
     * 获取所有系列及系列视频
     */
    @Override
    public List<VideoSeries> loadVideoSeriesWithVideo(String userId) {
        VideoSeriesQuery query = new VideoSeriesQuery();
        query.setUserId(userId);
        query.setOrderBy("sort asc");
        List<VideoSeries> seriesList = this.videoSeriesMapper.selectList(query);
        for (VideoSeries series : seriesList) {
            SeriesVideoQuery videoQuery = new SeriesVideoQuery();
            videoQuery.setSeriesId(series.getSeriesId());
            videoQuery.setOrderBy("sort asc");
            this.seriesVideoMapper.selectList(videoQuery);
        }
        return seriesList;
    }

    /**
     * 获取系列详情及系列视频
     */
    @Override
    public VideoSeries getVideoSeriesDetail(Integer seriesId) {
        VideoSeries series = this.videoSeriesMapper.selectBySeriesId(seriesId);
        if (series != null) {
            SeriesVideoQuery videoQuery = new SeriesVideoQuery();
            videoQuery.setSeriesId(seriesId);
            videoQuery.setOrderBy("sort asc");
            this.seriesVideoMapper.selectList(videoQuery);
        }
        return series;
    }

}
