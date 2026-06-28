package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 视频播放历史表
 */
public class VideoPlayHistory implements Serializable {


    /**
     * 历史记录id
     */
    private Integer historyId;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    public void setHistoryId(Integer historyId){
        this.historyId = historyId;
    }

    public Integer getHistoryId(){
        return this.historyId;
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

    public void setFileId(String fileId){
        this.fileId = fileId;
    }

    public String getFileId(){
        return this.fileId;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }

    public Date getUpdateTime(){
        return this.updateTime;
    }

    @Override
    public String toString (){
        return "历史记录id:"+(historyId == null ? "空" : historyId)+"，视频id:"+(videoId == null ? "空" : videoId)+"，用户id:"+(userId == null ? "空" : userId)+"，文件id:"+(fileId == null ? "空" : fileId)+"，更新时间:"+(updateTime == null ? "空" : DateUtil.format(updateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
