package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 弹幕信息表
 */
public class DanmuInfo implements Serializable {


    /**
     * 弹幕id
     */
    private Integer danmuId;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 弹幕文本
     */
    private String text;

    /**
     * 弹幕模式
     */
    private Integer mode;

    /**
     * 弹幕颜色
     */
    private String color;

    /**
     * 弹幕时间点
     */
    private Long time;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date postTime;


    public void setDanmuId(Integer danmuId){
        this.danmuId = danmuId;
    }

    public Integer getDanmuId(){
        return this.danmuId;
    }

    public void setVideoId(String videoId){
        this.videoId = videoId;
    }

    public String getVideoId(){
        return this.videoId;
    }

    public void setFileId(String fileId){
        this.fileId = fileId;
    }

    public String getFileId(){
        return this.fileId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public void setMode(Integer mode){
        this.mode = mode;
    }

    public Integer getMode(){
        return this.mode;
    }

    public void setColor(String color){
        this.color = color;
    }

    public String getColor(){
        return this.color;
    }

    public void setTime(Long time){
        this.time = time;
    }

    public Long getTime(){
        return this.time;
    }

    public void setPostTime(Date postTime){
        this.postTime = postTime;
    }

    public Date getPostTime(){
        return this.postTime;
    }

    @Override
    public String toString (){
        return "弹幕id:"+(danmuId == null ? "空" : danmuId)+"，视频id:"+(videoId == null ? "空" : videoId)+"，文件id:"+(fileId == null ? "空" : fileId)+"，用户id:"+(userId == null ? "空" : userId)+"，弹幕文本:"+(text == null ? "空" : text)+"，弹幕模式:"+(mode == null ? "空" : mode)+"，弹幕颜色:"+(color == null ? "空" : color)+"，弹幕时间点:"+(time == null ? "空" : time)+"，发布时间:"+(postTime == null ? "空" : DateUtil.format(postTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
