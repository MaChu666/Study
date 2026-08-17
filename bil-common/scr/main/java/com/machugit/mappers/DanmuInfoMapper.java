package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 弹幕信息 数据库操作接口
 */
public interface DanmuInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据DanmuId更新
	 */
	 Integer updateByDanmuId(@Param("bean") T t,@Param("danmuId") Integer danmuId);


	/**
	 * 举报次数加一
	 */
	 Integer incrementReportCountByDanmuId(@Param("danmuId") Integer danmuId);


	/**
	 * 根据DanmuId删除
	 */
	 Integer deleteByDanmuId(@Param("danmuId") Integer danmuId);


	/**
	 * 根据DanmuId获取对象
	 */
	 T selectByDanmuId(@Param("danmuId") Integer danmuId);


	/**
	 * 根据VideoId获取对象
	 */
	 T selectByVideoId(@Param("videoId") String videoId);


	/**
	 * 根据FileId获取对象
	 */
	 T selectByFileId(@Param("fileId") String fileId);


}
