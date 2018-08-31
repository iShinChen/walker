package com.springmvc.logs.service.impl;

import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springmvc.logs.mapper.LogsMapper;
import com.springmvc.logs.service.LogsService;

/**
* 类说明
* @author shin chen
* 
*/
@Service("logsService")
public class LogsServiceImpl implements LogsService{

	private final static Logger logger = Logger.getLogger(LogsServiceImpl.class);
	
	@Autowired
	private LogsMapper logsMapper;
	
	@Override
	public Long logVV(Map<String, Object> paraMap) throws SQLException {
		logger.info("记录用户VV日志:" + paraMap.toString());
		logsMapper.insertLogVV(paraMap);
		try {
			return (Long) paraMap.get("ID");
		} catch (Exception e) {}
		
		return null;
	}

}
