package com.springmvc.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.api.service.PageShowService;
import com.springmvc.framework.entity.Page;
import com.springmvc.framework.entity.PageResultBean;
import com.springmvc.framework.entity.ResultBean;
import com.springmvc.framework.util.ParamUtil;
import com.springmvc.framework.util.PrintWriterUtil;
import com.springmvc.walker.service.CategoryService;
import com.springmvc.walker.service.MessageService;

/**
 * 页面展示API
 * @author shin chen
 *
 */
@Controller
@RequestMapping("/view") 
public class PageShowController {
	private final static Logger logger = Logger.getLogger(PageShowController.class);
	
	@Autowired
	private PageShowService pageShowService;
	
	/**
	 * 获取导航菜单
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getMenuList")
	public void getMenuList(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();		
		try {
			List<Map<String, Object>> firstList = new ArrayList<Map<String, Object>>();
			firstList = pageShowService.getCategoryByParentId("3dbd16bacbfe49439b143b94e7a96cd6");
			for(Map<String, Object> firstMap : firstList){
				List<Map<String, Object>> secondList = new ArrayList<Map<String, Object>>();
				secondList = pageShowService.getCategoryByParentId(firstMap.get("menuId").toString());
				firstMap.put("childMenu", secondList);
			}
			result.setPageResultBean(firstList.size(), firstList.size(), firstList, true);
		} catch (Exception e) {
			logger.error(e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取轮播组件图片
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getViewPictures")
	public void getViewPictures(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();		
		try {
			List<Map<String, Object>> pictures = new ArrayList<Map<String, Object>>();
			pictures = pageShowService.getViewPictures();
			result.setPageResultBean(pictures.size(), pictures.size(), pictures, true);
		} catch (Exception e) {
			logger.error(e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取轮播简介内容
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getViewInfo")
	public void getViewInfo(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();		
		try {
			List<Map<String, Object>> persons = new ArrayList<Map<String, Object>>();
			persons = pageShowService.getPersons();
			result.setPageResultBean(persons.size(), persons.size(), persons, true);
		} catch (Exception e) {
			logger.error(e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取资讯列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getViewNews")
	public void getViewNews(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();		
		try {
			String[] fileds = { "start", "limit","type"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> news = new ArrayList<Map<String, Object>>();
			news = pageShowService.getViewNewsByType(paraMap, page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), news, true);
		} catch (Exception e) {
			logger.error(e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取资讯列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getViewNewsByNewsId")
	public void getViewNewsByNewsId(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = pageShowService.getInfoById(request.getParameter("messageId"));
			result.setData(resultMap);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取资讯列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getRecommendInfo")
	public void getRecommendInfo(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = pageShowService.getRecommendInfo();
			result.setData(resultMap);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据异常。");
		}
		PrintWriterUtil.write(response, result);
	}
}
