package com.machugit.entity.query;


/**
 * 举报信息表参数
 */
public class ReportInfoQuery extends BaseParam {


    /**
     * 举报id
     */
    private Integer reportId;

    /**
     * 举报人id
     */
    private String reporterId;

    /**
     * 举报目标类型
     */
    private Integer targetType;

    /**
     * 举报目标id
     */
    private String targetId;

    /**
     * 举报原因类型
     */
    private Integer reasonType;

    /**
     * 举报原因描述
     */
    private String reasonDesc;

    private String reasonDescFuzzy;

    /**
     * 举证图片
     */
    private String proofImages;

    /**
     * 处理状态
     */
    private Integer status;

    /**
     * 处理人id
     */
    private String handlerId;

    /**
     * 处理结果
     */
    private String handleResult;

    private String handleResultFuzzy;

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


    public void setReportId(Integer reportId){
        this.reportId = reportId;
    }

    public Integer getReportId(){
        return this.reportId;
    }

    public void setReporterId(String reporterId){
        this.reporterId = reporterId;
    }

    public String getReporterId(){
        return this.reporterId;
    }

    public void setTargetType(Integer targetType){
        this.targetType = targetType;
    }

    public Integer getTargetType(){
        return this.targetType;
    }

    public void setTargetId(String targetId){
        this.targetId = targetId;
    }

    public String getTargetId(){
        return this.targetId;
    }

    public void setReasonType(Integer reasonType){
        this.reasonType = reasonType;
    }

    public Integer getReasonType(){
        return this.reasonType;
    }

    public void setReasonDesc(String reasonDesc){
        this.reasonDesc = reasonDesc;
    }

    public String getReasonDesc(){
        return this.reasonDesc;
    }

    public void setReasonDescFuzzy(String reasonDescFuzzy){
        this.reasonDescFuzzy = reasonDescFuzzy;
    }

    public String getReasonDescFuzzy(){
        return this.reasonDescFuzzy;
    }

    public void setProofImages(String proofImages){
        this.proofImages = proofImages;
    }

    public String getProofImages(){
        return this.proofImages;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus(){
        return this.status;
    }

    public void setHandlerId(String handlerId){
        this.handlerId = handlerId;
    }

    public String getHandlerId(){
        return this.handlerId;
    }

    public void setHandleResult(String handleResult){
        this.handleResult = handleResult;
    }

    public String getHandleResult(){
        return this.handleResult;
    }

    public void setHandleResultFuzzy(String handleResultFuzzy){
        this.handleResultFuzzy = handleResultFuzzy;
    }

    public String getHandleResultFuzzy(){
        return this.handleResultFuzzy;
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
