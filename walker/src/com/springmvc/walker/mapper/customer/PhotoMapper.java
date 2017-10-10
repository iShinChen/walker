package com.springmvc.walker.mapper.customer;

import java.util.List;
import java.util.Map;

public interface PhotoMapper {

	public int insertPhoto(Map<String, Object> paraMap);
	
	public int updatePhoto(Map<String, Object> paraMap);
	
	public int deletePhoto(String id);
	
	public int getPhotoCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getPhotoPage(Map<String, Object> paraMap);
	
	public Map<String, Object> getPhotoById(String id);
}
