package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.VideoAuditLog;
import com.machugit.entity.query.VideoAuditLogQuery;
import com.machugit.mappers.VideoAuditLogMapper;
import com.machugit.service.VideoAuditLogService;

/**
 * 视频审核日志 业务接口实现
 */
@Service("videoAuditLogService")
public class VideoAuditLogServiceImpl implements VideoAuditLogService {

    @Resource
    private VideoAuditLogMapper<VideoAuditLog, VideoAuditLogQuery> videoAuditLogMapper;

    /**
     * 记录审核日志
     */
    @Override
    public void logAudit(String videoId, String auditorId, Integer fromStatus, Integer toStatus, String reason) {
        VideoAuditLog auditLog = new VideoAuditLog();
        auditLog.setVideoId(videoId);
        auditLog.setAuditorId(auditorId);
        auditLog.setFromStatus(fromStatus);
        auditLog.setToStatus(toStatus);
        auditLog.setReason(reason);
        auditLog.setCreateTime(new Date());
        this.videoAuditLogMapper.insert(auditLog);
    }

    /**
     * 加载视频的审核日志
     */
    @Override
    public List<VideoAuditLog> loadAuditLogs(String videoId) {
        VideoAuditLogQuery query = new VideoAuditLogQuery();
        query.setVideoId(videoId);
        query.setOrderBy("create_time desc");
        return this.videoAuditLogMapper.selectList(query);
    }

}
