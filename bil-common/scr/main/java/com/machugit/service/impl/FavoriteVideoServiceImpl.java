package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.FavoriteFolder;
import com.machugit.entity.po.FavoriteVideo;
import com.machugit.entity.query.FavoriteFolderQuery;
import com.machugit.entity.query.FavoriteVideoQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.FavoriteFolderMapper;
import com.machugit.mappers.FavoriteVideoMapper;
import com.machugit.service.FavoriteVideoService;

/**
 * 收藏视频 业务接口实现
 */
@Service("favoriteVideoService")
public class FavoriteVideoServiceImpl implements FavoriteVideoService {

    @Resource
    private FavoriteVideoMapper<FavoriteVideo, FavoriteVideoQuery> favoriteVideoMapper;

    @Resource
    private FavoriteFolderMapper<FavoriteFolder, FavoriteFolderQuery> favoriteFolderMapper;

    /**
     * 加载收藏夹下的所有视频
     */
    @Override
    public List<FavoriteVideo> loadVideos(Integer folderId) {
        FavoriteVideoQuery query = new FavoriteVideoQuery();
        query.setFolderId(folderId);
        query.setOrderBy("create_time desc");
        return this.favoriteVideoMapper.selectList(query);
    }

    /**
     * 添加视频到收藏夹
     */
    @Override
    public void addVideo(Integer folderId, String userId, String videoId) {
        FavoriteVideoQuery checkQuery = new FavoriteVideoQuery();
        checkQuery.setFolderId(folderId);
        checkQuery.setVideoId(videoId);
        List<FavoriteVideo> existingList = this.favoriteVideoMapper.selectList(checkQuery);
        if (existingList != null && !existingList.isEmpty()) {
            throw new BusinessException("视频已在收藏夹中");
        }

        FavoriteVideo video = new FavoriteVideo();
        video.setFolderId(folderId);
        video.setUserId(userId);
        video.setVideoId(videoId);
        video.setCreateTime(new Date());
        this.favoriteVideoMapper.insert(video);

        FavoriteFolder folder = this.favoriteFolderMapper.selectByFolderId(folderId);
        if (folder != null) {
            FavoriteFolder updateFolder = new FavoriteFolder();
            Integer currentCount = folder.getVideoCount();
            if (currentCount == null) {
                currentCount = 0;
            }
            updateFolder.setVideoCount(currentCount + 1);
            updateFolder.setUpdateTime(new Date());
            this.favoriteFolderMapper.updateByFolderId(updateFolder, folderId);
        }
    }

    /**
     * 从收藏夹移除视频
     */
    @Override
    public void removeVideo(Integer id) {
        FavoriteVideo video = this.favoriteVideoMapper.selectById(id);
        if (video == null) {
            return;
        }
        this.favoriteVideoMapper.deleteById(id);

        FavoriteFolder folder = this.favoriteFolderMapper.selectByFolderId(video.getFolderId());
        if (folder != null) {
            FavoriteFolder updateFolder = new FavoriteFolder();
            Integer currentCount = folder.getVideoCount();
            if (currentCount != null && currentCount > 0) {
                updateFolder.setVideoCount(currentCount - 1);
            }
            updateFolder.setUpdateTime(new Date());
            this.favoriteFolderMapper.updateByFolderId(updateFolder, video.getFolderId());
        }
    }

}
