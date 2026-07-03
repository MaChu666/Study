package com.machugit.entity.query;

/**
 * 系统主题配置表参数
 */
public class SysThemeQuery extends BaseParam {

    /**
     * 主题id
     */
    private Integer themeId;

    /**
     * 主题名称
     */
    private String themeName;

    private String themeNameFuzzy;

    /**
     * 状态（0：禁用 1：启用）
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    public void setThemeId(Integer themeId){
        this.themeId = themeId;
    }

    public Integer getThemeId(){
        return this.themeId;
    }

    public void setThemeName(String themeName){
        this.themeName = themeName;
    }

    public String getThemeName(){
        return this.themeName;
    }

    public void setThemeNameFuzzy(String themeNameFuzzy){
        this.themeNameFuzzy = themeNameFuzzy;
    }

    public String getThemeNameFuzzy(){
        return this.themeNameFuzzy;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus(){
        return this.status;
    }

    public void setSort(Integer sort){
        this.sort = sort;
    }

    public Integer getSort(){
        return this.sort;
    }

}
