package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.ReportInfo;
import com.machugit.entity.query.ReportInfoQuery;

/**
 * 举报信息 业务接口
 */
public interface ReportInfoService {

    /**
     * 提交举报
     */
    void submitReport(ReportInfo report);

    /**
     * 加载举报列表（管理端）
     */
    List<ReportInfo> loadReports(ReportInfoQuery query);

    /**
     * 处理举报（管理端）
     */
    void handleReport(Integer reportId, Integer status, String handlerId, String handleResult);

}
