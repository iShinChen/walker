package com.springmvc.walker.service;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.Page;

public interface PhotoService {
	
	public List<Map<String, Object>> getPhotoPage(Map<String, Object> paraMap, Page page);

	public boolean savePhoto(Map<String, Object> paraMap);
	
	public boolean deletePhoto(String ids);
	
	public Map<String, Object> getPhotoById(String id);
}
