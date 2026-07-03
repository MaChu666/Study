package com.machugit.entity.query;

import java.util.Date;


/**
 * 用户行为表参数
 */
public class UserActionQuery extends BaseParam {


    /**
     * 行为id
     */
    private Integer actionId;

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
     * 行为类型
     */
    private Integer actionType;

    /**
     * 投币数量（仅投币类型有效，1或2）
     */
    private Integer coinCount;

    /**
     * 行为计数
     */
    private Integer actionCount;

    /**
     * 评论id
     */
    private Integer commentId;

    /**
     * 是否取消（0：有效 1：已取消）
     */
    private Integer isCancel;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;


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

    public void setActionType(Integer actionType){
        this.actionType = actionType;
    }

    public Integer getActionType(){
        return this.actionType;
    }

    public void setCoinCount(Integer coinCount){
        this.coinCount = coinCount;
    }

    public Integer getCoinCount(){
        return this.coinCount;
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

    public void setIsCancel(Integer isCancel){
        this.isCancel = isCancel;
    }

    public Integer getIsCancel(){
        return this.isCancel;
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

}
