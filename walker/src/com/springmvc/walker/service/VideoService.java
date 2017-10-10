package com.springmvc.walker.service;

import java.util.List;
import java.util.Map;

import com.springmvc.walker.entity.Page;

public interface VideoService {

	public List<Map<String, Object>> getVideoPage(Map<String, Object> paraMap, Page page);
	
	public List<Map<String, Object>> getVideoListByIds(String ids);

	public void saveVideo(Map<String, Object> paraMap);
	
	public void deleteVideo(String ids);
	
	public Map<String, Object> getVideoById(String id);
	
	public Map<String, Object> getVideoByUrl(String url);
	
	public void updVideoFlagById(String id,String flag);
	
	public void updVideoStatusById(String id,String status);
}
