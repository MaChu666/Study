package com.machugit.admin.controller;

import com.machugit.entity.po.SysTheme;
import com.machugit.entity.query.SysThemeQuery;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.SysThemeServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/theme")
@Validated
public class AdminThemeController extends ABaseAdminController {

    @Resource
    private SysThemeServiceImpl sysThemeService;

    @RequestMapping("/loadAllThemes")
    public ResponseVO loadAllThemes() {
        SysThemeQuery query = new SysThemeQuery();
        List<SysTheme> list = sysThemeService.loadAllThemes(query);
        return getSuccessResponseVO(list);
    }

    @RequestMapping("/addTheme")
    public ResponseVO addTheme(String themeName,
                               String gradient,
                               String primaryColor,
                               Integer sort,
                               Integer status) {
        SysTheme bean = new SysTheme();
        bean.setThemeName(themeName);
        bean.setGradient(gradient);
        bean.setPrimaryColor(primaryColor);
        bean.setSort(sort);
        bean.setStatus(status);
        sysThemeService.addTheme(bean);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/updateTheme")
    public ResponseVO updateTheme(Integer themeId,
                                  String themeName,
                                  String gradient,
                                  String primaryColor,
                                  Integer sort,
                                  Integer status) {
        SysTheme bean = new SysTheme();
        bean.setThemeId(themeId);
        bean.setThemeName(themeName);
        bean.setGradient(gradient);
        bean.setPrimaryColor(primaryColor);
        bean.setSort(sort);
        bean.setStatus(status);
        sysThemeService.updateTheme(bean);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/deleteTheme")
    public ResponseVO deleteTheme(Integer themeId) {
        sysThemeService.deleteTheme(themeId);
        return getSuccessResponseVO(null);
    }

}
