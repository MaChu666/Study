package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 用户行为表
 */
public class UserAction implements Serializable {


    /**
     * 行为id
     */
    private Integer actionId;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 行为类型
     */
    private Integer actionType;

    /**
     * 行为计数
     */
    private Integer actionCount;

    /**
     * 评论id
     */
    private Integer commentId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    public void setActionId(Integer actionId){
        this.actionId = actionId;
    }

    public Integer getActionId(){
        return this.actionId;
    }

    public void setVideoId(String videoId){
        this.videoId = videoId;
    }

    public String getVideoId(){
        return this.videoId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setActionType(Integer actionType){
        this.actionType = actionType;
    }

    public Integer getActionType(){
        return this.actionType;
    }

    public void setActionCount(Integer actionCount){
        this.actionCount = actionCount;
    }

    public Integer getActionCount(){
        return this.actionCount;
    }

    public void setCommentId(Integer commentId){
        this.commentId = commentId;
    }

    public Integer getCommentId(){
        return this.commentId;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "行为id:"+(actionId == null ? "空" : actionId)+"，视频id:"+(videoId == null ? "空" : videoId)+"，用户id:"+(userId == null ? "空" : userId)+"，行为类型:"+(actionType == null ? "空" : actionType)+"，行为计数:"+(actionCount == null ? "空" : actionCount)+"，评论id:"+(commentId == null ? "空" : commentId)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
