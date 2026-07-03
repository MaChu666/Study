package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 动态点赞表 数据库操作接口
 */
public interface DynamicLikeMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据Id更新
	 */
	 Integer updateById(@Param("bean") T t,@Param("id") Integer id);


	/**
	 * 根据Id删除
	 */
	 Integer deleteById(@Param("id") Integer id);


	/**
	 * 根据Id获取对象
	 */
	 T selectById(@Param("id") Integer id);


	/**
	 * 根据DynamicId获取对象
	 */
	 T selectByDynamicId(@Param("dynamicId") Integer dynamicId);


	/**
	 * 根据UserId获取对象
	 */
	 T selectByUserId(@Param("userId") String userId);


}
