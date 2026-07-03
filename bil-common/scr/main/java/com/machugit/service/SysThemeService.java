package com.machugit.service;

import com.machugit.entity.po.SysTheme;
import com.machugit.entity.query.SysThemeQuery;

import java.util.List;

/**
 * 系统主题 业务接口
 */
public interface SysThemeService {

    /**
     * 获取所有启用的主题列表
     */
    List<SysTheme> loadActiveThemes();

    /**
     * 获取所有主题列表（管理后台）
     */
    List<SysTheme> loadAllThemes(SysThemeQuery query);

    /**
     * 新增主题
     */
    void addTheme(SysTheme bean);

    /**
     * 更新主题
     */
    void updateTheme(SysTheme bean);

    /**
     * 删除主题
     */
    void deleteTheme(Integer themeId);

}
