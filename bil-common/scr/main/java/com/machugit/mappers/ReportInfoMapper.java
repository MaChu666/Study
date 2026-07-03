package com.machugit.mappers;

import org.apache.ibatis.annotations.Param;

/**
 * 举报信息表 数据库操作接口
 */
public interface ReportInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据ReportId更新
	 */
	 Integer updateByReportId(@Param("bean") T t,@Param("reportId") Integer reportId);


	/**
	 * 根据ReportId删除
	 */
	 Integer deleteByReportId(@Param("reportId") Integer reportId);


	/**
	 * 根据ReportId获取对象
	 */
	 T selectByReportId(@Param("reportId") Integer reportId);


	/**
	 * 根据ReporterId获取对象
	 */
	 T selectByReporterId(@Param("reporterId") String reporterId);


}
