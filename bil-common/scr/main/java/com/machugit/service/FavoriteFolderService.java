package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.FavoriteFolder;

/**
 * 收藏夹 业务接口
 */
public interface FavoriteFolderService {

    /**
     * 加载用户所有收藏夹
     */
    List<FavoriteFolder> loadFolders(String userId);

    /**
     * 保存收藏夹
     */
    FavoriteFolder saveFolder(FavoriteFolder folder);

    /**
     * 删除收藏夹
     */
    void delFolder(Integer folderId);

}
