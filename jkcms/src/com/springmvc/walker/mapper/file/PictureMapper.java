package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface PictureMapper {

	public boolean insertPicture(Map<String, Object> paraMap);
	
	public boolean updatePicture(Map<String, Object> paraMap);
	
	public boolean deletePicture(String id);
	
	public boolean deletePictureByParentId(String parentId);
	
	public int getPictureCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getPicturePage(Map<String, Object> paraMap);
	
	public Map<String, Object> getPictureById(String id);
	
	public List<Map<String, Object>> getPictureByParentId(String parentId);
	
	public List<Map<String, Object>> getPictureByType(String type);
}
