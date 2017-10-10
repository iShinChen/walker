package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface InfoMapper {

	void insertInfo(Map<String, Object> paraMap);
	
	void updateInfo(Map<String, Object> paraMap);
	
	void deleteInfo(String[] ids);
	
	int getInfoCount(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getInfoPage(Map<String, Object> paraMap);
	
	Map<String, Object> getInfoById(String id);
}
