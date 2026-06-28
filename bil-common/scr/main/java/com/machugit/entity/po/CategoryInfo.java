package com.machugit.entity.po;

import java.io.Serializable;


/**
 * 分类信息表
 */
public class CategoryInfo implements Serializable {


    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类id
     */
    private Integer pCategoryId;

    /**
     * 图标
     */
    private String icon;

    /**
     * 背景
     */
    private String background;

    /**
     * 排序
     */
    private Integer sort;


    public void setCategoryId(Integer categoryId){
        this.categoryId = categoryId;
    }

    public Integer getCategoryId(){
        return this.categoryId;
    }

    public void setCategoryCode(String categoryCode){
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode(){
        return this.categoryCode;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public String getCategoryName(){
        return this.categoryName;
    }

    public void setPCategoryId(Integer pCategoryId){
        this.pCategoryId = pCategoryId;
    }

    public Integer getPCategoryId(){
        return this.pCategoryId;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public String getIcon(){
        return this.icon;
    }

    public void setBackground(String background){
        this.background = background;
    }

    public String getBackground(){
        return this.background;
    }

    public void setSort(Integer sort){
        this.sort = sort;
    }

    public Integer getSort(){
        return this.sort;
    }

    @Override
    public String toString (){
        return "分类id:"+(categoryId == null ? "空" : categoryId)+"，分类编码:"+(categoryCode == null ? "空" : categoryCode)+"，分类名称:"+(categoryName == null ? "空" : categoryName)+"，父分类id:"+(pCategoryId == null ? "空" : pCategoryId)+"，图标:"+(icon == null ? "空" : icon)+"，背景:"+(background == null ? "空" : background)+"，排序:"+(sort == null ? "空" : sort);
    }
}
