package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 消息信息表
 */
public class MessageInfo implements Serializable {


    /**
     * 消息id
     */
    private Integer messageId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 阅读状态
     */
    private Integer readStatus;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    public void setMessageId(Integer messageId){
        this.messageId = messageId;
    }

    public Integer getMessageId(){
        return this.messageId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setMessageType(Integer messageType){
        this.messageType = messageType;
    }

    public Integer getMessageType(){
        return this.messageType;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){
        return this.content;
    }

    public void setReadStatus(Integer readStatus){
        this.readStatus = readStatus;
    }

    public Integer getReadStatus(){
        return this.readStatus;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "消息id:"+(messageId == null ? "空" : messageId)+"，用户id:"+(userId == null ? "空" : userId)+"，消息类型:"+(messageType == null ? "空" : messageType)+"，消息内容:"+(content == null ? "空" : content)+"，阅读状态:"+(readStatus == null ? "空" : readStatus)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
