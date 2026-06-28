package com.machugit.service;

import java.util.List;

import com.machugit.entity.po.CategoryInfo;

/**
 * 分类信息 业务接口
 */
public interface CategoryInfoService {

    /**
     * 加载全部分类
     */
    List<CategoryInfo> loadAllCategory();

    /**
     * 加载子分类
     */
    List<CategoryInfo> loadCategory(Integer pCategoryId);

    /**
     * 保存分类
     */
    void saveCategory(CategoryInfo bean);

    /**
     * 删除分类
     */
    void delCategory(Integer categoryId);

    /**
     * 修改排序
     */
    void changeSort(Integer pCategoryId, String categoryIds);

}
