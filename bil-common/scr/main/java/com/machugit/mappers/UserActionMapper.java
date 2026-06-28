package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 用户行为 数据库操作接口
 */
public interface UserActionMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据ActionId更新
	 */
	 Integer updateByActionId(@Param("bean") T t,@Param("actionId") Integer actionId);


	/**
	 * 根据ActionId删除
	 */
	 Integer deleteByActionId(@Param("actionId") Integer actionId);


	/**
	 * 根据ActionId获取对象
	 */
	 T selectByActionId(@Param("actionId") Integer actionId);


	/**
	 * 根据VideoId获取对象
	 */
	 T selectByVideoId(@Param("videoId") String videoId);


	/**
	 * 根据UserId获取对象
	 */
	 T selectByUserId(@Param("userId") String userId);


}
