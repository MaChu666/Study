package com.machugit.entity.vo;

import com.machugit.entity.po.DanmuInfo;

// @Data
// @EqualsAndHashCode(callSuper = true)
public class DanmuVO extends DanmuInfo {
    private String videoName;   // 视频名称
    private String userName;    // 用户昵称
    public String getVideoName() { return videoName; }
    public void setVideoName(String videoName) { this.videoName = videoName; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
