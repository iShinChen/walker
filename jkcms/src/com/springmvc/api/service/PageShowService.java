package com.springmvc.api.service;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.Page;

public interface PageShowService {

	public List<Map<String, Object>> getCategoryByParentId(String parentId);
	
	public List<Map<String, Object>> getViewPictures();
	
	public List<Map<String, Object>> getPersons();
	
	public List<Map<String, Object>> getViewNewsByType(Map<String, Object> paraMap, Page page);
	
	public Map<String, Object> getInfoById(String infoId);
	
	public Map<String, Object> getRecommendInfo();
	
}
