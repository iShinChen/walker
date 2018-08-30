package com.springmvc.walker.service;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.Page;

public interface InfoService {

	public List<Map<String, Object>> getInfoPage(Map<String, Object> paraMap, Page page);

	public boolean saveInfo(Map<String, Object> paraMap);
	
	public boolean deleteInfo(String ids);
	
	public Map<String, Object> getInfoById(String id);
	
	public boolean setRecommend(String id);
}
