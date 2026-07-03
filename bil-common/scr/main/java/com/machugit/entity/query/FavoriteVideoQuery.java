package com.machugit.entity.query;

import java.util.Date;


/**
 * 收藏视频表参数
 */
public class FavoriteVideoQuery extends BaseParam {


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

    private String userIdFuzzy;

    /**
     * 视频id
     */
    private String videoId;

    private String videoIdFuzzy;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;


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

    public void setUserIdFuzzy(String userIdFuzzy){
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getUserIdFuzzy(){
        return this.userIdFuzzy;
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
