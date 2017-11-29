package com.springmvc.walker.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.framework.entity.Page;
import com.springmvc.framework.util.UUIDUtil;
import com.springmvc.walker.mapper.file.VideoOnlineMapper;
import com.springmvc.walker.service.VideoOnlineService;

@Service("videoOnlineService")
public class VideoOnlineServiceImpl implements VideoOnlineService{
	
	private final static Logger logger = Logger.getLogger(VideoServiceImpl.class);
	
	@Resource
	private VideoOnlineMapper videoOnlineMapper;

	@Override
	public List<Map<String, Object>> getVideoOnlinePage(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = videoOnlineMapper.getVideoOnlineCount(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		
		logger.info("分页查询视频信息START");
		list = videoOnlineMapper.getVideoOnlinePage(paraMap);
		logger.info("分页查询视频信息SUCCESS,共"+list.size()+"条记录");
		return list;
	}

	@Override
	public boolean saveVideoOnline(Map<String, Object> paraMap) {
		if(null == paraMap) return false;
		if(null != paraMap.get("id") && !"".equals(paraMap.get("id"))){
			logger.info("更新视频数据"+paraMap.toString());
			return videoOnlineMapper.updateVideoOnline(paraMap);
		}else{
			paraMap.put("id", UUIDUtil.getUUID());
			logger.info("插入视频数据"+paraMap.toString());
			return videoOnlineMapper.insertVideoOnline(paraMap);
		}
	}

	@Override
	public boolean deleteVideoOnline(String ids) {
		logger.info("物理删除视频数据");
		String[] videoId = ids.split(",");
		logger.info("删除数据:id="+ids);
		return videoOnlineMapper.deleteVideoOnline(videoId);
	}

	@Override
	public Map<String, Object> getVideoOnlineById(String id) {
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("查询单条视频id:" + id);
		result = videoOnlineMapper.getVideoOnlineById(id);
		logger.info("查询单条视频成功:" + result.toString());
		return result;
	}

	@Override
	public boolean updVideoOnlineStatusById(String id, String status) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("id", id);
		paraMap.put("status", status);
		logger.info("更新视频："+id+"的上传状态为："+status);
		return videoOnlineMapper.updVideoOnlineStatus(paraMap);
	}

}
