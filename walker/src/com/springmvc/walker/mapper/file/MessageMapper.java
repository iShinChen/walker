package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface MessageMapper {

	public boolean insertMessage(Map<String, Object> paraMap);
	
	public boolean updateMessage(Map<String, Object> paraMap);
	
	public boolean deleteCompletely(String[] ids);
	
	public boolean deleteMessage(String[] ids);
	
	public int getMessageCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getMessagePage(Map<String, Object> paraMap);
	
	public Map<String, Object> getMessageById(String id);
	
	public List<Map<String, Object>> getMessageByIds(String[] ids);
}
