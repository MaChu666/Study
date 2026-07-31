package com.machugit.interaction.controller;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.po.FavoriteFolder;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.FavoriteFolderServiceImpl;
import com.machugit.service.impl.FavoriteVideoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/favorite")
@Validated
public class FavoriteController extends com.machugit.controller.ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    @Resource
    private FavoriteFolderServiceImpl favoriteFolderService;

    @Resource
    private FavoriteVideoServiceImpl favoriteVideoService;

    @RequestMapping("/loadFolders")
    public ResponseVO loadFolders() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        return getSuccessResponseVO(favoriteFolderService.loadFolders(userId));
    }

    @RequestMapping("/saveFolder")
    public ResponseVO saveFolder(String folderName, String description, Integer type) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        FavoriteFolder folder = new FavoriteFolder();
        folder.setUserId(userId);
        folder.setFolderName(folderName);
        folder.setDescription(description);
        folder.setType(type);
        return getSuccessResponseVO(favoriteFolderService.saveFolder(folder));
    }

    @RequestMapping("/delFolder")
    public ResponseVO delFolder(Integer folderId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        favoriteFolderService.delFolder(folderId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideos")
    public ResponseVO loadVideos(Integer folderId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        return getSuccessResponseVO(favoriteVideoService.loadVideos(folderId));
    }

    @RequestMapping("/addVideo")
    public ResponseVO addVideo(Integer folderId, String videoId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        String userId = tokenUserInfoDto.getUserId();
        favoriteVideoService.addVideo(folderId, userId, videoId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/removeVideo")
    public ResponseVO removeVideo(Integer id) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            throw new BusinessException("请先登录");
        }
        favoriteVideoService.removeVideo(id);
        return getSuccessResponseVO(null);
    }
}
