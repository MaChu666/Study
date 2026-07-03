package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 用户动态表 数据库操作接口
 */
public interface UserDynamicMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据DynamicId更新
	 */
	 Integer updateByDynamicId(@Param("bean") T t,@Param("dynamicId") Integer dynamicId);


	/**
	 * 根据DynamicId删除
	 */
	 Integer deleteByDynamicId(@Param("dynamicId") Integer dynamicId);


	/**
	 * 根据DynamicId获取对象
	 */
	 T selectByDynamicId(@Param("dynamicId") Integer dynamicId);


	/**
	 * 根据UserId获取对象
	 */
	 T selectByUserId(@Param("userId") String userId);


}
