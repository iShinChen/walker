package com.springmvc.walker.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.walker.entity.Page;
import com.springmvc.walker.mapper.file.InfoMapper;
import com.springmvc.walker.service.InfoService;
import com.springmvc.walker.util.UUIDUtil;

@Service("infoService")
public class InfoServiceImpl implements InfoService{

	private final static Logger logger = Logger.getLogger(InfoServiceImpl.class);
	
	@Resource
	private InfoMapper infoMapper;
	
	@Override
	public List<Map<String, Object>> getInfoPage(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			int count = infoMapper.getInfoCount(paraMap);
			page.setTotalRow(count);
			page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
			page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
			
			paraMap.put("start", page.getCurrPage());
			paraMap.put("limit", page.getPageRow());
			
			logger.info("分页查询咨询信息START");
			list = infoMapper.getInfoPage(paraMap);
			logger.info("分页查询咨询信息SUCCESS,共"+list.size()+"条记录");
		} catch (Exception e) {
			logger.error("查询咨询发生异常", e);
		}
		return list;
	}

	@Override
	public void saveInfo(Map<String, Object> paraMap) {
		if(null == paraMap){
			return;
		}
		try {
			if(null != paraMap.get("id") && !"".equals(paraMap.get("id"))){
				logger.info("更新咨询数据"+paraMap.toString());
				infoMapper.updateInfo(paraMap);
				logger.info("更新咨询数据成功");
			}else{
				paraMap.put("id", UUIDUtil.getUUID());
				logger.info("插入咨询数据"+paraMap.toString());
				infoMapper.insertInfo(paraMap);
				logger.info("插入咨询数据成功");
			}
			
		} catch (Exception e) {
			logger.error("保存发生异常", e);
		}
	}

	@Override
	public void deleteInfo(String ids) {
		try {
			if(null != ids && !"".equals(ids)){
				String[] id = ids.split(",");
				infoMapper.deleteInfo(id);
				logger.info("删除数据成功");
			}
		} catch (Exception e) {
			logger.error("操作发生异常", e);
		}
	}

	@Override
	public Map<String, Object> getInfoById(String id) {
		Map<String, Object> file = new HashMap<String, Object>();
		try {
			logger.info("查询单条咨询id:" + id);
			file = infoMapper.getInfoById(id);
			logger.info("查询单条咨询成功:" + file.toString());
		} catch (Exception e) {
			logger.error("查询单条咨询发生异常", e);
			file = null;
		}
		return file;
	}

}
