package com.machugit.admin.controller;

import java.util.List;

import com.machugit.entity.po.ReportInfo;
import com.machugit.entity.query.ReportInfoQuery;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.ReportInfoServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/report")
@Validated
public class AdminReportController extends ABaseAdminController {

    @Resource
    private ReportInfoServiceImpl reportInfoService;

    @RequestMapping("/loadReports")
    public ResponseVO loadReports(String status) {
        ReportInfoQuery query = new ReportInfoQuery();
        if (status != null && !status.isEmpty()) {
            query.setStatus(Integer.valueOf(status));
        }
        List<ReportInfo> list = reportInfoService.loadReports(query);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/handleReport")
    public ResponseVO handleReport(String reportId,
                                    String status,
                                    String handleResult) {
        String handlerId = getTokenUserInfoDto();
        reportInfoService.handleReport(Integer.valueOf(reportId),
                Integer.valueOf(status),
                handlerId,
                handleResult);
        return getSuccessResponseVO(null);
    }
}
