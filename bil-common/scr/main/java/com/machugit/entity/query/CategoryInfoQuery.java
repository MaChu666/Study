package com.machugit.entity.query;



/**
 * 分类信息表参数
 */
public class CategoryInfoQuery extends BaseParam {


    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 分类编码
     */
    private String categoryCode;

    private String categoryCodeFuzzy;

    /**
     * 分类名称
     */
    private String categoryName;

    private String categoryNameFuzzy;

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

    public void setCategoryCodeFuzzy(String categoryCodeFuzzy){
        this.categoryCodeFuzzy = categoryCodeFuzzy;
    }

    public String getCategoryCodeFuzzy(){
        return this.categoryCodeFuzzy;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public String getCategoryName(){
        return this.categoryName;
    }

    public void setCategoryNameFuzzy(String categoryNameFuzzy){
        this.categoryNameFuzzy = categoryNameFuzzy;
    }

    public String getCategoryNameFuzzy(){
        return this.categoryNameFuzzy;
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

}
