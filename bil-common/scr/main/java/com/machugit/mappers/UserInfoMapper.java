package com.machugit.mappers;

import com.machugit.entity.po.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 用户信息表 数据库操作接口
 */
public interface UserInfoMapper<T,P> extends BaseMapper<T,P> {

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
	 * 根据UseName更新
	 */
	 Integer updateByUseName(@Param("bean") T t,@Param("useName") String useName);


	/**
	 * 根据UseName删除
	 */
	 Integer deleteByUseName(@Param("useName") String useName);


	/**
	 * 根据UseName获取对象
	 */
	 T selectByUseName(@Param("useName") String useName);


	/**
	 * 根据Email更新
	 */
	 Integer updateByEmail(@Param("bean") T t,@Param("email") String email);


	/**
	 * 根据Email删除
	 */
	 Integer deleteByEmail(@Param("email") String email);


	/**
	 * 根据Email获取对象
	 */
	 T selectByEmail(@Param("email") String email);


	/**
	 * 根据 userId 集合批量查询（用于获取用户昵称）
	 */
	List<UserInfo> selectByUserIds(@Param("userIds") Set<String> userIds);




}
