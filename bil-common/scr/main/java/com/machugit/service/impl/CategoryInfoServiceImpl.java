package com.machugit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.CategoryInfo;
import com.machugit.entity.query.CategoryInfoQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.CategoryInfoMapper;
import com.machugit.service.CategoryInfoService;
import com.machugit.utils.StringTools;

/**
 * 分类信息 业务接口实现
 */
@Service("categoryInfoService")
public class CategoryInfoServiceImpl implements CategoryInfoService {

    @Resource
    private CategoryInfoMapper<CategoryInfo, CategoryInfoQuery> categoryInfoMapper;

    /**
     * 加载全部分类
     */
    @Override
    public List<CategoryInfo> loadAllCategory() {
        CategoryInfoQuery query = new CategoryInfoQuery();
        query.setOrderBy("sort asc");
        return this.categoryInfoMapper.selectList(query);
    }

    /**
     * 加载子分类
     */
    @Override
    public List<CategoryInfo> loadCategory(Integer pCategoryId) {
        CategoryInfoQuery query = new CategoryInfoQuery();
        query.setPCategoryId(pCategoryId);
        query.setOrderBy("sort asc");
        return this.categoryInfoMapper.selectList(query);
    }

    /**
     * 保存分类
     */
    @Override
    public void saveCategory(CategoryInfo bean) {
        if (bean.getCategoryId() == null) {
            this.categoryInfoMapper.insert(bean);
        } else {
            this.categoryInfoMapper.updateByCategoryId(bean, bean.getCategoryId());
        }
    }

    /**
     * 删除分类
     */
    @Override
    public void delCategory(Integer categoryId) {
        this.categoryInfoMapper.deleteByCategoryId(categoryId);
    }

    /**
     * 修改排序
     */
    @Override
    public void changeSort(Integer pCategoryId, String categoryIds) {
        String[] categoryIdArray = categoryIds.split(",");
        for (int i = 0; i < categoryIdArray.length; i++) {
            String categoryIdStr = categoryIdArray[i];
            if (StringTools.isEmpty(categoryIdStr)) {
                continue;
            }
            Integer categoryId = Integer.parseInt(categoryIdStr.trim());
            CategoryInfo updateInfo = new CategoryInfo();
            updateInfo.setSort(i + 1);
            this.categoryInfoMapper.updateByCategoryId(updateInfo, categoryId);
        }
    }

}
