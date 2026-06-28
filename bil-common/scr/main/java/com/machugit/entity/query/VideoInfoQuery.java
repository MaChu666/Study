package com.machugit.entity.query;



/**
 * 视频信息表参数
 */
public class VideoInfoQuery extends BaseParam {


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

    private String videoNameFuzzy;

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

    private String tagsFuzzy;

    /**
     * 视频简介
     */
    private String introduction;

    private String introductionFuzzy;

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
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    /**
     * 更新时间
     */
    private String updateTime;

    private String updateTimeStart;

    private String updateTimeEnd;


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

    public void setVideoNameFuzzy(String videoNameFuzzy){
        this.videoNameFuzzy = videoNameFuzzy;
    }

    public String getVideoNameFuzzy(){
        return this.videoNameFuzzy;
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

    public void setTagsFuzzy(String tagsFuzzy){
        this.tagsFuzzy = tagsFuzzy;
    }

    public String getTagsFuzzy(){
        return this.tagsFuzzy;
    }

    public void setIntroduction(String introduction){
        this.introduction = introduction;
    }

    public String getIntroduction(){
        return this.introduction;
    }

    public void setIntroductionFuzzy(String introductionFuzzy){
        this.introductionFuzzy = introductionFuzzy;
    }

    public String getIntroductionFuzzy(){
        return this.introductionFuzzy;
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
