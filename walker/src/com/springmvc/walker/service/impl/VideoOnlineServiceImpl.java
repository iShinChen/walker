package com.springmvc.walker.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.walker.entity.Page;
import com.springmvc.walker.mapper.file.VideoOnlineMapper;
import com.springmvc.walker.service.VideoOnlineService;
import com.springmvc.walker.util.UUIDUtil;

@Service("videoOnlineService")
public class VideoOnlineServiceImpl implements VideoOnlineService{
	
	private final static Logger logger = Logger.getLogger(VideoServiceImpl.class);
	
	@Resource
	private VideoOnlineMapper videoOnlineMapper;

	@Override
	public List<Map<String, Object>> getVideoOnlinePage(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			int count = videoOnlineMapper.getVideoOnlineCount(paraMap);
			page.setTotalRow(count);
			page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
			page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
			
			paraMap.put("start", page.getCurrPage());
			paraMap.put("limit", page.getPageRow());
			
			logger.info("分页查询视频信息START");
			list = videoOnlineMapper.getVideoOnlinePage(paraMap);
			logger.info("分页查询视频信息SUCCESS,共"+list.size()+"条记录");
		} catch (Exception e) {
			logger.error("查询信息集合发生异常", e);
		}
		return list;
	}

	@Override
	public void saveVideoOnline(Map<String, Object> paraMap) {
		if(null == paraMap){
			return;
		}
		try {
			if(null != paraMap.get("id") && !"".equals(paraMap.get("id"))){
				logger.info("更新视频数据"+paraMap.toString());
				videoOnlineMapper.updateVideoOnline(paraMap);
				logger.info("更新视频数据成功");
			}else{
				paraMap.put("id", UUIDUtil.getUUID());
				logger.info("插入视频数据"+paraMap.toString());
				videoOnlineMapper.insertVideoOnline(paraMap);
				logger.info("插入视频数据成功");
			}
			
		} catch (Exception e) {
			logger.error("保存发生异常", e);
		}
	}

	@Override
	public void deleteVideoOnline(String ids) {
		try {
			logger.info("物理删除视频数据");
			String[] videoId = ids.split(",");
			logger.info("删除数据:id="+ids);
			videoOnlineMapper.deleteVideoOnline(videoId);
			logger.info("删除数据成功");
		} catch (Exception e) {
			logger.error("删除发生异常", e);
		}
	}

	@Override
	public Map<String, Object> getVideoOnlineById(String id) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			logger.info("查询单条视频id:" + id);
			result = videoOnlineMapper.getVideoOnlineById(id);
			logger.info("查询单条视频成功:" + result.toString());
		} catch (Exception e) {
			logger.error("查询单条视频发生异常", e);
			result = null;
		}
		return result;
	}

	@Override
	public void updVideoOnlineStatusById(String id, String status) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("id", id);
		paraMap.put("status", status);
		try {
			videoOnlineMapper.updVideoOnlineStatus(paraMap);
			logger.info("更新视频上传状态："+status);
		} catch (Exception e) {
			logger.error("更新视频上传状态发生异常", e);
		}
	}

}
