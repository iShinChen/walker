package com.springmvc.framework.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

public class StringUtils {
	
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	public static boolean isEmpty(String str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isEmptyOrBlank(Object str) {
		if (str == null || str.toString().trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isNumber(String str) {
		try {
			Double.valueOf(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isInteger(String str) {
		if (!isEmpty(str)) {
			try {
				Integer.parseInt(str);
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	public static boolean isLong(String str) {
		if (!isEmpty(str)) {
			try {
				Long.parseLong(str);
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	public static boolean regexMatch(String str, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static String upCaseFirstChar(String str) {
		if (str != null && str.trim().length() > 0) {
			str = str.trim();
			char firstChar = str.charAt(0);
			String firstStr = String.valueOf(firstChar).toUpperCase();
			return firstStr + str.substring(1);
		}
		return null;
	}

	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}

	/**
	 * 过滤特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		if ("".equals(str) || str.length() == 0) {
			return str;
		} else {
			String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）\"\"《》\\\\——+|{}【】‘；：”“’。，、？]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(str);
			return m.replaceAll("").trim().replaceAll(" ", "");
		}
	}

	/**
	 * 是否JsonObject格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isJsonObject(String str) {
		try {
			JSONObject.fromObject(str);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 是否JsonArray格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isJsonArray(String str) {
		try {
			JSONObject.fromObject(str);
			return true;
		} catch (Exception e2) {
		}
		return false;
	}

	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}
	
	/**
	 * 批量生成固定长度随机数字符串
	 * @param length 长度
	 * @param count 批量个数
	 * @return 随机数字符串列表
	 */
	public static Set<String> generateNumSN(int length, int count) {
		Set<String> list = new HashSet<String>();
		while(true) {
			StringBuffer sb = new StringBuffer("");
			for (int j = 0; j < length; j++) {
				sb.append((int) (10 * Math.random()));
			}
			if (!list.contains(sb.toString())) {
				list.add(sb.toString());
			}
			
			if (list.size() == count) {
				break;
			}
		}
		return list;
	}
	
	/**
	 * 批量带前缀生成固定长度随机数字符串
	 * @param length 长度
	 * @param count 批量个数
	 * @param preStr 前缀字符串
	 * @return 随机数字符串列表
	 */
	public static Set<String> generateNumSN(int length, int count, String preStr) {
		Set<String> list = new HashSet<String>();
		while(true) {
			StringBuffer sb = new StringBuffer(preStr);
			for (int j = 0; j < length; j++) {
				sb.append((int) (10 * Math.random()));
			}
			if (!list.contains(sb.toString())) {
				list.add(sb.toString());
			}
			
			if (list.size() == count) {
				break;
			}
		}
		return list;
	}
	
	public static String getYearMonth(){
		Calendar now = Calendar.getInstance();
		return String.valueOf(now.get(Calendar.YEAR)) + String.format("%02d", now.get(Calendar.MONTH ) + 1);
	}
	
	public static String getDateTime(){
		Calendar now = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); 
		return sdf.format(now.getTime());
	}
	
	//获取视频文件时长 0:00:00
	public static String formatDuration(long time){
		String duration = "";
		int h=(int)time/(60*60*1000);  
	    int m=(int)(time%(60*60*1000))/(60*1000);  
	    int s=(int)(time%(60*1000))/(1000);
	    duration = String.valueOf(h);
	    if(m<10){
	    	duration = duration +":0" +m;
	    }else{
	    	duration = duration +":" +m;
	    }if(s<10){
	    	duration = duration +":0" +s;
	    }else{
	    	duration = duration +":" +s;
	    }
		return duration;
	}
	
	public static String formatTime(String timeTemp) {  
        int timeParam = 0;  
        timeParam = Integer.valueOf((String) timeTemp);  

        int second = timeParam % 60;  
        int minuteTemp = timeParam / 60;  
        if (minuteTemp > 0) {  
            int minute = minuteTemp % 60;  
            int hour = minuteTemp / 60;  
            if (hour > 0) {  
                return (hour >= 10 ? (hour + "") : ("0" + hour)) + ":" + (minute >= 10 ? (minute + "") : ("0" + minute))  
                        + ":" + (second >= 10 ? (second + "") : ("0" + second));  
            } else {  
                return "00:" + (minute >= 10 ? (minute + "") : ("0" + minute)) + ":"  
                        + (second >= 10 ? (second + "") : ("0" + second));  
            }  
        } else {  
            return "00:00:" + (second >= 10 ? (second + "") : ("0" + second));  
        }  
    }
}
