package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface FileMapper {

	public boolean insertFile(Map<String, Object> paraMap);
	
	public boolean updateFile(Map<String, Object> paraMap);
	
	public boolean deleteCompletely(String[] ids);
	
	public boolean deleteFile(String[] ids);
	
	public int getFileCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getFilePage(Map<String, Object> paraMap);
	
	public Map<String, Object> getFileById(String id);
	
	public Map<String, Object> getFileByName(String name);
}
