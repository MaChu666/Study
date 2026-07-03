package com.machugit.entity.po;

import java.io.Serializable;

/**
 * 系统主题配置表
 */
public class SysTheme implements Serializable {

    /**
     * 主题id
     */
    private Integer themeId;

    /**
     * 主题名称
     */
    private String themeName;

    /**
     * 主题渐变色（CSS gradient）
     */
    private String gradient;

    /**
     * 主色调（用于辅助显示）
     */
    private String primaryColor;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0：禁用 1：启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private java.util.Date createTime;

    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

    public Integer getThemeId() {
        return this.themeId;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeName() {
        return this.themeName;
    }

    public void setGradient(String gradient) {
        this.gradient = gradient;
    }

    public String getGradient() {
        return this.gradient;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getPrimaryColor() {
        return this.primaryColor;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSort() {
        return this.sort;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    @Override
    public String toString() {
        return "主题id:" + (themeId == null ? "空" : themeId) + 
               "，主题名称:" + (themeName == null ? "空" : themeName) + 
               "，渐变色:" + (gradient == null ? "空" : gradient) + 
               "，主色调:" + (primaryColor == null ? "空" : primaryColor) + 
               "，排序:" + (sort == null ? "空" : sort) + 
               "，状态:" + (status == null ? "空" : status);
    }
}
