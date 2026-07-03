package com.machugit.entity.query;

import java.util.Date;


/**
 * 用户动态表参数
 */
public class UserDynamicQuery extends BaseParam {


    /**
     * 动态id
     */
    private Integer dynamicId;

    /**
     * 用户id
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 动态类型
     */
    private Integer dynamicType;

    /**
     * 内容
     */
    private String content;

    private String contentFuzzy;

    /**
     * 图片
     */
    private String images;

    private String imagesFuzzy;

    /**
     * 视频id
     */
    private String videoId;

    private String videoIdFuzzy;

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
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    /**
     * 更新时间
     */
    private String updateTime;

    private String updateTimeStart;

    private String updateTimeEnd;


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

    public void setUserIdFuzzy(String userIdFuzzy){
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getUserIdFuzzy(){
        return this.userIdFuzzy;
    }

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

    public void setContentFuzzy(String contentFuzzy){
        this.contentFuzzy = contentFuzzy;
    }

    public String getContentFuzzy(){
        return this.contentFuzzy;
    }

    public void setImages(String images){
        this.images = images;
    }

    public String getImages(){
        return this.images;
    }

    public void setImagesFuzzy(String imagesFuzzy){
        this.imagesFuzzy = imagesFuzzy;
    }

    public String getImagesFuzzy(){
        return this.imagesFuzzy;
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
