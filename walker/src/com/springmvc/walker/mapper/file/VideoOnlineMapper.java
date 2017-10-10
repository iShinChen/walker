package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface VideoOnlineMapper {

	public void insertVideoOnline(Map<String, Object> paraMap);
	
	public void updateVideoOnline(Map<String, Object> paraMap);
	
	public void deleteVideoOnline(String[] ids);
	
	public void updVideoOnlineStatus(Map<String, Object> paraMap);
	
	public int getVideoOnlineCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getVideoOnlinePage(Map<String, Object> paraMap);
	
	public Map<String, Object> getVideoOnlineById(String id);
	
}
