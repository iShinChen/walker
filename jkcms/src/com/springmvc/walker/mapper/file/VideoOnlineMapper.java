package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface VideoOnlineMapper {

	public boolean insertVideoOnline(Map<String, Object> paraMap);
	
	public boolean updateVideoOnline(Map<String, Object> paraMap);
	
	public boolean deleteVideoOnline(String[] ids);
	
	public boolean updVideoOnlineStatus(Map<String, Object> paraMap);
	
	public int getVideoOnlineCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getVideoOnlinePage(Map<String, Object> paraMap);
	
	public Map<String, Object> getVideoOnlineById(String id);
	
}
