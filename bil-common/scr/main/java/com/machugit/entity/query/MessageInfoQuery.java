package com.machugit.entity.query;

import java.util.Date;


/**
 * 消息信息表参数
 */
public class MessageInfoQuery extends BaseParam {


    /**
     * 消息id
     */
    private Integer messageId;

    /**
     * 用户id
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String content;

    private String contentFuzzy;

    /**
     * 阅读状态
     */
    private Integer readStatus;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;


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

    public void setUserIdFuzzy(String userIdFuzzy){
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getUserIdFuzzy(){
        return this.userIdFuzzy;
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

    public void setContentFuzzy(String contentFuzzy){
        this.contentFuzzy = contentFuzzy;
    }

    public String getContentFuzzy(){
        return this.contentFuzzy;
    }

    public void setReadStatus(Integer readStatus){
        this.readStatus = readStatus;
    }

    public Integer getReadStatus(){
        return this.readStatus;
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
