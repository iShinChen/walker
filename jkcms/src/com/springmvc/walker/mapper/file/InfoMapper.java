package com.springmvc.walker.mapper.file;

import java.util.List;
import java.util.Map;

public interface InfoMapper {

	public boolean insertInfo(Map<String, Object> paraMap);
	
	public boolean updateInfo(Map<String, Object> paraMap);
	
	public boolean deleteInfo(String[] ids);
	
	public int getInfoCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getInfoPage(Map<String, Object> paraMap);
	
	public Map<String, Object> getInfoById(String id);
	
	public List<Map<String, Object>> getInfoByType(Map<String, Object> paraMap);
	
	public int getInfoByTypeCount(Map<String, Object> paraMap);
	
	public Map<String, Object> getInfoByInfoId(String infoId);
	
	public Map<String, Object> getRecommendInfo();
	
	public boolean removeRecommend();
	
	public boolean setRecommend(String id);
	
}
