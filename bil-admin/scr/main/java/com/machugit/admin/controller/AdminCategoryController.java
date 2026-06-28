package com.machugit.admin.controller;

import java.util.List;

import com.machugit.entity.po.CategoryInfo;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.CategoryInfoServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/category")
@Validated
public class AdminCategoryController extends ABaseAdminController {

    @Resource
    private CategoryInfoServiceImpl categoryInfoService;

    @RequestMapping("/loadCategory")
    public ResponseVO loadCategory(Integer pCategoryId) {
        List<CategoryInfo> list = categoryInfoService.loadCategory(pCategoryId);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/saveCategory")
    public ResponseVO saveCategory(Integer pCategoryId,
                                   Integer categoryId,
                                   String categoryCode,
                                   String categoryName,
                                   String icon,
                                   String background) {
        CategoryInfo bean = new CategoryInfo();
        bean.setCategoryId(categoryId);
        bean.setPCategoryId(pCategoryId);
        bean.setCategoryCode(categoryCode);
        bean.setCategoryName(categoryName);
        bean.setIcon(icon);
        bean.setBackground(background);
        categoryInfoService.saveCategory(bean);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delCategory")
    public ResponseVO delCategory(@NotEmpty String categoryId) {
        categoryInfoService.delCategory(Integer.parseInt(categoryId));
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/changeSort")
    public ResponseVO changeSort(@NotEmpty String pCategoryId,
                                 @NotEmpty String categoryIds) {
        categoryInfoService.changeSort(Integer.parseInt(pCategoryId), categoryIds);
        return getSuccessResponseVO(null);
    }
}
