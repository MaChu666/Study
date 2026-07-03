package com.machugit.entity.query;

import java.util.Date;


/**
 * 用户经验日志表参数
 */
public class UserExpLogQuery extends BaseParam {


    /**
     * 日志id
     */
    private Integer logId;

    /**
     * 用户id
     */
    private String userId;

    private String userIdFuzzy;

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

    private String sourceIdFuzzy;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;


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

    public void setUserIdFuzzy(String userIdFuzzy){
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getUserIdFuzzy(){
        return this.userIdFuzzy;
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

    public void setSourceIdFuzzy(String sourceIdFuzzy){
        this.sourceIdFuzzy = sourceIdFuzzy;
    }

    public String getSourceIdFuzzy(){
        return this.sourceIdFuzzy;
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
