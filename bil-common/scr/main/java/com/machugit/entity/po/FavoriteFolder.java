package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;


/**
 * 收藏夹表
 */
public class FavoriteFolder implements Serializable {


    /**
     * 收藏夹id
     */
    private Integer folderId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 收藏夹名称
     */
    private String folderName;

    /**
     * 收藏夹描述
     */
    private String description;

    /**
     * 封面图片
     */
    private String coverImage;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


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

    public void setFolderName(String folderName){
        this.folderName = folderName;
    }

    public String getFolderName(){
        return this.folderName;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }

    public void setCoverImage(String coverImage){
        this.coverImage = coverImage;
    }

    public String getCoverImage(){
        return this.coverImage;
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

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }

    public Date getUpdateTime(){
        return this.updateTime;
    }

    @Override
    public String toString (){
        return "收藏夹id:"+(folderId == null ? "空" : folderId)+"，用户id:"+(userId == null ? "空" : userId)+"，收藏夹名称:"+(folderName == null ? "空" : folderName)+"，收藏夹描述:"+(description == null ? "空" : description)+"，封面图片:"+(coverImage == null ? "空" : coverImage)+"，类型:"+(type == null ? "空" : type)+"，视频数量:"+(videoCount == null ? "空" : videoCount)+"，排序:"+(sort == null ? "空" : sort)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，更新时间:"+(updateTime == null ? "空" : DateUtil.format(updateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
