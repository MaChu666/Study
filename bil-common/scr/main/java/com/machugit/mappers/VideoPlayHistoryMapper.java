package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 视频播放记录 数据库操作接口
 */
public interface VideoPlayHistoryMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据HistoryId更新
	 */
	 Integer updateByHistoryId(@Param("bean") T t,@Param("historyId") Integer historyId);


	/**
	 * 根据HistoryId删除
	 */
	 Integer deleteByHistoryId(@Param("historyId") Integer historyId);


	/**
	 * 根据HistoryId获取对象
	 */
	 T selectByHistoryId(@Param("historyId") Integer historyId);


	/**
	 * 根据UserId获取对象
	 */
	 T selectByUserId(@Param("userId") String userId);


}
