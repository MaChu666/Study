package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;


/**
 * 收藏视频表
 */
public class FavoriteVideo implements Serializable {


    /**
     * 记录id
     */
    private Integer id;

    /**
     * 收藏夹id
     */
    private Integer folderId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setFolderId(Integer folderId){
        this.folderId = folderId;
    }

    public Integer getFolderId(){
        return this.folderId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setVideoId(String videoId){
        this.videoId = videoId;
    }

    public String getVideoId(){
        return this.videoId;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "记录id:"+(id == null ? "空" : id)+"，收藏夹id:"+(folderId == null ? "空" : folderId)+"，用户id:"+(userId == null ? "空" : userId)+"，视频id:"+(videoId == null ? "空" : videoId)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
