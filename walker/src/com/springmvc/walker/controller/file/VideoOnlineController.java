package com.springmvc.walker.controller.file;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.walker.entity.Page;
import com.springmvc.walker.service.VideoOnlineService;
import com.springmvc.walker.util.ParamUtil;

@Controller
@RequestMapping("/videoOnline") 
public class VideoOnlineController {

	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(VideoOnlineController.class);
	
	@Autowired
	private VideoOnlineService videoOnlineService;
	
	/**
	 * 获取信息列表
	 */
	@RequestMapping(value = "/getVideoOnlineListPage")
	public void getVideoOnlineListPage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		String[] fileds = { "start", "limit","name"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = videoOnlineService.getVideoOnlinePage(paraMap, page);
			jsonMap.put("total",page.getTotalRow());
			jsonMap.put("rows",list);
			jsonMap.put("page",page.getPageRow());
			jsonMap.put("success", true);
		} catch (Exception e) {
			logger.error(e);
			jsonMap.put("success", false);
		}
		
		writer.write(JSONObject.toJSONString(jsonMap));
		writer.flush();
		writer.close();
	}
	
	@RequestMapping(value = "/getVideoOnlineById")
	public void getVideoOnlineById(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String id = request.getParameter("id");
		try {
			writer = response.getWriter();
			Map<String, Object> resultMap = videoOnlineService.getVideoOnlineById(id);
			jsonObj.put("data", resultMap);
			jsonObj.put("success", true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			jsonObj.put("error", e);
		} finally {
			writer.write(jsonObj.toString());
			writer.flush();
			writer.close();
		}	
	}
	
	/**
	 * 保存信息
	 */
	@RequestMapping(value = "/saveVideoOnline")
	public void saveVideoOnline(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String[] fileds = {"id","name","tags","video_url","flash_url","embed_url","status"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			videoOnlineService.saveVideoOnline(paraMap);
			jsonObj.put("success", true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			jsonObj.put("success", false);
		} finally {
			writer.write(jsonObj.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 删除信息
	 */
	@RequestMapping(value = "/deleteVideoOnline")
	public void deleteVideoOnline(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String ids = request.getParameter("ids");
		try {
			writer = response.getWriter();
			videoOnlineService.deleteVideoOnline(ids);
			jsonObj.put("success", true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			jsonObj.put("error", e);
		} finally {
			writer.write(jsonObj.toString());
			writer.flush();
			writer.close();
		}	
	}
}
