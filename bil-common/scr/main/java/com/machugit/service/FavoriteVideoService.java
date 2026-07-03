package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.FavoriteVideo;

/**
 * 收藏视频 业务接口
 */
public interface FavoriteVideoService {

    /**
     * 加载收藏夹下的所有视频
     */
    List<FavoriteVideo> loadVideos(Integer folderId);

    /**
     * 添加视频到收藏夹
     */
    void addVideo(Integer folderId, String userId, String videoId);

    /**
     * 从收藏夹移除视频
     */
    void removeVideo(Integer id);

}
