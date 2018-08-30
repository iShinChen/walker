package com.springmvc.framework.util;


import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;


/**
 * JSON工具类
 * @author Administrator
 *
 */
public class MapJsonUtil {
	 /**
	   * 对象转换JSONSTRING
	   * <ul>
	   * <li> @param obj 对象
	   * <li> @return String jsonString
	   * </ul>
	   */
	  public static String object2json(Object obj) {  
	    StringBuilder json = new StringBuilder();  
	    if (obj == null) {  
	      json.append("null");  
	    } else if (obj instanceof String ||
	         obj instanceof Integer ||
	         obj instanceof Float  ||
	         obj instanceof Short ||
	         obj instanceof Double || 
	         obj instanceof Long ||
	         obj instanceof BigDecimal ||
	         obj instanceof BigInteger || 
	         obj instanceof Byte) {  
	      json.append("\"").append(string2json(obj.toString())).append("\"");  
	    }else if(obj instanceof Boolean){
	    	json.append(string2json(obj.toString()));
	    } else if (obj instanceof Object[]) {  
	      json.append(array2json((Object[]) obj));  
	    } else if (obj instanceof List) {  
	      json.append(list2json((List<?>) obj));  
	    } else if (obj instanceof Map) {  
	      json.append(map2json((Map<?, ?>) obj));  
	    } else if (obj instanceof Set) {  
	      json.append(set2json((Set<?>) obj));  
	    } else {  
	      json.append(bean2json(obj));  
	    }  
	    return json.toString();  
	  }  
	 
	  /**
	   * 实体对象转换JSONSTRING
	   * <ul>
	   * <li> @param bean 实体对象
	   * <li> @return String JSONSTRING
	   * </ul>
	   */
	  public static String bean2json(Object bean) {  
	    StringBuilder json = new StringBuilder();  
	    json.append("\\");  
	    PropertyDescriptor[] props = null;  
	    try {  
	      props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();  
	    } catch (IntrospectionException e) {}  
	    if (props != null) {  
	      for (int i = 0; i < props.length; i++) {  
	        try {  
	          String name = object2json(props[i].getName());  
	          String value = object2json(props[i].getReadMethod().invoke(bean));  
	          name = name.replace("\"", "");
	          //value = value.replace("\"", "'");
	          json.append("\""+name+"\"");  
	          json.append(":");  
	          json.append(value);  
	          json.append(",");  
	        } catch (Exception e) {}  
	      }  
	      json.setCharAt(json.length() - 1, '}');  
	    } else {  
	      json.append("}");  
	    }  
	    return json.toString();  
	  }  
	 
	  /**
	   * 集合转换成jsonstring
	   * <ul>
	   * <li> @param list 数据集合
	   * <li> @return String jsonstring
	   * </ul>
	   */
	  public static String list2json(List<?> list) {  
	    StringBuilder json = new StringBuilder();  
	    json.append("[");  
	    if (list != null && list.size() > 0) {  
	      for (Object obj : list) {  
	        json.append(object2json(obj));  
	        json.append(",");  
	      }  
	      json.setCharAt(json.length() - 1, ']');  
	    } else {  
	      json.append("]");  
	    }  
	    return json.toString();  
	  }  
	 
	  /**
	   * 数组转换成jsonString
	   * <ul>
	   * <li> @param array 数组对象
	   * <li> @return String JSONSTRING
	   * </ul>
	   */
	  public static String array2json(Object[] array) {  
	    StringBuilder json = new StringBuilder();  
	    json.append("[");  
	    if (array != null && array.length > 0) {  
	      for (Object obj : array) {  
	        json.append(object2json(obj));  
	        json.append(",");  
	      }  
	      json.setCharAt(json.length() - 1, ']');  
	    } else {  
	      json.append("]");  
	    }  
	    return json.toString();  
	  }  
	 
	  /**
	   * MAP转换成JSONSTRING
	   * <ul>
	   * <li> @param map MAP对象
	   * <li> @return String JSONSTRING
	   * </ul>
	   */ 
	  public static String map2json(Map<?, ?> map) {  
	    StringBuilder json = new StringBuilder();  
	    json.append("\\");  
	    if (map != null && map.size() > 0) {  
	      for (Object key : map.keySet()) {  
	        json.append(object2json(key));  
	        json.append(":");  
	        json.append(object2json(map.get(key)));  
	        json.append(",");  
	      }  
	      json.setCharAt(json.length() - 1, '}');  
	    } else {  
	      json.append("}");  
	    }  
	    return json.toString();  
	  }  
	 
	  /**
	   * set转换成JSONSTRING
	   * <ul>
	   * <li> @param set set对象
	   * <li> @return String JSONSTRING
	   * </ul>
	   */
	  public static String set2json(Set<?> set) {  
	    StringBuilder json = new StringBuilder();  
	    json.append("[");  
	    if (set != null && set.size() > 0) {  
	      for (Object obj : set) {  
	        json.append(object2json(obj));  
	        json.append(",");  
	      }  
	      json.setCharAt(json.length() - 1, ']');  
	    } else {  
	      json.append("]");  
	    }  
	    return json.toString();  
	  }  
	 
	  /**
	   * String 转换 jsonstring
	   * <ul>
	   * <li> @param s String对象
	   * <li> @return string jsonstring
	   * </ul>
	   */
	  public static String string2json(String s) {  
	    if (s == null)  
	      return "";  
	    StringBuilder sb = new StringBuilder();  
	    for (int i = 0; i < s.length(); i++) {  
	      char ch = s.charAt(i);  
	      switch (ch) {  
	      case '"':  
	        sb.append("\\\"");  
	        break;  
	      case '\\':  
	        sb.append("\\\\");  
	        break;  
	      case '\b':  
	        sb.append("\\b");  
	        break;  
	      case '\f':  
	        sb.append("\\f");  
	        break;  
	      case '\n':  
	        sb.append("\\n");  
	        break;  
	      case '\r':  
	        sb.append("\\r");  
	        break;  
	      case '\t':  
	        sb.append("\\t");  
	        break;  
	      case '/':  
	        sb.append("\\/");  
	        break;  
	      default:  
	        if (ch >= '\u0000' && ch <= '\u001F') {  
	          String ss = Integer.toHexString(ch);  
	          sb.append("\\u");  
	          for (int k = 0; k < 4 - ss.length(); k++) {  
	            sb.append('0');  
	          }  
	          sb.append(ss.toUpperCase());  
	        } else {  
	          sb.append(ch);  
	        }  
	      }  
	    }  
	    return sb.toString();  
	  }  
	
/*	*//**
	 * 将json数组转换为字符串数组
	 * @param jsonArr JSONArray
	 * @return 字符串数组
	 *//*
	public String[] JSONArr2StrArr(JSONArray jsonArr){
		if (jsonArr==null) return null;
		String[] strArr=new String[jsonArr.length()];
		for (int i=0;i<strArr.length;i++){
			try {
				strArr[i]=jsonArr.getString(i);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
		return strArr;
	}
	*//**
	 * 将json数组转换为对象数组
	 * @param jsonArr JSONArray
	 * @return 对象数组
	 *//*
	public Object[] JSONArr2ObjectArr(JSONArray jsonArr){
		if (jsonArr==null) return null;
		Object[] ObjArr=new Object[jsonArr.length()];
		for (int i=0;i<ObjArr.length;i++){
			try {
				ObjArr[i]=jsonArr.get(i);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
		return ObjArr;
	}*/
	
    /**  
     * 从一个JSON 对象字符格式中得到一个java对象 
     * @param jsonString String
     * @param pojoCalss Class<?>
     * @return Object
     */ 
    public static Object jsonStr2Object(String jsonString,Class<?> pojoCalss){ 
        Object pojo; 
        JSONObject jsonObject = JSONObject.fromObject( jsonString ); 
        pojo = JSONObject.toBean(jsonObject,pojoCalss); 
        return pojo; 
    } 
    
    /**
     * 从一个JSON对象字符格式中得到一个java的对象组合
     * @param jsonString json字符串
     * @param pojoCalss  映射class
     * @param subClass   子映射class
     * @return  Object
     */
    @SuppressWarnings("rawtypes")
	public static Object jsonStr2Object(String jsonString,Class<?> pojoCalss,Map subClass)
    {
    	Object pojo; 
        JSONObject jsonObject = JSONObject.fromObject( jsonString ); 
        pojo = JSONObject.toBean(jsonObject,pojoCalss,subClass); 
        return pojo; 
    }
    

    /** 
     * 从json HASH表达式中获取一个map，改map支持嵌套功能 
     * @param jsonString String
     * @return Map
     */ 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map jsonStr2Map(String jsonString){ 
        JSONObject jsonObject = JSONObject.fromObject( jsonString ); 
        Iterator keyIter = jsonObject.keys(); 
        String key; 
        Object value; 
        Map valueMap = new HashMap(); 
        while( keyIter.hasNext()) { 
            key = (String)keyIter.next(); 
            value = jsonObject.get(key); 
            valueMap.put(key, value); 
        } 
        return valueMap; 
    } 

    /** 
     * 从json数组中得到相应java数组 
     * @param jsonString String 
     * @return Object[]
     */ 
    public static Object[] jsonStr2ObjectArray(String jsonString){ 
    	net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(jsonString); 
        return jsonArray.toArray(); 
        
    } 

    /** 
     * 从json对象集合表达式中得到一个java对象列表 
     * @param jsonString String
     * @param pojoClass Class<?>
     * @return List<?>
     */ 
    public static List<?> jsonStr2List(String jsonString, Class<?> pojoClass){ 
    	net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(jsonString); 
        JSONObject jsonObject; 
        Object pojoValue; 
        
        List<Object> list = new ArrayList<Object>(); 
        for ( int i = 0 ; i<jsonArray.size(); i++){ 
            
            jsonObject = jsonArray.getJSONObject(i); 
            pojoValue = JSONObject.toBean(jsonObject,pojoClass); 
            list.add(pojoValue); 
        } 
        return list; 
    } 
    
    
    /** 
     * 从json对象集合表达式中得到一个java对象列表 
     * @param jsonString String
     * @param pojoClass Class<?>
     * @param subClass   Map
     * @return List<?>
     */ 
    @SuppressWarnings("rawtypes")
	public static List<?> jsonStr2List(String jsonString, Class<?> pojoClass,Map subClass){ 
    	net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(jsonString); 
        JSONObject jsonObject; 
        Object pojoValue; 
        
        List<Object> list = new ArrayList<Object>(); 
        for ( int i = 0 ; i<jsonArray.size(); i++){ 
            
            jsonObject = jsonArray.getJSONObject(i); 
            pojoValue = JSONObject.toBean(jsonObject,pojoClass,subClass); 
            list.add(pojoValue); 
        } 
        return list; 
    } 

    /** 
     * 从json数组中解析出java字符串数组 
     * @param jsonString String
     * @return String[]
     */ 
    public static String[] getStringArray4Json(String jsonString){ 
    	net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(jsonString); 
        String[] stringArray = new String[jsonArray.size()]; 
        for( int i = 0 ; i<jsonArray.size() ; i++ ){ 
            stringArray[i] = jsonArray.getString(i); 
        } 
        return stringArray; 
    } 

    /** 
     * 从json数组中解析出javaLong型对象数组 
     * @param jsonString String
     * @return Long[]
     */ 
    public static Long[] getLongArray4Json(String jsonString){ 
    	net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(jsonString); 
        Long[] longArray = new Long[jsonArray.size()]; 
        for( int i = 0 ; i<jsonArray.size();i++ ){ 
            longArray[i] = jsonArray.getLong(i); 
        } 
        return longArray; 
    } 

    /** 
     * 从json数组中解析出java Integer型对象数组 
     * @param jsonString String
     * @return Integer[]
     */ 
    public static Integer[] getIntegerArray4Json(String jsonString){ 
    	net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(jsonString); 
        Integer[] integerArray = new Integer[jsonArray.size()]; 
        for( int i = 0 ; i<jsonArray.size() ; i++ ){ 
            integerArray[i] = jsonArray.getInt(i); 
        } 
        return integerArray; 
    } 
    
	/**
	 * 得到Code与Name的JsonArray相关对象
	 * @param codes int[]
	 * @param names String[]
	 * @param addCodes String[][]
	 * @return String
	 */
	public static String getComCodeJSONArray(int[] codes,String[] names,String[][]addCodes){
		if (codes.length==0 || codes.length!=names.length){
			throw new IllegalArgumentException("数组个数必须相等且大于1个");
		}
		StringBuffer sbf=new StringBuffer(codes.length*20+10);
		sbf.append("[");
		if (addCodes!=null && addCodes.length>0){
			for (int i=0;i<addCodes.length;i++){
				if(i>0) sbf.append(",");
				sbf.append("['").append(addCodes[i][0]).append("','").append(addCodes[i][1]).append("']");
			}
		}
		for (int i=0;i<codes.length;i++){
			if (i>0 || (addCodes!=null && addCodes.length>0)) sbf.append(",");
			sbf.append("['").append(codes[i]).append("','").append(names[i]).append("']");
		}
		sbf.append("]");
		return sbf.toString();		
	}
	
	/**
	 * 得到Code与Name的JsonArray相关对象
	 * @param codes long[]
	 * @param names String[]
	 * @param addCodes String[][]
	 * @return String
	 */
	public static String getComCodeJSONArray(long[] codes,String[] names,String[][] addCodes){
		if (codes.length==0 || codes.length!=names.length){
			throw new IllegalArgumentException("数组个数必须相等且大于1个");
		}
		StringBuffer sbf=new StringBuffer(codes.length*20+10);
		sbf.append("[");
		if (addCodes!=null && addCodes.length>0){
			for (int i=0;i<addCodes.length;i++){
				if(i>0) sbf.append(",");
				sbf.append("['").append(addCodes[i][0]).append("','").append(addCodes[i][1]).append("']");
			}
		}
		for (int i=0;i<codes.length;i++){
			if (i>0 || (addCodes!=null && addCodes.length>0)) sbf.append(",");
			sbf.append("['").append(codes[i]).append("','").append(names[i]).append("']");
		}
		sbf.append("]");
		return sbf.toString();		
	}
	
	/**
	 * map转化为bean
	 * @param map
	 * @param beanClass
	 * @return
	 * @throws Exception
	 */
	public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {    
        if (map == null)  
            return null;
        Object obj = beanClass.newInstance();
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);
        return obj;  
    } 
}
