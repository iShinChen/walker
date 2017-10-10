package com.springmvc.walker.mapper;

import java.util.List;
import java.util.Map;

import com.springmvc.walker.entity.TSysConfig;

public interface SysConfigMapper {
	
	List<TSysConfig> getSysConfigList();
	
	List<Map<String, Object>> getSysBookType();
	
	List<Map<String, Object>> getSysBookByParent(String PARENT_ID);
	
	List<Map<String, Object>> getFtpConfigList();
	
	void updateSysTaskState();

}
