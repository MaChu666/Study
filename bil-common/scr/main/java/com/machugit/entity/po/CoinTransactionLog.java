package com.machugit.entity.po;

import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 硬币交易日志表
 */
public class CoinTransactionLog implements Serializable {


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

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "日志id:"+(logId == null ? "空" : logId)+"，用户id:"+(userId == null ? "空" : userId)+"，视频id:"+(videoId == null ? "空" : videoId)+"，硬币数量:"+(coinAmount == null ? "空" : coinAmount)+"，来源类型:"+(sourceType == null ? "空" : sourceType)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
