package com.machugit.web.contorller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.machugit.entity.po.CategoryInfo;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.CategoryInfoServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/category")
@Validated
public class CategoryController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Resource
    private CategoryInfoServiceImpl categoryInfoService;

    /**
     * 加载全部分类
     */
    @RequestMapping("/loadAllCategory")
    public ResponseVO loadAllCategory() {
        List<CategoryInfo> list = categoryInfoService.loadAllCategory();
        return getSuccessResponseVO(list);
    }
}
