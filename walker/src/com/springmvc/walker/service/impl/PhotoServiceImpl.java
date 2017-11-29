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
import com.springmvc.walker.mapper.customer.PhotoMapper;
import com.springmvc.walker.service.PhotoService;

@Service("photoService")
public class PhotoServiceImpl implements PhotoService{
	
	private final static Logger logger = Logger.getLogger(PhotoServiceImpl.class);
	
	@Resource
	private PhotoMapper photoMapper;

	@Override
	public List<Map<String, Object>> getPhotoPage(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = photoMapper.getPhotoCount(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		
		logger.info("分页查询信息集合START");
		list = photoMapper.getPhotoPage(paraMap);
		logger.info("分页查询信息集合SUCCESS,共"+list.size()+"条记录");
		return list;
	}

	@Override
	public boolean savePhoto(Map<String, Object> paraMap) {
		if(null == paraMap) return false;
		if(null != paraMap.get("id") && !"".equals(paraMap.get("id"))){
			logger.info("更新数据"+paraMap.toString());
			return photoMapper.updatePhoto(paraMap);
		}else{
			paraMap.put("id", UUIDUtil.getUUID());
			logger.info("插入数据"+paraMap.toString());
			return photoMapper.insertPhoto(paraMap);
		}
	}

	@Override
	public boolean deletePhoto(String id) {
		logger.info("逻辑删除信息数据");
		return photoMapper.deletePhoto(id);	
	}

	@Override
	public Map<String, Object> getPhotoById(String id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("查询单条信息id:" + id);
		resultMap = photoMapper.getPhotoById(id);
		logger.info("查询单条信息成功:" + resultMap.toString());
		return resultMap;
	}

}
