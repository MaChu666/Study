package com.machugit.entity.query;

import java.util.Date;


/**
 * 评论信息表参数
 */
public class CommentInfoQuery extends BaseParam {


    /**
     * 评论id
     */
    private Integer commentId;

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
     * 评论内容
     */
    private String content;

    private String contentFuzzy;

    /**
     * 回复评论id
     */
    private Integer replyCommentId;
    private Integer root;

    /**
     * 图片路径
     */
    private String imgPath;

    private String imgPathFuzzy;

    /**
     * 置顶类型
     */
    private Integer topType;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;


    public void setCommentId(Integer commentId){
        this.commentId = commentId;
    }

    public Integer getCommentId(){
        return this.commentId;
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

    public void setReplyCommentId(Integer replyCommentId){
        this.replyCommentId = replyCommentId;
    }

    public Integer getReplyCommentId(){
        return this.replyCommentId;
    }
    public void setRoot(Integer root){ this.root = root; }
    public Integer getRoot(){ return this.root; }

    public void setImgPath(String imgPath){
        this.imgPath = imgPath;
    }

    public String getImgPath(){
        return this.imgPath;
    }

    public void setImgPathFuzzy(String imgPathFuzzy){
        this.imgPathFuzzy = imgPathFuzzy;
    }

    public String getImgPathFuzzy(){
        return this.imgPathFuzzy;
    }

    public void setTopType(Integer topType){
        this.topType = topType;
    }

    public Integer getTopType(){
        return this.topType;
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
