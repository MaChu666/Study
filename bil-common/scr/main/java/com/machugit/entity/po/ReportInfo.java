package com.machugit.entity.po;

import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 举报信息表
 */
public class ReportInfo implements Serializable {


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

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


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

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }

    public Date getUpdateTime(){
        return this.updateTime;
    }

    @Override
    public String toString (){
        return "举报id:"+(reportId == null ? "空" : reportId)+"，举报人id:"+(reporterId == null ? "空" : reporterId)+"，举报目标类型:"+(targetType == null ? "空" : targetType)+"，举报目标id:"+(targetId == null ? "空" : targetId)+"，举报原因类型:"+(reasonType == null ? "空" : reasonType)+"，举报原因描述:"+(reasonDesc == null ? "空" : reasonDesc)+"，举证图片:"+(proofImages == null ? "空" : proofImages)+"，处理状态:"+(status == null ? "空" : status)+"，处理人id:"+(handlerId == null ? "空" : handlerId)+"，处理结果:"+(handleResult == null ? "空" : handleResult)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，更新时间:"+(updateTime == null ? "空" : DateUtil.format(updateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
