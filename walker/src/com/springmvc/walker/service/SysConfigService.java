package com.springmvc.walker.service;

import java.util.List;
import java.util.Map;

import com.springmvc.walker.entity.TSysConfig;

public interface SysConfigService {

	List<TSysConfig> getSysConfigList();
	
	List<Map<String, Object>> getSysBookType();
	
	List<Map<String, Object>> getSysBookByParent(String PARENT_ID);
	
	public void updateSysTaskState();
}
