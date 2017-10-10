package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface FileMapper {

	void insertFile(Map<String, Object> paraMap);
	
	void updateFile(Map<String, Object> paraMap);
	
	void deleteCompletely(String[] ids);
	
	void deleteFile(String[] ids);
	
	int getFileCount(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getFilePage(Map<String, Object> paraMap);
	
	Map<String, Object> getFileById(String id);
	
	Map<String, Object> getFileByName(String name);
}
