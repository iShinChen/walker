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
import com.springmvc.walker.service.InfoService;
import com.springmvc.walker.util.ParamUtil;

@Controller
@RequestMapping("/info") 
public class InfoController {
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(InfoController.class);
	
	@Autowired
	private InfoService infoService;
	
	/**
	 * 获取信息列表
	 */
	@RequestMapping(value = "/getInfoListPage")
	public void getInfoListPage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		String[] fileds = { "start", "limit","name"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = infoService.getInfoPage(paraMap, page);
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
	
	@RequestMapping(value = "/getInfoById")
	public void getInfoById(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String id = request.getParameter("id");
		try {
			writer = response.getWriter();
			Map<String, Object> resultMap = infoService.getInfoById(id);
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
	@RequestMapping(value = "/saveInfo")
	public void saveInfo(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String[] fileds = {"id","title","summary","content","type","status"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			infoService.saveInfo(paraMap);
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
	@RequestMapping(value = "/deleteInfo")
	public void deleteInfo(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String ids = request.getParameter("ids");
		try {
			writer = response.getWriter();
			infoService.deleteInfo(ids);
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
