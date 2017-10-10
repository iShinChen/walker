package com.springmvc.walker.constant;

import java.util.HashMap;
import java.util.Map;

public class MappingConstant {
	
	public static Map<String, Object> movieSize = new HashMap<String, Object>();
	
	public static Map<String, Object> fileNotExistCount = new HashMap<String, Object>();
	
	public static Map<String, Object> equalCount = new HashMap<String, Object>();
	
	public static Map<String, Thread> DOWNLOADING_THREAD = new HashMap<String, Thread>();
	
	public static Map<String, Thread> UPLOADING_THREAD = new HashMap<String, Thread>();
	
	public static Map<String, Object> DOWNLOADING_PROGRESS = new HashMap<String, Object>();
	
	public static Map<String, Object> UPLOADING_PROGRESS = new HashMap<String, Object>();
	
	
	/**
	 * 业务类型
	 */
	public static final String ASSET_DESC_SERIES = "1";
	public static final String ASSET_DESC_PROGRAM = "2";
	public static final String ASSET_DESC_MOVIE = "3";
	
	/**
	 * 操作类型
	 */
	public static final String ASSET_OPERATION_ADD = "1";
	public static final String ASSET_OPERATION_DEL = "2";
	public static final String ASSET_OPERATION_CANCEL = "3";
	
	
}
