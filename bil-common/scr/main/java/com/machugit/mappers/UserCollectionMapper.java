package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 用户收藏表 数据库操作接口
 */
public interface UserCollectionMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据CollectionId更新
	 */
	 Integer updateByCollectionId(@Param("bean") T t,@Param("collectionId") Integer collectionId);


	/**
	 * 根据CollectionId删除
	 */
	 Integer deleteByCollectionId(@Param("collectionId") Integer collectionId);


	/**
	 * 根据CollectionId获取对象
	 */
	 T selectByCollectionId(@Param("collectionId") Integer collectionId);


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
