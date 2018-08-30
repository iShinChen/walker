package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface CategoryMapper {

	public List<Map<String, Object>> getCategory(Map<String, Object> paraMap);
	
	public boolean updateGategoryById(Map<String, Object> paraMap);
	
	public boolean insertCategory(Map<String, Object> paraMap);
	
	public boolean deleteGategoryById(String CATEGORY_ID);
	
	public boolean deleteGategoryRef(String CATEGORY_ID);
	
	public boolean moveToAbove(Map<String, Object> paraMap);
	
	public boolean moveToBelow(Map<String, Object> paraMap);
	
	public boolean moveToOtherAbove(Map<String, Object> paraMap);
	
	public boolean moveToOtherBelow(Map<String, Object> paraMap);
	
	public boolean moveToOtherParent(Map<String, Object> paraMap);
	
	public Map<String, Object> getCategoryByCategoryId(String categoryId);
	
	public List<Map<String, Object>> getCategoryByParentId(String parentId);
}
