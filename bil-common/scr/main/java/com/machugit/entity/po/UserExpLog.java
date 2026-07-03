package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;


/**
 * 用户经验日志表
 */
public class UserExpLog implements Serializable {


    /**
     * 日志id
     */
    private Integer logId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 经验值
     */
    private Integer expAmount;

    /**
     * 来源类型
     */
    private Integer sourceType;

    /**
     * 来源id
     */
    private String sourceId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    public void setLogId(Integer logId){
        this.logId = logId;
    }

    public Integer getLogId(){
        return this.logId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setExpAmount(Integer expAmount){
        this.expAmount = expAmount;
    }

    public Integer getExpAmount(){
        return this.expAmount;
    }

    public void setSourceType(Integer sourceType){
        this.sourceType = sourceType;
    }

    public Integer getSourceType(){
        return this.sourceType;
    }

    public void setSourceId(String sourceId){
        this.sourceId = sourceId;
    }

    public String getSourceId(){
        return this.sourceId;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "日志id:"+(logId == null ? "空" : logId)+"，用户id:"+(userId == null ? "空" : userId)+"，经验值:"+(expAmount == null ? "空" : expAmount)+"，来源类型:"+(sourceType == null ? "空" : sourceType)+"，来源id:"+(sourceId == null ? "空" : sourceId)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
