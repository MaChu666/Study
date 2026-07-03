package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;


/**
 * 用户签到表
 */
public class UserSignIn implements Serializable {


    /**
     * 签到id
     */
    private Integer signId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 签到日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signDate;

    /**
     * 连续天数
     */
    private Integer continuousDays;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


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

    public void setSignDate(Date signDate){
        this.signDate = signDate;
    }

    public Date getSignDate(){
        return this.signDate;
    }

    public void setContinuousDays(Integer continuousDays){
        this.continuousDays = continuousDays;
    }

    public Integer getContinuousDays(){
        return this.continuousDays;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    @Override
    public String toString (){
        return "签到id:"+(signId == null ? "空" : signId)+"，用户id:"+(userId == null ? "空" : userId)+"，签到日期:"+(signDate == null ? "空" : DateUtil.format(signDate, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，连续天数:"+(continuousDays == null ? "空" : continuousDays)+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
