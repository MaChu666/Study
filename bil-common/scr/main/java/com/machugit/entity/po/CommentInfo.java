package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 评论信息表
 */
public class CommentInfo implements Serializable {


    /**
     * 评论id
     */
    private Integer commentId;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 回复评论id
     */
    private Integer replyCommentId;

    /**
     * 图片路径
     */
    private String imgPath;

    /**
     * 置顶类型
     */
    private Integer topType;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


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

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return this.content;
    }

    public void setReplyCommentId(Integer replyCommentId){
        this.replyCommentId = replyCommentId;
    }

    public Integer getReplyCommentId(){
        return this.replyCommentId;
    }

    public void setImgPath(String imgPath){
        this.imgPath = imgPath;
    }

    public String getImgPath(){
        return this.imgPath;
    }

    public void setTopType(Integer topType){
        this.topType = topType;
    }

    public Integer getTopType(){
        return this.topType;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "评论id:"+(commentId == null ? "空" : commentId)+"，视频id:"+(videoId == null ? "空" : videoId)+"，用户id:"+(userId == null ? "空" : userId)+"，评论内容:"+(content == null ? "空" : content)+"，回复评论id:"+(replyCommentId == null ? "空" : replyCommentId)+"，图片路径:"+(imgPath == null ? "空" : imgPath)+"，置顶类型:"+(topType == null ? "空" : topType)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
