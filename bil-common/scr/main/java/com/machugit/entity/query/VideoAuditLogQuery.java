package com.machugit.entity.query;


/**
 * 视频审核日志表参数
 */
public class VideoAuditLogQuery extends BaseParam {


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

    private String reasonFuzzy;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;


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

    public void setReasonFuzzy(String reasonFuzzy){
        this.reasonFuzzy = reasonFuzzy;
    }

    public String getReasonFuzzy(){
        return this.reasonFuzzy;
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
