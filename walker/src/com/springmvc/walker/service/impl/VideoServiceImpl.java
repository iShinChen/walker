package com.springmvc.walker.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.walker.entity.Page;
import com.springmvc.walker.mapper.file.VideoMapper;
import com.springmvc.walker.service.VideoService;
import com.springmvc.walker.util.UUIDUtil;

@Service("videoService")
public class VideoServiceImpl implements VideoService{
	
	private final static Logger logger = Logger.getLogger(VideoServiceImpl.class);
	
	@Resource
	private VideoMapper videoMapper;

	@Override
	public List<Map<String, Object>> getVideoPage(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = videoMapper.getVideoCount(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		
		logger.info("分页查询视频信息START");
		list = videoMapper.getVideoPage(paraMap);
		logger.info("分页查询视频信息SUCCESS,共"+list.size()+"条记录");
		return list;
	}

	@Override
	public boolean saveVideo(Map<String, Object> paraMap) {
		if(null == paraMap) return false;
		if(null != paraMap.get("id") && !"".equals(paraMap.get("id"))){
			logger.info("更新视频数据"+paraMap.toString());
			return videoMapper.updateVideo(paraMap);
		}else{
			paraMap.put("id", UUIDUtil.getUUID());
			logger.info("插入视频数据"+paraMap.toString());
			return videoMapper.insertVideo(paraMap);
		}
	}

	@Override
	public boolean deleteVideo(String ids) {
		logger.info("物理删除视频数据");
		String[] videoId = ids.split(",");
		for(String id: videoId){
			logger.info("删除数据:id="+id);
			videoMapper.deleteVideo(id);
		}
		logger.info("删除数据成功");
		return true;
	}

	@Override
	public Map<String, Object> getVideoById(String id) {
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("查询单条视频id:" + id);
		result = videoMapper.getVideoById(id);
		logger.info("查询单条视频成功:" + result.toString());
		return result;
	}

	@Override
	public Map<String, Object> getVideoByUrl(String url) {
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("查询同名视频:" + url);
		result = videoMapper.getVideoByUrl(url);
		logger.info("查询同名视频成功:" + result.toString());
		return result;
	}

	@Override
	public boolean updVideoFlagById(String id,String flag) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("id", id);
		paraMap.put("flag", flag);
		logger.info("更新ID："+id+"的flag为："+flag);
		return videoMapper.updVideoFlag(paraMap);

	}

	@Override
	public List<Map<String, Object>> getVideoListByIds(String ids) {
		String[] id = ids.split(",");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = videoMapper.getVideoListByIds(id);
		return list;
	}

	@Override
	public boolean updVideoStatusById(String id, String status) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("id", id);
		paraMap.put("status", status);
		logger.info("更新ID："+id+"的status为："+status);
		return videoMapper.updVideoStatus(paraMap);
	}

}
