package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface VideoMapper {

	void insertVideo(Map<String, Object> paraMap);
	
	void updateVideo(Map<String, Object> paraMap);
	
	void deleteVideo(String id);
	
	void updVideoFlag(Map<String, Object> paraMap);
	
	void updVideoStatus(Map<String, Object> paraMap);
	
	int getVideoCount(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getVideoPage(Map<String, Object> paraMap);
	
	Map<String, Object> getVideoById(String id);
	
	Map<String, Object> getVideoByUrl(String name);
	
	List<Map<String, Object>> getVideoListByIds(String[] ids);
}
