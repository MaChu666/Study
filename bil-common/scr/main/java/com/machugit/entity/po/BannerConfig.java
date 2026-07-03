package com.machugit.entity.po;

import java.util.Date;
import com.machugit.entity.enums.DateTimePatternEnum;
import com.machugit.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 轮播图配置表
 */
public class BannerConfig implements Serializable {


    /**
     * 轮播图id
     */
    private Integer bannerId;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 跳转类型
     */
    private Integer linkType;

    /**
     * 跳转值
     */
    private String linkValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    public void setBannerId(Integer bannerId){
        this.bannerId = bannerId;
    }

    public Integer getBannerId(){
        return this.bannerId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    public void setLinkType(Integer linkType){
        this.linkType = linkType;
    }

    public Integer getLinkType(){
        return this.linkType;
    }

    public void setLinkValue(String linkValue){
        this.linkValue = linkValue;
    }

    public String getLinkValue(){
        return this.linkValue;
    }

    public void setSort(Integer sort){
        this.sort = sort;
    }

    public Integer getSort(){
        return this.sort;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus(){
        return this.status;
    }

    public void setStartTime(Date startTime){
        this.startTime = startTime;
    }

    public Date getStartTime(){
        return this.startTime;
    }

    public void setEndTime(Date endTime){
        this.endTime = endTime;
    }

    public Date getEndTime(){
        return this.endTime;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }

    public void setUpdateTime(Date updateTime){
        this.updateTime = updateTime;
    }

    public Date getUpdateTime(){
        return this.updateTime;
    }

    @Override
    public String toString (){
        return "轮播图id:"+(bannerId == null ? "空" : bannerId)+"，标题:"+(title == null ? "空" : title)+"，图片地址:"+(imageUrl == null ? "空" : imageUrl)+"，跳转类型:"+(linkType == null ? "空" : linkType)+"，跳转值:"+(linkValue == null ? "空" : linkValue)+"，排序:"+(sort == null ? "空" : sort)+"，状态:"+(status == null ? "空" : status)+"，开始时间:"+(startTime == null ? "空" : DateUtil.format(startTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，结束时间:"+(endTime == null ? "空" : DateUtil.format(endTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，更新时间:"+(updateTime == null ? "空" : DateUtil.format(updateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
    }
}
