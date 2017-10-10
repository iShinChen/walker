package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface MessageMapper {

	int insertMessage(Map<String, Object> paraMap);
	
	int updateMessage(Map<String, Object> paraMap);
	
	void deleteCompletely(String[] ids);
	
	void deleteMessage(String[] ids);
	
	int getMessageCount(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getMessagePage(Map<String, Object> paraMap);
	
	Map<String, Object> getMessageById(String id);
	
	List<Map<String, Object>> getMessageByIds(String[] ids);
}
