package com.machugit.entity.query;


/**
 * 硬币交易日志表参数
 */
public class CoinTransactionLogQuery extends BaseParam {


    /**
     * 日志id
     */
    private Integer logId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 硬币数量
     */
    private Integer coinAmount;

    /**
     * 来源类型
     */
    private Integer sourceType;

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

    public void setVideoId(String videoId){
        this.videoId = videoId;
    }

    public String getVideoId(){
        return this.videoId;
    }

    public void setCoinAmount(Integer coinAmount){
        this.coinAmount = coinAmount;
    }

    public Integer getCoinAmount(){
        return this.coinAmount;
    }

    public void setSourceType(Integer sourceType){
        this.sourceType = sourceType;
    }

    public Integer getSourceType(){
        return this.sourceType;
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
