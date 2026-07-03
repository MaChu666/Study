package com.machugit.mappers;

import com.machugit.entity.po.VideoInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 视频信息表 数据库操作接口
 */
public interface VideoInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据VideoId更新
	 */
	 Integer updateByVideoId(@Param("bean") T t,@Param("videoId") String videoId);


	/**
	 * 根据VideoId删除
	 */
	 Integer deleteByVideoId(@Param("videoId") String videoId);


	/**
	 * 根据VideoId获取对象
	 */
	 T selectByVideoId(@Param("videoId") String videoId);


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
	 List<T> selectByUserId(@Param("userId") String userId);


	/**
	 * 根据 videoId 集合批量查询（用于获取视频名称）
	 */
	List<VideoInfo> selectByVideoIds(@Param("videoIds") Set<String> videoIds);


}
