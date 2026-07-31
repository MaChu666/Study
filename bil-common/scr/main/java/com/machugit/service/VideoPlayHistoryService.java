package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.VideoPlayHistory;


/**
 * 视频播放记录 业务接口
 */
public interface VideoPlayHistoryService {

    /**
     * 加载播放历史
     */
    List<VideoPlayHistory> loadHistory(String userId);

    /**
     * 删除播放历史
     */
    void delHistory(Integer historyId);

    /**
     * 清空播放历史
     */
    void cleanHistory(String userId);

    /**
     * 增加播放量
     */
    void addPlayCount(String videoId);
}
