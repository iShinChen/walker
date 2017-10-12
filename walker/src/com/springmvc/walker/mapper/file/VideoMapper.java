package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface VideoMapper {

	public boolean insertVideo(Map<String, Object> paraMap);
	
	public boolean updateVideo(Map<String, Object> paraMap);
	
	public boolean deleteVideo(String id);
	
	public boolean updVideoFlag(Map<String, Object> paraMap);
	
	public boolean updVideoStatus(Map<String, Object> paraMap);
	
	public int getVideoCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getVideoPage(Map<String, Object> paraMap);
	
	public Map<String, Object> getVideoById(String id);
	
	public Map<String, Object> getVideoByUrl(String name);
	
	public List<Map<String, Object>> getVideoListByIds(String[] ids);
}
