package com.springmvc.walker.controller.file;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springmvc.walker.entity.Page;
import com.springmvc.walker.entity.PageResultBean;
import com.springmvc.walker.entity.ResultBean;
import com.springmvc.walker.service.InfoService;
import com.springmvc.walker.util.ParamUtil;
import com.springmvc.walker.util.PrintWriterUtil;

@Controller
@RequestMapping("/info") 
public class InfoController {
	
	private final static Logger logger = Logger.getLogger(InfoController.class);
	
	@Autowired
	private InfoService infoService;
	
	/**
	 * 获取信息列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getInfoListPage")
	public void getInfoListPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();	
		try {
			String[] fileds = { "start", "limit","name"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = infoService.getInfoPage(paraMap, page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常",e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 根据ID获取信息属性
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getInfoById")
	public void getInfoById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = infoService.getInfoById(request.getParameter("id"));
			result.setData(resultMap);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常",e);
			result.setSuccess(false);
			result.setErr_msg("获取数据异常。");
		} 
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 保存信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/saveInfo")
	public void saveInfo(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] fileds = {"id","title","summary","content","type","status"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			boolean saveResult = infoService.saveInfo(paraMap);
			if(saveResult){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("保存不成功。");
			}	
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存过程发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 删除信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteInfo")
	public void deleteInfo(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			boolean delResult = infoService.deleteInfo(request.getParameter("ids"));
			if(delResult){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("删除不成功。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("删除过程发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
}
