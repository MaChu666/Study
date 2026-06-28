package com.machugit.entity.query;


/**
 * 用户关注表参数
 */
public class UserFocusQuery extends BaseParam {


    /**
     * 关注id
     */
    private Integer focusId;

    /**
     * 用户id
     */
    private String userId;

    private String userIdFuzzy;

    /**
     * 被关注用户id
     */
    private String focusUserId;

    private String focusUserIdFuzzy;

    /**
     * 关注时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;


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

    public void setUserIdFuzzy(String userIdFuzzy){
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getUserIdFuzzy(){
        return this.userIdFuzzy;
    }

    public void setFocusUserId(String focusUserId){
        this.focusUserId = focusUserId;
    }

    public String getFocusUserId(){
        return this.focusUserId;
    }

    public void setFocusUserIdFuzzy(String focusUserIdFuzzy){
        this.focusUserIdFuzzy = focusUserIdFuzzy;
    }

    public String getFocusUserIdFuzzy(){
        return this.focusUserIdFuzzy;
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
