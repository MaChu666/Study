package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 用户签到表 数据库操作接口
 */
public interface UserSignInMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据SignId更新
	 */
	 Integer updateBySignId(@Param("bean") T t,@Param("signId") Integer signId);


	/**
	 * 根据SignId删除
	 */
	 Integer deleteBySignId(@Param("signId") Integer signId);


	/**
	 * 根据SignId获取对象
	 */
	 T selectBySignId(@Param("signId") Integer signId);


	/**
	 * 根据UserId更新
	 */
	 Integer updateByUserId(@Param("bean") T t,@Param("userId") String userId);


	/**
	 * 根据UserId删除
	 */
	 Integer deleteByUserId(@Param("userId") String userId);


	/**
	 * 根据UserId获取对象
	 */
	 T selectByUserId(@Param("userId") String userId);


}
