package com.machugit.web.contorller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.machugit.entity.po.SeriesVideo;
import com.machugit.entity.po.VideoSeries;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.VideoSeriesServiceImpl;

@RestController
@RequestMapping("/uhome/series")
@Validated
public class UHomeSeriesController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(UHomeSeriesController.class);

    @Resource
    private VideoSeriesServiceImpl videoSeriesService;

    @RequestMapping("/loadVideoSeries")
    public ResponseVO loadVideoSeries(@NotEmpty String userId) {
        List<VideoSeries> list = videoSeriesService.loadVideoSeries(userId);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/loadAllVideo")
    public ResponseVO loadAllVideo(@NotEmpty String seriesId) {
        Integer seriesIdInt = Integer.parseInt(seriesId);
        List<SeriesVideo> list = videoSeriesService.loadAllVideo(seriesIdInt);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/changeVideoSeriesSort")
    public ResponseVO changeVideoSeriesSort(@NotEmpty String seriesIds) {
        videoSeriesService.changeVideoSeriesSort(seriesIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delVideoSeries")
    public ResponseVO delVideoSeries(@NotEmpty String seriesId) {
        Integer seriesIdInt = Integer.parseInt(seriesId);
        videoSeriesService.delVideoSeries(seriesIdInt);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/saveVideoSeries")
    public ResponseVO saveVideoSeries(@NotEmpty String seriesId,
                                      @NotEmpty String seriesName,
                                      @NotEmpty String seriesDescription,
                                      @NotEmpty String userId) {
        VideoSeries series = new VideoSeries();
        series.setSeriesName(seriesName);
        series.setSeriesDescription(seriesDescription);
        series.setUserId(userId);
        if (!"0".equals(seriesId)) {
            series.setSeriesId(Integer.parseInt(seriesId));
        }
        videoSeriesService.saveVideoSeries(series);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/saveSeriesVideo")
    public ResponseVO saveSeriesVideo(@NotEmpty String seriesId,
                                      @NotEmpty String videoId) {
        Integer seriesIdInt = Integer.parseInt(seriesId);
        videoSeriesService.saveSeriesVideo(seriesIdInt, videoId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delSeriesVideo")
    public ResponseVO delSeriesVideo(@NotEmpty String id) {
        Integer idInt = Integer.parseInt(id);
        videoSeriesService.delSeriesVideo(idInt);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideoSeriesWithVideo")
    public ResponseVO loadVideoSeriesWithVideo(@NotEmpty String userId) {
        List<VideoSeries> list = videoSeriesService.loadVideoSeriesWithVideo(userId);
        return getSuccessResponseVO(list);
    }
}
