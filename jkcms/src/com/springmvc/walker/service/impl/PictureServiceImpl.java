package com.springmvc.walker.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.framework.entity.Page;
import com.springmvc.framework.util.UUIDUtil;
import com.springmvc.walker.mapper.file.PictureMapper;
import com.springmvc.walker.service.PictureService;

@Service("pictureService")
public class PictureServiceImpl implements PictureService{
	
	private final static Logger logger = Logger.getLogger(PictureServiceImpl.class);
	
	@Resource
	private PictureMapper pictureMapper;

	@Override
	public List<Map<String, Object>> getPicturePage(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = pictureMapper.getPictureCount(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		
		logger.info("分页查询信息集合START");
		list = pictureMapper.getPicturePage(paraMap);
		for(Map<String, Object> pic: list){
			pic.put("fileUrl", pic.get("fileUrl").toString().replaceAll(GlobalConstant.SYS_MAP.get("IMPORT_PICTURE"), GlobalConstant.SYS_MAP.get(GlobalConstant.FILE_WEB_SERVER)));
		}
		logger.info("分页查询信息集合SUCCESS,共"+list.size()+"条记录");
		return list;
	}

	@Override
	public boolean savePicture(Map<String, Object> paraMap) {
		if(null == paraMap) return false;
		if(null != paraMap.get("PICTURE_ID") && !"".equals(paraMap.get("PICTURE_ID"))){
			logger.info("更新数据"+paraMap.toString());
			return pictureMapper.updatePicture(paraMap);
		}else{
			paraMap.put("id", UUIDUtil.getUUID());
			logger.info("插入数据"+paraMap.toString());
			return pictureMapper.insertPicture(paraMap);
		}
	}

	@Override
	public boolean deletePicture(String id) {
		logger.info("逻辑删除信息数据");
		return pictureMapper.deletePicture(id);	
	}

	@Override
	public Map<String, Object> getPictureById(String id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("查询单条信息id:" + id);
		resultMap = pictureMapper.getPictureById(id);
		logger.info("查询单条信息成功:" + resultMap.toString());
		return resultMap;
	}

}
