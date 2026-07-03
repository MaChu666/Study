package com.machugit.entity.query;


/**
 * 动态点赞表参数
 */
public class DynamicLikeQuery extends BaseParam {


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
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;


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
