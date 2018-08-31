package com.springmvc.api.service;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.Page;

/**
 * 页面展示API-Service
 * @author shin chen
 *
 */
public interface PageShowService {

	/**
	 * 根据父栏目ID获取子栏目列表
	 * @param parentId
	 * @return
	 */
	public List<Map<String, Object>> getCategoryByParentId(String parentId);
	
	/**
	 * 获取主轮播页图片列表
	 * @return
	 */
	public List<Map<String, Object>> getViewPictures();
	
	/**
	 * 获取副轮播页人员列表
	 * @return
	 */
	public List<Map<String, Object>> getPersons();
	
	/**
	 * 根据资讯类型分页查询资讯
	 * @param paraMap
	 * @param page
	 * @return
	 */
	public List<Map<String, Object>> getViewNewsByType(Map<String, Object> paraMap, Page page);
	
	/**
	 * 根据资讯ID获取某资讯详情
	 * @param infoId
	 * @return
	 */
	public Map<String, Object> getInfoById(String infoId);
	
	/**
	 * 获取推荐位详情
	 * @return
	 */
	public Map<String, Object> getRecommendInfo();
	
}
