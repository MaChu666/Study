package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 系统主题配置表 数据库操作接口
 */
public interface SysThemeMapper<T,P> extends BaseMapper<T,P> {

    /**
     * 根据ThemeId更新
     */
    Integer updateById(@Param("bean") T t, @Param("id") Integer id);

    /**
     * 根据ThemeId删除
     */
    Integer deleteById(@Param("id") Integer id);

    /**
     * 根据ThemeId获取对象
     */
    T selectById(@Param("id") Integer id);

}
