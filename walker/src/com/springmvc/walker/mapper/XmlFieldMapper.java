package com.springmvc.walker.mapper;

import java.util.List;
import java.util.Map;

public interface XmlFieldMapper {

	public int listCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> listPage(Map<String, Object> paraMap);
	
	public Map<String, Object> selectById(String id);
	
	public boolean insert(Map<String, Object> paraMap);
	
	public boolean update(Map<String, Object> paraMap);
	
	public boolean deleteById(String id);
	
	public boolean deleteByParentId(String parentId);
	
	public int checkCode(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> child(Map<String, Object> paraMap);
	
}
