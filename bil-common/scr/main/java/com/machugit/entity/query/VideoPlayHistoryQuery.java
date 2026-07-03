package com.machugit.entity.query;

import java.util.Date;


/**
 * 视频播放历史表参数
 */
public class VideoPlayHistoryQuery extends BaseParam {


    /**
     * 历史记录id
     */
    private Integer historyId;

    /**
     * 视频id
     */
    private String videoId;

    private String videoIdFuzzy;

    /**
     * 用户id
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 文件id
     */
    private String fileId;

    private String fileIdFuzzy;

    /**
     * 上次观看到第几秒
     */
    private Integer progressSeconds;

    /**
     * 是否看完（0：未 1：已看完）
     */
    private Integer isFinished;

    /**
     * 更新时间
     */
    private String updateTime;

    private String updateTimeStart;

    private String updateTimeEnd;


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

    public void setVideoIdFuzzy(String videoIdFuzzy){
        this.videoIdFuzzy = videoIdFuzzy;
    }

    public String getVideoIdFuzzy(){
        return this.videoIdFuzzy;
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

    public void setProgressSeconds(Integer progressSeconds){
        this.progressSeconds = progressSeconds;
    }

    public Integer getProgressSeconds(){
        return this.progressSeconds;
    }

    public void setIsFinished(Integer isFinished){
        this.isFinished = isFinished;
    }

    public Integer getIsFinished(){
        return this.isFinished;
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
