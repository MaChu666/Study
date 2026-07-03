package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 轮播图配置表 数据库操作接口
 */
public interface BannerConfigMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据BannerId更新
	 */
	 Integer updateByBannerId(@Param("bean") T t,@Param("bannerId") Integer bannerId);


	/**
	 * 根据BannerId删除
	 */
	 Integer deleteByBannerId(@Param("bannerId") Integer bannerId);


	/**
	 * 根据BannerId获取对象
	 */
	 T selectByBannerId(@Param("bannerId") Integer bannerId);


}
