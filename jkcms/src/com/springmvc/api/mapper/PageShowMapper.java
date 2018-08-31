package com.springmvc.api.mapper;

import java.util.List;
import java.util.Map;

/**
* 页面展示API-Mapper
* @author shin chen
* 
*/
public interface PageShowMapper {
	
	public List<Map<String, Object>> getCategoryByParentId(String parentId);
	
	public List<Map<String, Object>> getPictureByParentId(String parentId);
	
	public List<Map<String, Object>> getPictureByType(String type);
	
	public List<Map<String, Object>> getPerson();
	
	public int getInfoByTypeCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getInfoByType(Map<String, Object> paraMap);
	
	public Map<String, Object> getInfoByInfoId(String infoId);
	
	public Map<String, Object> getRecommendInfo();
}
