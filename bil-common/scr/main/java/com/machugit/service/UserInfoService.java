package com.machugit.service;

import java.util.List;

import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.query.UserInfoQuery;
import com.machugit.entity.po.UserInfo;
import com.machugit.entity.vo.PaginationResultVO;


/**
 * 用户信息表 业务接口
 */
public interface UserInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<UserInfo> findListByParam(UserInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(UserInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserInfo bean,UserInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserInfoQuery param);

	/**
	 * 根据UserId查询对象
	 */
	UserInfo getUserInfoByUserId(String userId);


	/**
	 * 根据UserId修改
	 */
	Integer updateUserInfoByUserId(UserInfo bean,String userId);


	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoByUserId(String userId);


	/**
	 * 根据UseName查询对象
	 */
	UserInfo getUserInfoByUseName(String useName);


	/**
	 * 根据UseName修改
	 */
	Integer updateUserInfoByUseName(UserInfo bean,String useName);


	/**
	 * 根据UseName删除
	 */
	Integer deleteUserInfoByUseName(String useName);


	/**
	 * 根据Email查询对象
	 */
	UserInfo getUserInfoByEmail(String email);


	/**
	 * 根据Email修改
	 */
	Integer updateUserInfoByEmail(UserInfo bean,String email);


	/**
	 * 根据Email删除
	 */
	Integer deleteUserInfoByEmail(String email);


	/**
	 * 注册验证
	 */
	void register(String email,String useName,String registerPassword);

	/**
 	* 登录方法，用于用户认证并返回令牌用户信息
 	* @param email 用户邮箱，用于标识用户身份
 	* @param password 用户密码，用于验证用户身份
 	* @param ip 用户登录IP地址，用于记录登录位置信息
 	* @return TokenUserInfoDto 包含用户令牌和用户信息的DTO对象
 	*/
	TokenUserInfoDto login(String email, String password, String ip);

}