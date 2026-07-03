package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;


/**
 * 用户动态表
 */
public class UserDynamic implements Serializable {


    /**
     * 动态id
     */
    private Integer dynamicId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名（JOIN查询，非DB字段）
     */
    private String userName;

    /**
     * 用户头像（JOIN查询，非DB字段）
     */
    private String userAvatar;

    /**
     * 动态类型
     */
    private Integer dynamicType;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片
     */
    private String images;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 转发id
     */
    private Integer forwardId;

    /**
     * 转发数
     */
    private Integer forwardCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

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


    public void setDynamicId(Integer dynamicId){
        this.dynamicId = dynamicId;
    }

    public Integer getDynamicId(){
        return this.dynamicId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setUserName(String userName){ this.userName = userName; }
    public String getUserName(){ return this.userName; }
    public void setUserAvatar(String userAvatar){ this.userAvatar = userAvatar; }
    public String getUserAvatar(){ return this.userAvatar; }

    public void setDynamicType(Integer dynamicType){
        this.dynamicType = dynamicType;
    }

    public Integer getDynamicType(){
        return this.dynamicType;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return this.content;
    }

    public void setImages(String images){
        this.images = images;
    }

    public String getImages(){
        return this.images;
    }

    public void setVideoId(String videoId){
        this.videoId = videoId;
    }

    public String getVideoId(){
        return this.videoId;
    }

    public void setForwardId(Integer forwardId){
        this.forwardId = forwardId;
    }

    public Integer getForwardId(){
        return this.forwardId;
    }

    public void setForwardCount(Integer forwardCount){
        this.forwardCount = forwardCount;
    }

    public Integer getForwardCount(){
        return this.forwardCount;
    }

    public void setLikeCount(Integer likeCount){
        this.likeCount = likeCount;
    }

    public Integer getLikeCount(){
        return this.likeCount;
    }

    public void setCommentCount(Integer commentCount){
        this.commentCount = commentCount;
    }

    public Integer getCommentCount(){
        return this.commentCount;
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
        return "动态id:"+(dynamicId == null ? "空" : dynamicId)+"，用户id:"+(userId == null ? "空" : userId)+"，动态类型:"+(dynamicType == null ? "空" : dynamicType)+"，内容:"+(content == null ? "空" : content)+"，图片:"+(images == null ? "空" : images)+"，视频id:"+(videoId == null ? "空" : videoId)+"，转发id:"+(forwardId == null ? "空" : forwardId)+"，转发数:"+(forwardCount == null ? "空" : forwardCount)+"，点赞数:"+(likeCount == null ? "空" : likeCount)+"，评论数:"+(commentCount == null ? "空" : commentCount)+"，状态:"+(status == null ? "空" : status)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，更新时间:"+(updateTime == null ? "空" : DateUtil.format(updateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
