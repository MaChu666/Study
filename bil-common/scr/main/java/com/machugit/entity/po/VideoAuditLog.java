package com.machugit.entity.po;

import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 视频审核日志表
 */
public class VideoAuditLog implements Serializable {


    /**
     * 审核id
     */
    private Integer auditId;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 审核人id
     */
    private String auditorId;

    /**
     * 变更前状态
     */
    private Integer fromStatus;

    /**
     * 变更后状态
     */
    private Integer toStatus;

    /**
     * 审核原因
     */
    private String reason;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    public void setAuditId(Integer auditId){
        this.auditId = auditId;
    }

    public Integer getAuditId(){
        return this.auditId;
    }

    public void setVideoId(String videoId){
        this.videoId = videoId;
    }

    public String getVideoId(){
        return this.videoId;
    }

    public void setAuditorId(String auditorId){
        this.auditorId = auditorId;
    }

    public String getAuditorId(){
        return this.auditorId;
    }

    public void setFromStatus(Integer fromStatus){
        this.fromStatus = fromStatus;
    }

    public Integer getFromStatus(){
        return this.fromStatus;
    }

    public void setToStatus(Integer toStatus){
        this.toStatus = toStatus;
    }

    public Integer getToStatus(){
        return this.toStatus;
    }

    public void setReason(String reason){
        this.reason = reason;
    }

    public String getReason(){
        return this.reason;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "审核id:"+(auditId == null ? "空" : auditId)+"，视频id:"+(videoId == null ? "空" : videoId)+"，审核人id:"+(auditorId == null ? "空" : auditorId)+"，变更前状态:"+(fromStatus == null ? "空" : fromStatus)+"，变更后状态:"+(toStatus == null ? "空" : toStatus)+"，审核原因:"+(reason == null ? "空" : reason)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
