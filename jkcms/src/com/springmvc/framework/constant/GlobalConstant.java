package com.springmvc.framework.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import com.springmvc.framework.entity.TSysConfig;
import com.springmvc.framework.service.SysConfigService;

public class GlobalConstant {
	private static final Logger logger = Logger.getLogger(GlobalConstant.class);
	
	public static Map<String, String> SYS_MAP = new HashMap<String, String>();
	public static Map<String, List<Map<String, Object>>> SYS_BOOK = new HashMap<String, List<Map<String, Object>>>();
	
	public static final String EXPORT_EXCEL = "EXPORT_EXCEL";
	
	public static final String EXPORT_PDF_MODEL = "EXPORT_PDF_MODEL";
	
	public static final String EXPORT_PDF = "EXPORT_PDF";
	
	public static final String IMPORT_FILE = "IMPORT_FILE";
	
	public static final String IMPORT_PICTURE = "IMPORT_PICTURE";
	
	public static final String IMPORT_VIDEO = "IMPORT_VIDEO";
	
	public static final String FTP_TARGET_IP = "FTP_TARGET_IP";
	
	public static final String FTP_TARGET_PORT = "FTP_TARGET_PORT";
	
	public static final String FTP_TARGET_USER = "FTP_TARGET_USER";
	
	public static final String FTP_TARGET_PASSWORD = "FTP_TARGET_PASSWORD";
	
	public static final String FTP_ADDR = "FTP_ADDR";

	public static final String FILE_WEB_SERVER = "FILE_WEB_SERVER";
	
	public static final String FTP_TARGET_ROOTPATH = "FTP_TARGET_ROOTPATH";
	
	public static final String XML_BOOK_SERIES = "SERIES";
	public static final String XML_BOOK_PROGRAM = "PROGRAM";
	public static final String XML_BOOK_MOVIE = "MOVIE";
	public static final String XML_BOOK_PICTURE = "PICTURE";
	
	public static Map<String, List<Map<String, Object>>> XML_BOOK = new HashMap<String, List<Map<String, Object>>>();
	
	public static Map<String, List<Map<String, Object>>> XML_ELEMENTS = new HashMap<String, List<Map<String, Object>>>();
		
	public static void initSysConfig() {
		logger.info("系统配置加载中...");
		List<TSysConfig> list = new ArrayList<TSysConfig>();
		try {
			ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
			SysConfigService sysConfigService = (SysConfigService) context.getBean("sysConfigService");
			list = sysConfigService.getSysConfigList();
		} catch (Exception e) {
			logger.error("系统配置项加载异常", e);
		}

		if (list != null) {
			for (TSysConfig sysConfig : list) {
				GlobalConstant.SYS_MAP.put(sysConfig.getCode(), sysConfig.getValue());
			}
		}
		logger.info("系统配置加载完成!");
	}
	
	public static void initSysBook() {
		logger.info("加载数据字典中...");
		List<Map<String, Object>> typeList = new ArrayList<Map<String, Object>>();
		try {
			ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
			SysConfigService sysConfigService = (SysConfigService) context.getBean("sysConfigService");
			typeList = sysConfigService.getSysBookType();
			
			if(typeList != null)
			{
				for(Map<String, Object> map:typeList)
				{
					GlobalConstant.SYS_BOOK.put(String.valueOf(map.get("PARENT_ID")), sysConfigService.getSysBookByParent(String.valueOf(map.get("PARENT_ID"))));
				}
			}
		} catch (Exception e) {
			logger.error("加载数据字典异常", e);
		}
		logger.info("数据字典加载完成!");
	}
	
	public static void resetTaskState() {
		logger.info("自动任务状态重置...");
		try {
			ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
			SysConfigService sysConfigService = (SysConfigService) context.getBean("sysConfigService");
			sysConfigService.updateSysTaskState();
		} catch (Exception e) {
			logger.error("系统配置项加载异常", e);
		}
		logger.info("自动任务状态重置完成!");
	}
}
