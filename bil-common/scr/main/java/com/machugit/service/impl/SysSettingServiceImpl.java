package com.machugit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.SysSetting;
import com.machugit.entity.query.SysSettingQuery;
import com.machugit.mappers.SysSettingMapper;
import com.machugit.service.SysSettingService;

/**
 * 系统设置 业务接口实现
 */
@Service("sysSettingService")
public class SysSettingServiceImpl implements SysSettingService {

    @Resource
    private SysSettingMapper<SysSetting, SysSettingQuery> sysSettingMapper;

    /**
     * 获取系统设置
     */
    @Override
    public SysSetting getSetting() {
        SysSettingQuery query = new SysSettingQuery();
        List<SysSetting> list = this.sysSettingMapper.selectList(query);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 保存系统设置
     */
    @Override
    public void saveSetting(SysSetting bean) {
        SysSetting existSetting = getSetting();
        if (existSetting == null) {
            this.sysSettingMapper.insert(bean);
        } else {
            this.sysSettingMapper.updateById(bean, existSetting.getId());
        }
    }

}
