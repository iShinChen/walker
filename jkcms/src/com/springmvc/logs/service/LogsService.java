package com.springmvc.logs.service;

import java.sql.SQLException;
import java.util.Map;

/**
* 日志记录-Service
* @author shin chen
* 
*/
public interface LogsService {

	/**
	 * 记录用户VV日志
	 * 
	 * @param paraMap日志参数
	 */
	public Long logVV(Map<String, Object> paraMap) throws SQLException;

}
