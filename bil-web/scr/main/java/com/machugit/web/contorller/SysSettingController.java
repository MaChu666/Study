package com.machugit.web.contorller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.machugit.entity.po.SysSetting;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.SysSettingServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sysSetting")
@Validated
public class SysSettingController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(SysSettingController.class);

    @Resource
    private SysSettingServiceImpl sysSettingService;

    /**
     * 获取系统设置
     */
    @RequestMapping("/getSetting")
    public ResponseVO getSetting() {
        SysSetting sysSetting = sysSettingService.getSetting();
        return getSuccessResponseVO(sysSetting);
    }
}
