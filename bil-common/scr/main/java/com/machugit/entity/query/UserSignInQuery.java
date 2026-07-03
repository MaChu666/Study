package com.machugit.entity.query;

import java.util.Date;


/**
 * 用户签到表参数
 */
public class UserSignInQuery extends BaseParam {


    /**
     * 签到id
     */
    private Integer signId;

    /**
     * 用户id
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 签到日期
     */
    private String signDate;

    private String signDateStart;

    private String signDateEnd;

    /**
     * 连续天数
     */
    private Integer continuousDays;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;


    public void setSignId(Integer signId){
        this.signId = signId;
    }

    public Integer getSignId(){
        return this.signId;
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

    public void setSignDate(String signDate){
        this.signDate = signDate;
    }

    public String getSignDate(){
        return this.signDate;
    }

    public void setSignDateStart(String signDateStart){
        this.signDateStart = signDateStart;
    }

    public String getSignDateStart(){
        return this.signDateStart;
    }
    public void setSignDateEnd(String signDateEnd){
        this.signDateEnd = signDateEnd;
    }

    public String getSignDateEnd(){
        return this.signDateEnd;
    }

    public void setContinuousDays(Integer continuousDays){
        this.continuousDays = continuousDays;
    }

    public Integer getContinuousDays(){
        return this.continuousDays;
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
