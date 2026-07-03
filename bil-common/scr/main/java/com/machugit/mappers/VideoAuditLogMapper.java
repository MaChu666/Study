package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 视频审核日志表 数据库操作接口
 */
public interface VideoAuditLogMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据AuditId更新
	 */
	 Integer updateByAuditId(@Param("bean") T t,@Param("auditId") Integer auditId);


	/**
	 * 根据AuditId删除
	 */
	 Integer deleteByAuditId(@Param("auditId") Integer auditId);


	/**
	 * 根据AuditId获取对象
	 */
	 T selectByAuditId(@Param("auditId") Integer auditId);


	/**
	 * 根据VideoId获取对象
	 */
	 T selectByVideoId(@Param("videoId") String videoId);


}
