package com.machugit.web.contorller;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.machugit.entity.po.VideoSeries;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.VideoSeriesServiceImpl;

@RestController
@RequestMapping("/series")
@Validated
public class SeriesController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(SeriesController.class);

    @Resource
    private VideoSeriesServiceImpl videoSeriesService;

    @RequestMapping("/getVideoSeriesDetail")
    public ResponseVO getVideoSeriesDetail(@NotEmpty String seriesId) {
        Integer seriesIdInt = Integer.parseInt(seriesId);
        VideoSeries series = videoSeriesService.getVideoSeriesDetail(seriesIdInt);
        return getSuccessResponseVO(series);
    }
}
