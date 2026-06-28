package com.machugit.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;


/**
 * 视频系列表
 */
public class VideoSeries implements Serializable {


    /**
     * 系列id
     */
    private Integer seriesId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 系列名称
     */
    private String seriesName;

    /**
     * 系列描述
     */
    private String seriesDescription;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    public void setSeriesId(Integer seriesId){
        this.seriesId = seriesId;
    }

    public Integer getSeriesId(){
        return this.seriesId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setSeriesName(String seriesName){
        this.seriesName = seriesName;
    }

    public String getSeriesName(){
        return this.seriesName;
    }

    public void setSeriesDescription(String seriesDescription){
        this.seriesDescription = seriesDescription;
    }

    public String getSeriesDescription(){
        return this.seriesDescription;
    }

    public void setSort(Integer sort){
        this.sort = sort;
    }

    public Integer getSort(){
        return this.sort;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }

    public Date getUpdateTime(){
        return this.updateTime;
    }

    @Override
    public String toString (){
        return "系列id:"+(seriesId == null ? "空" : seriesId)+"，用户id:"+(userId == null ? "空" : userId)+"，系列名称:"+(seriesName == null ? "空" : seriesName)+"，系列描述:"+(seriesDescription == null ? "空" : seriesDescription)+"，排序:"+(sort == null ? "空" : sort)+"，更新时间:"+(updateTime == null ? "空" : DateUtil.format(updateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
