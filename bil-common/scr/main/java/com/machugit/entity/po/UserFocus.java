package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;


/**
 * 用户关注表
 */
public class UserFocus implements Serializable {


    /**
     * 关注id
     */
    private Integer focusId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 被关注用户id
     */
    private String focusUserId;

    /**
     * 关注时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    public void setFocusId(Integer focusId){
        this.focusId = focusId;
    }

    public Integer getFocusId(){
        return this.focusId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setFocusUserId(String focusUserId){
        this.focusUserId = focusUserId;
    }

    public String getFocusUserId(){
        return this.focusUserId;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "关注id:"+(focusId == null ? "空" : focusId)+"，用户id:"+(userId == null ? "空" : userId)+"，被关注用户id:"+(focusUserId == null ? "空" : focusUserId)+"，关注时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
