package com.machugit.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.machugit.entity.po.SysTheme;
import com.machugit.entity.query.SysThemeQuery;
import com.machugit.exception.BusinessException;
import com.machugit.mappers.SysThemeMapper;
import com.machugit.service.SysThemeService;

/**
 * 系统主题 业务接口实现
 */
@Service("sysThemeService")
public class SysThemeServiceImpl implements SysThemeService {

    @Resource
    private SysThemeMapper<SysTheme, SysThemeQuery> sysThemeMapper;

    @Override
    public List<SysTheme> loadActiveThemes() {
        SysThemeQuery query = new SysThemeQuery();
        query.setStatus(1);
        query.setOrderBy("sort asc");
        return this.sysThemeMapper.selectList(query);
    }

    @Override
    public List<SysTheme> loadAllThemes(SysThemeQuery query) {
        if (query.getOrderBy() == null) {
            query.setOrderBy("sort asc");
        }
        return this.sysThemeMapper.selectList(query);
    }

    @Override
    public void addTheme(SysTheme bean) {
        this.sysThemeMapper.insert(bean);
    }

    @Override
    public void updateTheme(SysTheme bean) {
        if (bean.getThemeId() == null) {
            throw new BusinessException("主题ID不能为空");
        }
        this.sysThemeMapper.updateById(bean, bean.getThemeId());
    }

    @Override
    public void deleteTheme(Integer themeId) {
        this.sysThemeMapper.deleteById(themeId);
    }

}
