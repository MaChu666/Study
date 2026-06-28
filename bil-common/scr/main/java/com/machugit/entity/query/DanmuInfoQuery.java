package com.machugit.entity.query;

import java.util.Date;


/**
 * 弹幕信息表参数
 */
public class DanmuInfoQuery extends BaseParam {


    /**
     * 弹幕id
     */
    private Integer danmuId;

    /**
     * 视频id
     */
    private String videoId;

    private String videoIdFuzzy;

    /**
     * 文件id
     */
    private String fileId;

    private String fileIdFuzzy;

    /**
     * 用户id
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 弹幕文本
     */
    private String text;

    private String textFuzzy;

    /**
     * 弹幕模式
     */
    private Integer mode;

    /**
     * 弹幕颜色
     */
    private String color;

    private String colorFuzzy;

    /**
     * 弹幕时间点
     */
    private Long time;

    /**
     * 发布时间
     */
    private String postTime;

    private String postTimeStart;

    private String postTimeEnd;


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

    public void setVideoIdFuzzy(String videoIdFuzzy){
        this.videoIdFuzzy = videoIdFuzzy;
    }

    public String getVideoIdFuzzy(){
        return this.videoIdFuzzy;
    }

    public void setFileId(String fileId){
        this.fileId = fileId;
    }

    public String getFileId(){
        return this.fileId;
    }

    public void setFileIdFuzzy(String fileIdFuzzy){
        this.fileIdFuzzy = fileIdFuzzy;
    }

    public String getFileIdFuzzy(){
        return this.fileIdFuzzy;
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

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public void setTextFuzzy(String textFuzzy){
        this.textFuzzy = textFuzzy;
    }

    public String getTextFuzzy(){
        return this.textFuzzy;
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

    public void setColorFuzzy(String colorFuzzy){
        this.colorFuzzy = colorFuzzy;
    }

    public String getColorFuzzy(){
        return this.colorFuzzy;
    }

    public void setTime(Long time){
        this.time = time;
    }

    public Long getTime(){
        return this.time;
    }

    public void setPostTime(String postTime){
        this.postTime = postTime;
    }

    public String getPostTime(){
        return this.postTime;
    }

    public void setPostTimeStart(String postTimeStart){
        this.postTimeStart = postTimeStart;
    }

    public String getPostTimeStart(){
        return this.postTimeStart;
    }
    public void setPostTimeEnd(String postTimeEnd){
        this.postTimeEnd = postTimeEnd;
    }

    public String getPostTimeEnd(){
        return this.postTimeEnd;
    }

}
