package com.springmvc.walker.util;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;

import com.springmvc.framework.util.StringUtils;

/**
 * 针对ServletRequest提供的Util类
 * <p>
 * 创建日期：2016-2-29<br>
 * 修改历史：<br>
 * 修改日期：<br>
 * 修改作者：<br>
 * 修改内容：<br>
 * 
 * @author qupeng
 * @version 1.0
 */
public class RequestUtils {
	
	/**
	 * 字符串校验正则表达式：ID主键使用
	 */
	public static final String STRING_REGEX_PK_ID = "^[a-zA-Z0-9]{32}$";
	
	/**
	 * 字符串校验正则表达式：字母和数字（不限长度）
	 */
	public static final String STRING_REGEX_ID = "^[a-zA-Z0-9]*$";
	
	public static final String STRING_REGEX_DATE = "^([0-9\\-]|Q|FY|D|W)*$";
	
	public static final String STRING_REGEX_NUMBER = "^[0-9]*$";
	
	public static final String STRING_REGEX_TEXT = "^([a-zA-Z0-9]|\\%)*$";
	
	/*
	 * 频度使用
	 */
	public static final String STRING_REGEX_RATE = "^(DAY|WEEK|MONTH|YEAR|QUATER)$";
	
	/**
	 * 字符串校验正则表达式：编码使用
	 */
	public static final String STRING_REGEX_CODE = "^[_a-zA-Z][a-zA-Z0-9_-]*$";
	
	/**
	 * 字符串校验正则表达式：FUNCCODE使用
	 */
	public static final String STRING_REGEX_FUNCCODE = "^[a-zA-Z_/][a-zA-Z0-9_/\\.]*$";
	
	/**
	 * 字符串校验正则表达式：URL
	 */
	public static final String STRING_REGEX_URL = "^(((https|http)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*)?(/[a-zA-Z0-9\\&%_\\./-~-]*)?$";
	
	private static Set<String> SPECIALCHAR = new HashSet<String>();
	static {
		SPECIALCHAR.add(",");
		SPECIALCHAR.add("<");
		SPECIALCHAR.add(">");
		SPECIALCHAR.add("\"");
		SPECIALCHAR.add("'");
	}

	/**
	 * 获取request中的所有参数，并过滤SQL特殊字符
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map getMapParameter(ServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String name = (String) params.nextElement();
			String[] values = request.getParameterValues(name);
			for (int i = 0; i < values.length; i++) {
				values[i] = escapeStringParameter(values[i], true);
			}

			map.put(name, values);
		}
		return map;
	}

	/**
	 * 获取一个Integer类型的参数，如果没有返回空，如果参数并非数字抛出异常
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 * @return Integer的参数或者为null
	 */
	public static Integer getIntParameter(ServletRequest request, String name) {
		String value = request.getParameter(name);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		if (StringUtils.isInteger(value)) {
			return Integer.parseInt(value);
		} else {
			throw new RuntimeException("参数" + name + "不是整型数字(Int)!");
		}
	}

	/**
	 * 获取一个int数组类型的参数，如果找不到就返回空数组
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 */
	public static Integer[] getIntParameters(ServletRequest request, String name) {
		String[] values = request.getParameterValues(name);
		if (values == null || values.length == 0) {
			return null;
		}

		Integer[] returnVals = new Integer[values.length];
		for (int i = 0; i < values.length; i++) {
			if (StringUtils.isEmpty(values[i])) {
				returnVals[i] = null;
			}
			if (StringUtils.isInteger(values[i])) {
				returnVals[i] = Integer.parseInt(values[i]);
			} else {
				throw new RuntimeException("参数" + name + "含有非整型数字(Int)的内容!");
			}
		}
		return returnVals;
	}

	/**
	 * 获取一个Long类型的参数，如果不存在则返回null，如果不是数字则抛出异常
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 * @return
	 */
	public static Long getLongParameter(ServletRequest request, String name) {
		String value = request.getParameter(name);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		if (StringUtils.isLong(value)) {
			return Long.parseLong(value);
		} else {
			throw new RuntimeException("参数" + name + "不是长整型数字(Long)!");
		}
	}

	/**
	 * 返回一个long数组的参数，如果找不到或者不是数字则返回空数组，不会抛出异常
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 */
	public static Long[] getLongParameters(HttpServletRequest request,
			String name) {
		String[] values = request.getParameterValues(name);
		if (values == null || values.length == 0) {
			return null;
		}

		Long[] returnVals = new Long[values.length];
		for (int i = 0; i < values.length; i++) {
			if (StringUtils.isEmpty(values[i])) {
				returnVals[i] = null;
			}
			if (StringUtils.isLong(values[i])) {
				returnVals[i] = Long.parseLong(values[i]);
			} else {
				throw new RuntimeException("参数" + name + "含有非长整型数字(Long)的内容!");
			}
		}
		return returnVals;
	}

	/**
	 * 获取一个Double类型的参数，如果找不到则返回null，如果不是数值则抛出异常
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 */
	public static Double getDoubleParameter(ServletRequest request, String name) {
		String value = request.getParameter(name);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		if (StringUtils.isNumber(value)) {
			return Double.parseDouble(value);
		} else {
			throw new RuntimeException("参数" + name + "不是数字(Double)!");
		}
	}

	/**
	 * 获取一个double数组的参数，如果找不到或者不是数值则返回空数组
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 */
	public static Double[] getDoubleParameters(ServletRequest request,
			String name) {
		String[] values = request.getParameterValues(name);
		if (values == null || values.length == 0) {
			return null;
		}

		Double[] returnVals = new Double[values.length];
		for (int i = 0; i < values.length; i++) {
			if (StringUtils.isEmpty(values[i])) {
				returnVals[i] = null;
			}
			if (StringUtils.isNumber(values[i])) {
				returnVals[i] = Double.parseDouble(values[i]);
			} else {
				throw new RuntimeException("参数" + name + "含有非数字(Double)的内容!");
			}
		}
		return returnVals;
	}

	/**
	 * 获取一个String的参数，如果找不到则返回null
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 */
	public static String getStringParameter(HttpServletRequest request,
			String name) {
		String value = request.getParameter(name);
		if (StringUtils.isJsonObject(value)) {
			return JSONObject.fromObject(value).toString();
		}
		else if(StringUtils.isJsonArray(value)) {
			return JSONArray.fromObject(value).toString();
		}
		return getStringParameter(request, name, null, -1);
	}

	/**
	 * 获取一个String的参数，如果找不到则返回null
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 * @param regex
	 *            正则表达式
	 */
	public static String getStringParameter(HttpServletRequest request,
			String name, String regex) {
		return getStringParameter(request, name, regex, -1);
	}

	/**
	 * 获取一个String的参数，如果找不到则返回null
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 * @param length
	 * 			     字符串长度
	 */
	public static String getStringParameter(HttpServletRequest request,
			String name, int length) {
		return getStringParameter(request, name, null, length);
	}

	/**
	 * 获取一个String的参数，如果找不到则返回null
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 * @param regex
	 *            正则表达式
	 * @param length
	 * 			     字符串长度
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String getStringParameter(HttpServletRequest request,
			String name, String regex, int length) {
		String value = request.getParameter(name);

		boolean hasName = false;
		Enumeration params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String pName = (String) params.nextElement();
			if (pName.equals(name)) {
				hasName = true;
				break;
			}
		}
		if (!hasName) {
			return null;
		}
		if (StringUtils.isEmpty(value)) {
			return "";
		}
		String url = request.getRequestURI();
		try {
			if (regex != null && !value.matches(regex)) {
				if (url.endsWith(".do")) {
					throw new RuntimeException("传入参数(" + name + ")不合法!");
				} else {
					return "";
				}
			}
		} catch (PatternSyntaxException e) {
			throw new RuntimeException("错误的正则表达式(" + regex + ")!");
		}
		
		if (length > 0 && value.length() > length) {
			if (url.endsWith(".do")) {
				throw new RuntimeException("传入参数(" + name + ")长度超长(≤" + length+ ")!");
			} else {
				return "";
			}
		}

		return escapeStringParameter(value, true);
	}

	/**
	 * 获取一个String数组的参数，如果不存在则返回空数组
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 */
	public static String[] getStringParameters(HttpServletRequest request,
			String name) {
		return getStringParameters(request, name, null, -1);
	}

	/**
	 * 获取一个String数组的参数，如果不存在则返回空数组
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 * @param regex
	 *            正则表达式
	 */
	public static String[] getStringParameters(HttpServletRequest request,
			String name, String regex) {
		return getStringParameters(request, name, regex, -1);
	}

	/**
	 * 获取一个String数组的参数，如果不存在则返回空数组
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 * @param regex
	 *            正则表达式
	 */
	public static String[] getStringParameters(HttpServletRequest request,
			String name, int length) {
		return getStringParameters(request, name, null, length);
	}

	/**
	 * 获取一个String数组的参数，如果不存在则返回空数组
	 * 
	 * @param request
	 *            当前的HTTP request对象
	 * @param name
	 *            参数名称
	 * @param regex
	 *            正则表达式
	 * @param length
	 * 			     字符串长度
	 */
	public static String[] getStringParameters(HttpServletRequest request,
			String name, String regex, int length) {
		String[] values = request.getParameterValues(name);
		if (values == null || values.length == 0) {
			return null;
		}
		String url = request.getRequestURI();
		for (int i = 0; i < values.length; i++) {
			if (values[i] != null) {
				try {
					if (regex != null && !values[i].matches(regex)) {
						if (url.endsWith(".do")) {
							throw new RuntimeException("传入参数(" + name + ")不合法!");
						} else {
							values[i] = "";
							continue;
						}
					}
				} catch (PatternSyntaxException e) {
					throw new RuntimeException("错误的正则表达式!");
				}
				
				if (length > 0 && values[i].length() > length) {
					if (url.endsWith(".do")) {
						throw new RuntimeException("传入参数(" + name + ")长度超长(≤" + length+ ")!");
					} else {
						values[i] = "";
						continue;
					}
				}
				
				values[i] = escapeStringParameter(values[i], true);
			}
		}

		return values;
	}

	/**
	 * 拼装请求的完整URL
	 * 
	 * @param url
	 * @param parameterNames
	 * @param parameterValues
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String getRequestURL(String url, String[] parameterNames,
			String[] parameterValues) throws IllegalArgumentException {
		if (url == null)
			return url;
		StringBuffer targetUrl = new StringBuffer(url);
		String anchor = null;
		int hash = url.indexOf('#');
		if (hash >= 0) {
			anchor = url.substring(hash + 1);
			targetUrl.setLength(hash);
		}
		boolean question = false;
		if (targetUrl.toString().indexOf('?') >= 0)
			question = true;
		if (parameterNames != null && parameterValues != null) {
			int length = parameterNames.length;
			for (int index = 0; index < length; index++) {
				if (!question) {
					targetUrl.append('?');
					question = true;
				} else
					targetUrl.append('&');
				targetUrl.append(parameterNames[index]);
				targetUrl.append('=');
				targetUrl.append(parameterValues[index]);
			}
		}
		if (anchor != null)
			targetUrl.append('#').append(anchor);
		return targetUrl.toString();
	}

	/**
	 * 拼装请求的完整的URL
	 * 
	 * @param request
	 * @return
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("rawtypes")
	public static String getRequestURL(HttpServletRequest request)
			throws IllegalArgumentException {
		String requestUrl = request.getRequestURL().toString();
		String queryString = request.getQueryString();
		StringBuffer targetUrl = new StringBuffer();
		targetUrl.append(requestUrl);
		if (queryString != null && queryString.trim().length() > 0)
			targetUrl.append("?").append(queryString);
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			String temp = targetUrl.toString();
			String anchor = null;
			int hash = temp.indexOf('#');
			if (hash >= 0) {
				anchor = temp.substring(hash + 1);
				targetUrl.setLength(hash);
				temp = targetUrl.toString();
			}
			boolean question = temp.indexOf('?') >= 0;
			Enumeration params = request.getParameterNames();
			while (params.hasMoreElements()) {
				String name = (String) params.nextElement();
				String[] values = RequestUtils.getStringParameters(request,
						name);
				if (values == null) {
					if (!question) {
						targetUrl.append('?');
						question = true;
					} else
						targetUrl.append("&");
					targetUrl.append(name).append("=");
				} else {
					for (int i = 0; i < values.length; i++) {
						if (!question) {
							targetUrl.append('?');
							question = true;
						} else
							targetUrl.append("&");
						targetUrl.append(name).append("=").append(values[i]);
					}
				}
			}
			if (anchor != null)
				targetUrl.append('#').append(anchor);
		}
		return targetUrl.toString();
	}

	public static String escapeStringParameter(String value, boolean isChinese) {
		if (isChinese) {// 是中文
			StringBuffer temp = new StringBuffer();
			for (int i = 0; i < value.length(); i++) {
				if (SPECIALCHAR.contains(String.valueOf(value.charAt(i)))) {
					String tempChar = String.valueOf(value.charAt(i));
					tempChar = StringEscapeUtils.escapeHtml(tempChar);
					tempChar = StringEscapeUtils.escapeJavaScript(tempChar);
					tempChar = StringEscapeUtils.escapeSql(tempChar);
					temp.append(tempChar);
				} else {
					temp.append(value.charAt(i));
				}
			}
			value = temp.toString();
		} else {// 不是中文
			value = StringEscapeUtils.escapeHtml(value);
			value = StringEscapeUtils.escapeJavaScript(value);
			value = StringEscapeUtils.escapeSql(value);
		}
		return value;
	}

	public static String getRootPath() {
		String classPath = RequestUtils.class.getClassLoader().getResource("/").getPath();
		String rootPath = "";
		// windows下
		if ("\\".equals(File.separator)) {
			rootPath = classPath.substring(1, classPath
					.indexOf("/WEB-INF/classes"));
			rootPath = rootPath.replace("/", "\\");
		}
		// linux下
		if ("/".equals(File.separator)) {
			rootPath = classPath.substring(0, classPath
					.indexOf("/WEB-INF/classes"));
			rootPath = rootPath.replace("\\", "/");
		}
		return rootPath;
	}
}
