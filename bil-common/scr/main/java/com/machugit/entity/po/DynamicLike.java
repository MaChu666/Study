package com.machugit.entity.po;

import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 动态点赞表
 */
public class DynamicLike implements Serializable {


    /**
     * 自增id
     */
    private Integer id;

    /**
     * 动态id
     */
    private Integer dynamicId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }

    public void setDynamicId(Integer dynamicId){
        this.dynamicId = dynamicId;
    }

    public Integer getDynamicId(){
        return this.dynamicId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "自增id:"+(id == null ? "空" : id)+"，动态id:"+(dynamicId == null ? "空" : dynamicId)+"，用户id:"+(userId == null ? "空" : userId)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
