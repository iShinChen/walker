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
		try {
			int count = videoMapper.getVideoCount(paraMap);
			page.setTotalRow(count);
			page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
			page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
			
			paraMap.put("start", page.getCurrPage());
			paraMap.put("limit", page.getPageRow());
			
			logger.info("分页查询视频信息START");
			list = videoMapper.getVideoPage(paraMap);
			logger.info("分页查询视频信息SUCCESS,共"+list.size()+"条记录");
		} catch (Exception e) {
			logger.error("查询信息集合发生异常", e);
		}
		return list;
	}

	@Override
	public void saveVideo(Map<String, Object> paraMap) {
		if(null == paraMap){
			return;
		}
		try {
			if(null != paraMap.get("id") && !"".equals(paraMap.get("id"))){
				logger.info("更新视频数据"+paraMap.toString());
				videoMapper.updateVideo(paraMap);
				logger.info("更新视频数据成功");
			}else{
				paraMap.put("id", UUIDUtil.getUUID());
				logger.info("插入视频数据"+paraMap.toString());
				videoMapper.insertVideo(paraMap);
				logger.info("插入视频数据成功");
			}
			
		} catch (Exception e) {
			logger.error("保存发生异常", e);
		}
	}

	@Override
	public void deleteVideo(String ids) {
		try {
			logger.info("物理删除视频数据");
			String[] videoId = ids.split(",");
			for(String id: videoId){
				logger.info("删除数据:id="+id);
				videoMapper.deleteVideo(id);
			}
			logger.info("删除数据成功");
		} catch (Exception e) {
			logger.error("删除发生异常", e);
		}
	}

	@Override
	public Map<String, Object> getVideoById(String id) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			logger.info("查询单条视频id:" + id);
			result = videoMapper.getVideoById(id);
			logger.info("查询单条视频成功:" + result.toString());
		} catch (Exception e) {
			logger.error("查询单条视频发生异常", e);
			result = null;
		}
		return result;
	}

	@Override
	public Map<String, Object> getVideoByUrl(String url) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			logger.info("查询同名视频:" + url);
			result = videoMapper.getVideoByUrl(url);
			logger.info("查询同名视频成功:" + result.toString());
		} catch (Exception e) {
			logger.error("查询同名视频发生异常", e);
			result = null;
		}
		return result;
	}

	@Override
	public void updVideoFlagById(String id,String flag) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("id", id);
		paraMap.put("flag", flag);
		try {
			videoMapper.updVideoFlag(paraMap);
			logger.info("更新视频下载状态："+flag);
		} catch (Exception e) {
			logger.error("更新视频下载状态发生异常", e);
		}
	}

	@Override
	public List<Map<String, Object>> getVideoListByIds(String ids) {
		String[] id = ids.split(",");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = videoMapper.getVideoListByIds(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void updVideoStatusById(String id, String status) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("id", id);
		paraMap.put("status", status);
		try {
			videoMapper.updVideoStatus(paraMap);
			logger.info("更新视频上传状态："+status);
		} catch (Exception e) {
			logger.error("更新视频上传状态发生异常", e);
		}
	}

}
