package com.machugit.entity.po;

import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 视频信息表
 */
public class VideoInfo implements Serializable {


    /**
     * 视频id
     */
    private String videoId;

    /**
     * 视频封面
     */
    private String videoCover;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 父分类id
     */
    private Integer pCategoryId;

    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 投稿类型
     */
    private Integer postType;

    /**
     * 标签
     */
    private String tags;

    /**
     * 视频简介
     */
    private String introduction;

    /**
     * 互动设置
     */
    private String interaction;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 播放量
     */
    private Long playCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 弹幕数
     */
    private Long danmuCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 投币数
     */
    private Long coinCount;

    /**
     * 收藏数
     */
    private Long collectCount;

    /**
     * 状态
     */
    private Integer status;

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


    public void setVideoId(String videoId){
        this.videoId = videoId;
    }

    public String getVideoId(){
        return this.videoId;
    }

    public void setVideoCover(String videoCover){
        this.videoCover = videoCover;
    }

    public String getVideoCover(){
        return this.videoCover;
    }

    public void setVideoName(String videoName){
        this.videoName = videoName;
    }

    public String getVideoName(){
        return this.videoName;
    }

    public void setPCategoryId(Integer pCategoryId){
        this.pCategoryId = pCategoryId;
    }

    public Integer getPCategoryId(){
        return this.pCategoryId;
    }

    public void setCategoryId(Integer categoryId){
        this.categoryId = categoryId;
    }

    public Integer getCategoryId(){
        return this.categoryId;
    }

    public void setPostType(Integer postType){
        this.postType = postType;
    }

    public Integer getPostType(){
        return this.postType;
    }

    public void setTags(String tags){
        this.tags = tags;
    }

    public String getTags(){
        return this.tags;
    }

    public void setIntroduction(String introduction){
        this.introduction = introduction;
    }

    public String getIntroduction(){
        return this.introduction;
    }

    public void setInteraction(String interaction){
        this.interaction = interaction;
    }

    public String getInteraction(){
        return this.interaction;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setPlayCount(Long playCount){
        this.playCount = playCount;
    }

    public Long getPlayCount(){
        return this.playCount;
    }

    public void setLikeCount(Long likeCount){
        this.likeCount = likeCount;
    }

    public Long getLikeCount(){
        return this.likeCount;
    }

    public void setDanmuCount(Long danmuCount){
        this.danmuCount = danmuCount;
    }

    public Long getDanmuCount(){
        return this.danmuCount;
    }

    public void setCommentCount(Long commentCount){
        this.commentCount = commentCount;
    }

    public Long getCommentCount(){
        return this.commentCount;
    }

    public void setCoinCount(Long coinCount){
        this.coinCount = coinCount;
    }

    public Long getCoinCount(){
        return this.coinCount;
    }

    public void setCollectCount(Long collectCount){
        this.collectCount = collectCount;
    }

    public Long getCollectCount(){
        return this.collectCount;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus(){
        return this.status;
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
        return "视频id:"+(videoId == null ? "空" : videoId)+"，视频封面:"+(videoCover == null ? "空" : videoCover)+"，视频名称:"+(videoName == null ? "空" : videoName)+"，父分类id:"+(pCategoryId == null ? "空" : pCategoryId)+"，分类id:"+(categoryId == null ? "空" : categoryId)+"，投稿类型:"+(postType == null ? "空" : postType)+"，标签:"+(tags == null ? "空" : tags)+"，视频简介:"+(introduction == null ? "空" : introduction)+"，互动设置:"+(interaction == null ? "空" : interaction)+"，用户id:"+(userId == null ? "空" : userId)+"，播放量:"+(playCount == null ? "空" : playCount)+"，点赞数:"+(likeCount == null ? "空" : likeCount)+"，弹幕数:"+(danmuCount == null ? "空" : danmuCount)+"，评论数:"+(commentCount == null ? "空" : commentCount)+"，投币数:"+(coinCount == null ? "空" : coinCount)+"，收藏数:"+(collectCount == null ? "空" : collectCount)+"，状态:"+(status == null ? "空" : status)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，更新时间:"+(updateTime == null ? "空" : DateUtil.format(updateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
