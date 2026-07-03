package com.machugit.entity.query;

import java.util.Date;


/**
 * 收藏夹表参数
 */
public class FavoriteFolderQuery extends BaseParam {


    /**
     * 收藏夹id
     */
    private Integer folderId;

    /**
     * 用户id
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 收藏夹名称
     */
    private String folderName;

    private String folderNameFuzzy;

    /**
     * 收藏夹描述
     */
    private String description;

    private String descriptionFuzzy;

    /**
     * 封面图片
     */
    private String coverImage;

    private String coverImageFuzzy;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 视频数量
     */
    private Integer videoCount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    /**
     * 更新时间
     */
    private String updateTime;

    private String updateTimeStart;

    private String updateTimeEnd;


    public void setFolderId(Integer folderId){
        this.folderId = folderId;
    }

    public Integer getFolderId(){
        return this.folderId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setUserIdFuzzy(String userIdFuzzy){
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getUserIdFuzzy(){
        return this.userIdFuzzy;
    }

    public void setFolderName(String folderName){
        this.folderName = folderName;
    }

    public String getFolderName(){
        return this.folderName;
    }

    public void setFolderNameFuzzy(String folderNameFuzzy){
        this.folderNameFuzzy = folderNameFuzzy;
    }

    public String getFolderNameFuzzy(){
        return this.folderNameFuzzy;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescriptionFuzzy(String descriptionFuzzy){
        this.descriptionFuzzy = descriptionFuzzy;
    }

    public String getDescriptionFuzzy(){
        return this.descriptionFuzzy;
    }

    public void setCoverImage(String coverImage){
        this.coverImage = coverImage;
    }

    public String getCoverImage(){
        return this.coverImage;
    }

    public void setCoverImageFuzzy(String coverImageFuzzy){
        this.coverImageFuzzy = coverImageFuzzy;
    }

    public String getCoverImageFuzzy(){
        return this.coverImageFuzzy;
    }

    public void setType(Integer type){
        this.type = type;
    }

    public Integer getType(){
        return this.type;
    }

    public void setVideoCount(Integer videoCount){
        this.videoCount = videoCount;
    }

    public Integer getVideoCount(){
        return this.videoCount;
    }

    public void setSort(Integer sort){
        this.sort = sort;
    }

    public Integer getSort(){
        return this.sort;
    }

    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }

    public String getCreateTime(){
        return this.createTime;
    }

    public void setCreateTimeStart(String createTimeStart){
        this.createTimeStart = createTimeStart;
    }

    public String getCreateTimeStart(){
        return this.createTimeStart;
    }
    public void setCreateTimeEnd(String createTimeEnd){
        this.createTimeEnd = createTimeEnd;
    }

    public String getCreateTimeEnd(){
        return this.createTimeEnd;
    }

    public void setUpdateTime(String updateTime){
        this.updateTime = updateTime;
    }

    public String getUpdateTime(){
        return this.updateTime;
    }

    public void setUpdateTimeStart(String updateTimeStart){
        this.updateTimeStart = updateTimeStart;
    }

    public String getUpdateTimeStart(){
        return this.updateTimeStart;
    }
    public void setUpdateTimeEnd(String updateTimeEnd){
        this.updateTimeEnd = updateTimeEnd;
    }

    public String getUpdateTimeEnd(){
        return this.updateTimeEnd;
    }

}
