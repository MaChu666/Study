package com.machugit.service;

import com.machugit.entity.po.SysSetting;

/**
 * 系统设置 业务接口
 */
public interface SysSettingService {

    /**
     * 获取系统设置
     */
    SysSetting getSetting();

    /**
     * 保存系统设置
     */
    void saveSetting(SysSetting bean);

}
