package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.ReportInfo;
import com.machugit.entity.query.ReportInfoQuery;
import com.machugit.mappers.ReportInfoMapper;
import com.machugit.service.ReportInfoService;

/**
 * 举报信息 业务接口实现
 */
@Service("reportInfoService")
public class ReportInfoServiceImpl implements ReportInfoService {

    @Resource
    private ReportInfoMapper<ReportInfo, ReportInfoQuery> reportInfoMapper;

    /**
     * 提交举报
     */
    @Override
    public void submitReport(ReportInfo report) {
        report.setCreateTime(new Date());
        report.setUpdateTime(new Date());
        this.reportInfoMapper.insert(report);
    }

    /**
     * 加载举报列表（管理端）
     */
    @Override
    public List<ReportInfo> loadReports(ReportInfoQuery query) {
        query.setOrderBy("create_time desc");
        return this.reportInfoMapper.selectList(query);
    }

    /**
     * 处理举报（管理端）
     */
    @Override
    public void handleReport(Integer reportId, Integer status, String handlerId, String handleResult) {
        ReportInfo updateInfo = new ReportInfo();
        updateInfo.setStatus(status);
        updateInfo.setHandlerId(handlerId);
        updateInfo.setHandleResult(handleResult);
        updateInfo.setUpdateTime(new Date());
        this.reportInfoMapper.updateByReportId(updateInfo, reportId);
    }

}
