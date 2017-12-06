package com.springmvc.walker.controller.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springmvc.framework.entity.Page;
import com.springmvc.framework.entity.PageResultBean;
import com.springmvc.framework.entity.ResultBean;
import com.springmvc.framework.util.ParamUtil;
import com.springmvc.framework.util.PrintWriterUtil;
import com.springmvc.walker.service.xml.SeriesService;
import com.springmvc.walker.thread.xml.BuildSeriesXmlThread;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.ProgramEntity;
import com.springmvc.walker.xml.entity.SeriesEntity;

@Controller
@RequestMapping("/series") 
public class SeriesController {
	
	private final static Logger logger = Logger.getLogger(SeriesController.class);
	
	@Autowired
	private SeriesService seriesService;

	/**
	 * 查询合集信息列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/listPage")
	public void listPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit", "SERIES_ID", "NAME", "TYPE","STATUS","CDN_TIME"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = seriesService.getPageList(paraMap, page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 生成合集工单
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/buildSeriesXml")
	public void buildSeriesXml(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			String[] id = request.getParameter("ids").split(",");
			for(String seriesId: id){
				Thread thread = new BuildSeriesXmlThread(seriesId,seriesService);
				thread.start();
			}
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("生成工单启动异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 生成合集工单
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/checkSeries")
	public void checkSeries(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		String seriesId = request.getParameter("seriesId");
		try {
			seriesService.checkSeries(seriesId);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常" + e.getMessage());
			result.setSuccess(false);
			result.setErr_msg("缺集检查发生异常。");
		} 
		PrintWriterUtil.write(response, result);
	} 
	
	/**
	 * 根据ID查询合集信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getSeriesById")
	public void getSeriesById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String seriesId = request.getParameter("seriesId");
			SeriesEntity series = seriesService.getSeriesById(seriesId);
			result.setData(ParamUtil.convertBeanToMap(series));
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("根据ID查询合集信息发生异常。", e);
			result.setSuccess(false);
			result.setErr_msg("根据ID查询合集信息发生异常。");	
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 保存合集信息
	 * @param request
	 * @param response
	 */
	 
	@RequestMapping(value = "/updateSeries")
	public void updateSeries(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		String[] fileds = { "NAME", "EN_NAME", "ALIAS_NAME", "COPYRIGHT", "COPYRIGHT_DATE", 
				"EPISODE_TYPE", "TYPE", "TYPE_NAME", "SIMPLESPELL", "FULLSPELL", 
				"AREA", "RELEASE_TIME", "LANGUAGE", "CAPTION", 
				"DIRECTOR", "ACTOR", "ADAPTOR", "PRODUCER", 
				"PROGRAM_COUNT", "NEW_PROGRAM", 
				"KEYWORDS", "AWARDS", "DESCRIPTION", "SERIES_ID" };
		Map<String, Object> paraMap = ParamUtil.getParamMap(request,fileds);
		try {
			boolean upd = seriesService.updateSeries((SeriesEntity)ParamUtil.convertMapToBean(SeriesEntity.class, paraMap));
			if(upd){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("保存合集信息失败。");	
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存合集信息发生异常。");	
		} 
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 根据合集ID查询分集信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getProgramPageBySeriesId")
	public void getProgramPageBySeriesId(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit", "SERIES_ID" };
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			Page page = new Page();
			List<ProgramEntity> programs = seriesService.getProgramPageBySeriesId(paraMap, page);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for(ProgramEntity  program: programs){
				list.add(ParamUtil.convertBeanToMap(program));
			}
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("操作发生异常", e);
			result.setSuccess(false);
			result.setErr_msg("根据合集ID查询分集信息发生异常。");	
		}
		PrintWriterUtil.write(response, result);
	}

	/**
	 * 根据合集ID查询海报列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getPicturesBySeriesId")
	public void getPicturesBySeriesId(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String seriesId = request.getParameter("seriesId");
			List<PictureEntity> pictures = seriesService.getPicturesBySeriesId(seriesId);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for(PictureEntity picture : pictures){
				list.add(ParamUtil.convertBeanToMap(picture));
			}
			result.setPageResultBean(list.size(), 1, list, true);
		} catch (Exception e) {
			logger.error("操作发生异常", e);
			result.setSuccess(false);
			result.setErr_msg("根据合集ID查询海报列表发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	
	
	/**
	 * 合集上线
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/onlineSeries")
	public void onlineSeries(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] seriesIds = request.getParameter("seriesIds").split(",");
			int successCount = 0;
			for (String seriesId : seriesIds) {
				if(seriesService.updateStatusById(seriesId, "01")) {
					successCount++;
				};
			}
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("successCount", successCount);
			result.setSuccess(true);
			result.setData(resultMap);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("合集上线发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 合集下线
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/offlineSeries")
	public void offlineSeries(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] seriesIds = request.getParameter("seriesIds").split(",");
			int successCount = 0;
			for (String seriesId : seriesIds) {
				if(seriesService.updateStatusById(seriesId, "02")) {
					successCount++;
				};
			}
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("successCount", successCount);
			result.setSuccess(true);
			result.setData(resultMap);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("合集下线发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 合集删除
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteSeries")
	public void deleteSeries(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			if(seriesService.deleteSeries(request.getParameter("seriesId"))) {
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("合集删除失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("合集删除发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
}
