package com.springmvc.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springmvc.api.mapper.PageShowMapper;
import com.springmvc.api.service.PageShowService;
import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.framework.entity.Page;

@Service("pageShowService")
public class PageShowServiceImpl implements PageShowService{

	@Autowired
	private PageShowMapper pageShowMapper;
	
	@Override
	public List<Map<String, Object>> getCategoryByParentId(String parentId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = pageShowMapper.getCategoryByParentId(parentId);
		return list;
	}
	
	@Override
	public List<Map<String, Object>> getViewPictures() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = pageShowMapper.getPictureByType("01");
		for(Map<String, Object> picture :list){
			picture.put("pictureUrl", picture.get("pictureUrl").toString().replaceAll(GlobalConstant.SYS_MAP.get("IMPORT_PICTURE"), GlobalConstant.SYS_MAP.get(GlobalConstant.FILE_WEB_SERVER)));
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getPersons() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = pageShowMapper.getPerson();
		for(Map<String, Object> person : list){
			List<Map<String, Object>> pictures = new ArrayList<Map<String, Object>>();
			pictures = pageShowMapper.getPictureByParentId(person.get("personId").toString());
			for(Map<String, Object> picture :pictures){
				picture.put("pictureUrl", picture.get("pictureUrl").toString().replaceAll(GlobalConstant.SYS_MAP.get("IMPORT_PICTURE"), GlobalConstant.SYS_MAP.get(GlobalConstant.FILE_WEB_SERVER)));
			}
			person.put("picture", pictures);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getViewNewsByType(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		paraMap.put("start", paraMap.get("start") == "" ? "0" : paraMap.get("start"));
		paraMap.put("limit", paraMap.get("limit") == "" ? "20" : paraMap.get("limit"));
		
		int count = pageShowMapper.getInfoByTypeCount(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		
		list = pageShowMapper.getInfoByType(paraMap);
		return list;
	}

	@Override
	public Map<String, Object> getInfoById(String infoId) {
		Map<String, Object> info = new HashMap<String, Object>();
		info = pageShowMapper.getInfoByInfoId(infoId);
		return info;
	}

	@Override
	public Map<String, Object> getRecommendInfo() {
		Map<String, Object> info = new HashMap<String, Object>();
		info = pageShowMapper.getRecommendInfo();
		return info;
	}
}
