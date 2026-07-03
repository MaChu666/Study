package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.FavoriteFolder;
import com.machugit.entity.po.FavoriteVideo;
import com.machugit.entity.query.FavoriteFolderQuery;
import com.machugit.entity.query.FavoriteVideoQuery;
import com.machugit.mappers.FavoriteFolderMapper;
import com.machugit.mappers.FavoriteVideoMapper;
import com.machugit.service.FavoriteFolderService;

/**
 * 收藏夹 业务接口实现
 */
@Service("favoriteFolderService")
public class FavoriteFolderServiceImpl implements FavoriteFolderService {

    @Resource
    private FavoriteFolderMapper<FavoriteFolder, FavoriteFolderQuery> favoriteFolderMapper;

    @Resource
    private FavoriteVideoMapper<FavoriteVideo, FavoriteVideoQuery> favoriteVideoMapper;

    /**
     * 加载用户所有收藏夹
     */
    @Override
    public List<FavoriteFolder> loadFolders(String userId) {
        FavoriteFolderQuery query = new FavoriteFolderQuery();
        query.setUserId(userId);
        query.setOrderBy("sort asc");
        return this.favoriteFolderMapper.selectList(query);
    }

    /**
     * 保存收藏夹
     */
    @Override
    public FavoriteFolder saveFolder(FavoriteFolder folder) {
        if (folder.getFolderId() == null) {
            folder.setCreateTime(new Date());
            folder.setUpdateTime(new Date());
            this.favoriteFolderMapper.insert(folder);
        } else {
            folder.setUpdateTime(new Date());
            this.favoriteFolderMapper.updateByFolderId(folder, folder.getFolderId());
        }
        return folder;
    }

    /**
     * 删除收藏夹
     */
    @Override
    public void delFolder(Integer folderId) {
        FavoriteVideoQuery videoQuery = new FavoriteVideoQuery();
        videoQuery.setFolderId(folderId);
        this.favoriteVideoMapper.deleteByParam(videoQuery);
        this.favoriteFolderMapper.deleteByFolderId(folderId);
    }

}
