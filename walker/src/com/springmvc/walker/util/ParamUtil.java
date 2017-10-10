package com.springmvc.walker.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
/**
 * 参数工具类
 * @author may
 *
 */
public class ParamUtil {
	/**
	 * 获取相关请求参数
	 * 
	 * @param request
	 *            系统获得
	 * @param fields
	 *            参数字段名数组
	 * @return Map<String,String> 参数集合
	 */
	public static Map<String, Object> getParamMap(HttpServletRequest request,
			String[] fields) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (int i = 0; i < fields.length; i++) {
			if (!paramMap.containsKey(fields[i])) {
				paramMap.put(fields[i],
						getStringParameter(request.getParameter(fields[i])));
			}
		}
		return paramMap;
	}
	
	/**
	 * 获取一个String的参数，如果找不到则返回null
	 * 
	 * @param name
	 * @return String
	 */
	public static String getStringParameter(String name) {
		if (name == null) {
			return "";
		}
		return name;
	}
	
}
