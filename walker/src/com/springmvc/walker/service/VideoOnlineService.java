package com.springmvc.walker.service;

import java.util.List;
import java.util.Map;

import com.springmvc.walker.entity.Page;

public interface VideoOnlineService {

	public List<Map<String, Object>> getVideoOnlinePage(Map<String, Object> paraMap, Page page);

	public boolean saveVideoOnline(Map<String, Object> paraMap);
	
	public boolean deleteVideoOnline(String ids);
	
	public Map<String, Object> getVideoOnlineById(String id);
	
	public boolean updVideoOnlineStatusById(String id,String status);
	
}
