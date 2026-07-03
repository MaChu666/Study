package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 收藏视频表 数据库操作接口
 */
public interface FavoriteVideoMapper<T,P> extends BaseMapper<T,P> {

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
	 * 根据FolderId更新
	 */
	 Integer updateByFolderId(@Param("bean") T t,@Param("folderId") Integer folderId);


	/**
	 * 根据FolderId删除
	 */
	 Integer deleteByFolderId(@Param("folderId") Integer folderId);


	/**
	 * 根据FolderId获取对象
	 */
	 T selectByFolderId(@Param("folderId") Integer folderId);


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
