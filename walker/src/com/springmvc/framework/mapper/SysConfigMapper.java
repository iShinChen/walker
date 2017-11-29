package com.springmvc.framework.mapper;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.TSysConfig;

public interface SysConfigMapper {
	
	List<TSysConfig> getSysConfigList();
	
	List<Map<String, Object>> getSysBookType();
	
	List<Map<String, Object>> getSysBookByParent(String PARENT_ID);
	
	List<Map<String, Object>> getFtpConfigList();
	
	boolean updateSysTaskState();
	
	List<Map<String, Object>> getXmlBookType();
	
	Map<String, Object> getXmlBookById(String ID);
	
	List<Map<String, Object>> getXmlBookByParent(String PARENT_ID);

}
