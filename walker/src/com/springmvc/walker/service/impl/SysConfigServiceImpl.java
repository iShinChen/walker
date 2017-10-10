package com.springmvc.walker.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.walker.entity.TSysConfig;
import com.springmvc.walker.mapper.SysConfigMapper;
import com.springmvc.walker.service.SysConfigService;

@Service("sysConfigService")
public class SysConfigServiceImpl implements SysConfigService{
	
	private final static Logger logger = Logger.getLogger(SysConfigServiceImpl.class);
	
	@Resource
	private SysConfigMapper sysConfigMapper;

	@Override
	public List<TSysConfig> getSysConfigList() {
		
		List<TSysConfig> list = new ArrayList<TSysConfig>();
		try {
			logger.info("获取系统配置列表Start");
			list = sysConfigMapper.getSysConfigList();
			logger.info("获取系统配置列表Success");
		} catch (Exception e) {
			logger.error("操作发生异常", e);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getSysBookType() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = sysConfigMapper.getSysBookType();
		} catch (Exception e) {
			logger.error("操作发生异常", e);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getSysBookByParent(String PARENT_ID) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = sysConfigMapper.getSysBookByParent(PARENT_ID);
		} catch (Exception e) {
			logger.error("操作发生异常", e);
		}
		return list;
	}

	@Override
	public void updateSysTaskState() {
		try {
			logger.info("初始化任务状态Start");
			sysConfigMapper.updateSysTaskState();
			logger.info("初始化任务状态成功");
		} catch (Exception e) {
			logger.error("操作发生异常", e);
		}
	}

}
