package com.springmvc.walker.service;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.Page;

public interface PictureService {
	
	public List<Map<String, Object>> getPicturePage(Map<String, Object> paraMap, Page page);

	public boolean savePicture(Map<String, Object> paraMap);
	
	public boolean deletePicture(String ids);
	
	public Map<String, Object> getPictureById(String id);
}
