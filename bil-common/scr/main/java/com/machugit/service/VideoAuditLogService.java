package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.VideoAuditLog;

/**
 * 视频审核日志 业务接口
 */
public interface VideoAuditLogService {

    /**
     * 记录审核日志
     */
    void logAudit(String videoId, String auditorId, Integer fromStatus, Integer toStatus, String reason);

    /**
     * 加载视频的审核日志
     */
    List<VideoAuditLog> loadAuditLogs(String videoId);

}
