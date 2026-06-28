package com.machugit.admin.controller;

import java.util.HashMap;
import java.util.Map;

import com.machugit.entity.query.UserInfoQuery;
import com.machugit.entity.query.VideoInfoQuery;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.UserInfoServiceImpl;
import com.machugit.service.impl.VideoInfoServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/index")
@Validated
public class AdminIndexController extends ABaseAdminController {

    @Resource
    private VideoInfoServiceImpl videoInfoService;

    @Resource
    private UserInfoServiceImpl userInfoService;

    @RequestMapping("/getActualTimeStatisticsInfo")
    public ResponseVO getActualTimeStatisticsInfo() {
        Map<String, Object> result = new HashMap<>();

        Integer totalUserCount = userInfoService.findCountByParam(new UserInfoQuery());
        result.put("totalUserCount", totalUserCount);

        VideoInfoQuery videoQuery = new VideoInfoQuery();
        videoQuery.setPageNo(1);
        PaginationResultVO videoResult = videoInfoService.loadVideoList(videoQuery);
        result.put("totalVideoCount", videoResult.getTotalCount());

        result.put("totalPlayCount", 0);

        result.put("todayAddUserCount", 0);
        result.put("todayAddVideoCount", 0);

        return getSuccessResponseVO(result);
    }

    @RequestMapping("/getWeekStatisticsInfo")
    public ResponseVO getWeekStatisticsInfo() {
        Map<String, Object> result = new HashMap<>();

        result.put("weekUserCounts", new int[]{0, 0, 0, 0, 0, 0, 0});
        result.put("weekVideoCounts", new int[]{0, 0, 0, 0, 0, 0, 0});
        result.put("weekPlayCounts", new int[]{0, 0, 0, 0, 0, 0, 0});

        return getSuccessResponseVO(result);
    }
}
