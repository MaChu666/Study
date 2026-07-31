package com.machugit.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.machugit.entity.po.SysSetting;
import com.machugit.entity.po.SysTheme;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.SysSettingServiceImpl;
import com.machugit.service.impl.SysThemeServiceImpl;
import com.machugit.controller.ABaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/sysSetting")
@Validated
public class SysSettingController extends com.machugit.controller.ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(SysSettingController.class);

    @Resource
    private SysSettingServiceImpl sysSettingService;

    @Resource
    private SysThemeServiceImpl sysThemeService;

    /**
     * 获取系统设置
     */
    @RequestMapping("/getSetting")
    public ResponseVO getSetting() {
        SysSetting sysSetting = sysSettingService.getSetting();
        return getSuccessResponseVO(sysSetting);
    }

    /**
     * 获取启用的主题列表
     */
    @RequestMapping("/loadThemes")
    public ResponseVO loadThemes() {
        List<SysTheme> list = sysThemeService.loadActiveThemes();
        return getSuccessResponseVO(list);
    }
}

