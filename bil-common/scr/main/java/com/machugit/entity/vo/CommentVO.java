package com.machugit.entity.vo;

import com.machugit.entity.po.CommentInfo;

// @Data
// @EqualsAndHashCode(callSuper = true)
public class CommentVO extends CommentInfo {
    private String videoName;
    private String userName;
    public String getVideoName() { return videoName; }
    public void setVideoName(String videoName) { this.videoName = videoName; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
