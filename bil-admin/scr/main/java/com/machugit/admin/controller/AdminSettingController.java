package com.machugit.admin.controller;

import com.machugit.entity.po.SysSetting;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.service.impl.SysSettingServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/setting")
@Validated
public class AdminSettingController extends ABaseAdminController {

    @Resource
    private SysSettingServiceImpl sysSettingService;

    @RequestMapping("/getSetting")
    public ResponseVO getSetting() {
        SysSetting setting = sysSettingService.getSetting();
        return getSuccessResponseVO(setting);
    }

    @RequestMapping("/saveSetting")
    public ResponseVO saveSetting(Long maxFileSize,
                                  Long maxChunkSize,
                                  Integer commentOpen,
                                  Integer danmuOpen,
                                  Integer videoAudit,
                                  Integer registerOpen,
                                  String sysName) {
        SysSetting bean = new SysSetting();
        bean.setMaxFileSize(maxFileSize);
        bean.setMaxChunkSize(maxChunkSize);
        bean.setCommentOpen(commentOpen);
        bean.setDanmuOpen(danmuOpen);
        bean.setVideoAudit(videoAudit);
        bean.setRegisterOpen(registerOpen);
        bean.setSysName(sysName);
        sysSettingService.saveSetting(bean);
        return getSuccessResponseVO(null);
    }
}
