package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 用户关注表 数据库操作接口
 */
public interface UserFocusMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据FocusId更新
	 */
	 Integer updateByFocusId(@Param("bean") T t,@Param("focusId") Integer focusId);


	/**
	 * 根据FocusId删除
	 */
	 Integer deleteByFocusId(@Param("focusId") Integer focusId);


	/**
	 * 根据FocusId获取对象
	 */
	 T selectByFocusId(@Param("focusId") Integer focusId);


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


	/**
	 * 根据FocusUserId更新
	 */
	 Integer updateByFocusUserId(@Param("bean") T t,@Param("focusUserId") String focusUserId);


	/**
	 * 根据FocusUserId删除
	 */
	 Integer deleteByFocusUserId(@Param("focusUserId") String focusUserId);


	/**
	 * 根据FocusUserId获取对象
	 */
	 T selectByFocusUserId(@Param("focusUserId") String focusUserId);


}
