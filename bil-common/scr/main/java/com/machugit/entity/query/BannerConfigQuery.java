package com.machugit.entity.query;


/**
 * 轮播图配置表参数
 */
public class BannerConfigQuery extends BaseParam {


    /**
     * 轮播图id
     */
    private Integer bannerId;

    /**
     * 标题
     */
    private String title;

    private String titleFuzzy;

    /**
     * 图片地址
     */
    private String imageUrl;

    private String imageUrlFuzzy;

    /**
     * 跳转类型
     */
    private Integer linkType;

    /**
     * 跳转值
     */
    private String linkValue;

    private String linkValueFuzzy;

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
    private String startTime;

    private String startTimeStart;

    private String startTimeEnd;

    /**
     * 结束时间
     */
    private String endTime;

    private String endTimeStart;

    private String endTimeEnd;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    /**
     * 更新时间
     */
    private String updateTime;

    private String updateTimeStart;

    private String updateTimeEnd;


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

    public void setTitleFuzzy(String titleFuzzy){
        this.titleFuzzy = titleFuzzy;
    }

    public String getTitleFuzzy(){
        return this.titleFuzzy;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    public void setImageUrlFuzzy(String imageUrlFuzzy){
        this.imageUrlFuzzy = imageUrlFuzzy;
    }

    public String getImageUrlFuzzy(){
        return this.imageUrlFuzzy;
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

    public void setLinkValueFuzzy(String linkValueFuzzy){
        this.linkValueFuzzy = linkValueFuzzy;
    }

    public String getLinkValueFuzzy(){
        return this.linkValueFuzzy;
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

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public String getStartTime(){
        return this.startTime;
    }

    public void setStartTimeStart(String startTimeStart){
        this.startTimeStart = startTimeStart;
    }

    public String getStartTimeStart(){
        return this.startTimeStart;
    }
    public void setStartTimeEnd(String startTimeEnd){
        this.startTimeEnd = startTimeEnd;
    }

    public String getStartTimeEnd(){
        return this.startTimeEnd;
    }

    public void setEndTime(String endTime){
        this.endTime = endTime;
    }

    public String getEndTime(){
        return this.endTime;
    }

    public void setEndTimeStart(String endTimeStart){
        this.endTimeStart = endTimeStart;
    }

    public String getEndTimeStart(){
        return this.endTimeStart;
    }
    public void setEndTimeEnd(String endTimeEnd){
        this.endTimeEnd = endTimeEnd;
    }

    public String getEndTimeEnd(){
        return this.endTimeEnd;
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

    public void setUpdateTime(String updateTime){
        this.updateTime = updateTime;
    }

    public String getUpdateTime(){
        return this.updateTime;
    }

    public void setUpdateTimeStart(String updateTimeStart){
        this.updateTimeStart = updateTimeStart;
    }

    public String getUpdateTimeStart(){
        return this.updateTimeStart;
    }
    public void setUpdateTimeEnd(String updateTimeEnd){
        this.updateTimeEnd = updateTimeEnd;
    }

    public String getUpdateTimeEnd(){
        return this.updateTimeEnd;
    }

}
