package com.springmvc.walker.service;

import java.util.List;
import java.util.Map;

import com.springmvc.walker.entity.Page;

public interface InfoService {

	public List<Map<String, Object>> getInfoPage(Map<String, Object> paraMap, Page page);

	public void saveInfo(Map<String, Object> paraMap);
	
	public void deleteInfo(String ids);
	
	public Map<String, Object> getInfoById(String id);
}
