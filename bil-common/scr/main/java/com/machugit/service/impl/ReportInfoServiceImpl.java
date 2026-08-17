package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.ReportInfo;
import com.machugit.entity.query.ReportInfoQuery;
import com.machugit.mappers.ReportInfoMapper;
import com.machugit.mappers.DanmuInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.machugit.service.ReportInfoService;

/**
 * 举报信息 业务接口实现
 */
@Service("reportInfoService")
public class ReportInfoServiceImpl implements ReportInfoService {

    @Resource
    private ReportInfoMapper<ReportInfo, ReportInfoQuery> reportInfoMapper;

    @Resource
    private DanmuInfoMapper<com.machugit.entity.po.DanmuInfo, com.machugit.entity.query.DanmuInfoQuery> danmuInfoMapper;

    private static final Logger logger = LoggerFactory.getLogger(ReportInfoServiceImpl.class);

    /**
     * 提交举报
     */
    @Override
    public void submitReport(ReportInfo report) {
        // 举报目标是弹幕时，弹幕标记次数加一
        if (report.getTargetType() != null && report.getTargetType() == 3 && report.getTargetId() != null) {
            try {
                danmuInfoMapper.incrementReportCountByDanmuId(Integer.valueOf(report.getTargetId()));
            } catch (Exception e) {
                logger.error("弹幕举报标记失败, targetId:{}", report.getTargetId(), e);
            }
        }
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
