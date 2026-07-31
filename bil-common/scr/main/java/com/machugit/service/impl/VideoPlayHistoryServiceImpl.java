package com.machugit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.VideoPlayHistory;
import com.machugit.entity.query.VideoPlayHistoryQuery;
import com.machugit.mappers.VideoPlayHistoryMapper;
import com.machugit.service.VideoPlayHistoryService;


/**
 * 视频播放记录 业务接口实现
 */
@Service("videoPlayHistoryService")
public class VideoPlayHistoryServiceImpl implements VideoPlayHistoryService {

    @Resource
    private VideoPlayHistoryMapper<VideoPlayHistory, VideoPlayHistoryQuery> videoPlayHistoryMapper;

    /**
     * 加载播放历史
     */
    @Override
    public List<VideoPlayHistory> loadHistory(String userId) {
        VideoPlayHistoryQuery query = new VideoPlayHistoryQuery();
        query.setUserId(userId);
        query.setOrderBy("update_time desc");
        return this.videoPlayHistoryMapper.selectList(query);
    }

    /**
     * 删除播放历史
     */
    @Override
    public void delHistory(Integer historyId) {
        this.videoPlayHistoryMapper.deleteByHistoryId(historyId);
    }

    /**
     * 清空播放历史
     */
    @Override
    public void cleanHistory(String userId) {
        VideoPlayHistoryQuery query = new VideoPlayHistoryQuery();
        query.setUserId(userId);
        this.videoPlayHistoryMapper.deleteByParam(query);
    }

    @Override
    public void addPlayCount(String videoId) {
        // TODO: implement add play count logic
    }
}