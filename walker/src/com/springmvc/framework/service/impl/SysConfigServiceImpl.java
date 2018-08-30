package com.springmvc.framework.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.framework.entity.TSysConfig;
import com.springmvc.framework.mapper.SysConfigMapper;
import com.springmvc.framework.service.SysConfigService;

@Service("sysConfigService")
public class SysConfigServiceImpl implements SysConfigService{
	
	private final static Logger logger = Logger.getLogger(SysConfigServiceImpl.class);
	
	@Resource
	private SysConfigMapper sysConfigMapper;

	@Override
	public List<TSysConfig> getSysConfigList() {		
		logger.info("获取系统配置列表Start");
		return sysConfigMapper.getSysConfigList();
	}

	@Override
	public List<Map<String, Object>> getSysBookType() {
		return sysConfigMapper.getSysBookType();
	}

	@Override
	public List<Map<String, Object>> getSysBookByParent(String PARENT_ID) {
		return sysConfigMapper.getSysBookByParent(PARENT_ID);
	}

	@Override
	public boolean updateSysTaskState() {
		logger.info("初始化任务状态Start");
		return sysConfigMapper.updateSysTaskState();
	}

	@Override
	public List<Map<String, Object>> getXmlBookType() {
		return sysConfigMapper.getXmlBookType();
	}

	@Override
	public List<Map<String, Object>> getXmlBookByParent(String PARENT_ID) {
		return sysConfigMapper.getXmlBookByParent(PARENT_ID);
	}

	@Override
	public Map<String, Object> getXmlBookById(String ID) {
		return sysConfigMapper.getXmlBookById(ID);
	}
}
