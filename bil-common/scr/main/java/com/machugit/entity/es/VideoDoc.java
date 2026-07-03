package com.machugit.entity.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "bil_video")
@Setting(settingPath = "es/bil-video-setting.json")
public class VideoDoc {

    @Id
    private String videoId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String videoName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String tags;

    @Field(type = FieldType.Keyword)
    private String userId;

    @Field(type = FieldType.Keyword)
    private Integer categoryId;

    @Field(type = FieldType.Keyword)
    private Integer pCategoryId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String introduction;

    @Field(type = FieldType.Keyword)
    private String videoCover;

    @Field(type = FieldType.Long)
    private Long playCount;

    @Field(type = FieldType.Long)
    private Long likeCount;

    @Field(type = FieldType.Long)
    private Long danmuCount;

    @Field(type = FieldType.Long)
    private Long commentCount;

    @Field(type = FieldType.Long)
    private Long collectCount;

    @Field(type = FieldType.Long)
    private Long coinCount;

    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(type = FieldType.Integer)
    private Integer isDeleted;

    @Field(type = FieldType.Date)
    private String createTime;

    @Field(type = FieldType.Keyword)
    private String userName;

    @Field(type = FieldType.Keyword)
    private String userAvatar;

    public VideoDoc() {}

    public static VideoDoc from(com.machugit.entity.po.VideoInfo v) {
        VideoDoc doc = new VideoDoc();
        doc.videoId = v.getVideoId();
        doc.videoName = v.getVideoName();
        doc.tags = v.getTags();
        doc.userId = v.getUserId();
        doc.categoryId = v.getCategoryId();
        doc.pCategoryId = v.getPCategoryId();
        doc.introduction = v.getIntroduction();
        doc.videoCover = v.getVideoCover();
        doc.playCount = v.getPlayCount();
        doc.likeCount = v.getLikeCount();
        doc.danmuCount = v.getDanmuCount();
        doc.commentCount = v.getCommentCount();
        doc.collectCount = v.getCollectCount();
        doc.coinCount = v.getCoinCount();
        doc.status = v.getStatus();
        doc.isDeleted = v.getIsDeleted();
        doc.createTime = v.getCreateTime() != null ? v.getCreateTime().toString() : null;
        return doc;
    }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }
    public String getVideoName() { return videoName; }
    public void setVideoName(String videoName) { this.videoName = videoName; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getPCategoryId() { return pCategoryId; }
    public void setPCategoryId(Integer pCategoryId) { this.pCategoryId = pCategoryId; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getVideoCover() { return videoCover; }
    public void setVideoCover(String videoCover) { this.videoCover = videoCover; }
    public Long getPlayCount() { return playCount; }
    public void setPlayCount(Long playCount) { this.playCount = playCount; }
    public Long getLikeCount() { return likeCount; }
    public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }
    public Long getDanmuCount() { return danmuCount; }
    public void setDanmuCount(Long danmuCount) { this.danmuCount = danmuCount; }
    public Long getCommentCount() { return commentCount; }
    public void setCommentCount(Long commentCount) { this.commentCount = commentCount; }
    public Long getCollectCount() { return collectCount; }
    public void setCollectCount(Long collectCount) { this.collectCount = collectCount; }
    public Long getCoinCount() { return coinCount; }
    public void setCoinCount(Long coinCount) { this.coinCount = coinCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
}