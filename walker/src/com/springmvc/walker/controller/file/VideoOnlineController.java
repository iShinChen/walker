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
import com.springmvc.walker.service.VideoOnlineService;
import com.springmvc.walker.util.ParamUtil;
import com.springmvc.walker.util.PrintWriterUtil;

@Controller
@RequestMapping("/videoOnline") 
public class VideoOnlineController {
	
	private final static Logger logger = Logger.getLogger(VideoOnlineController.class);
	
	@Autowired
	private VideoOnlineService videoOnlineService;
	
	/**
	 * 获取数据列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getVideoOnlineListPage")
	public void getVideoOnlineListPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit","name"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = videoOnlineService.getVideoOnlinePage(paraMap, page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 根据ID获取数据详情
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getVideoOnlineById")
	public void getVideoOnlineById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = videoOnlineService.getVideoOnlineById(request.getParameter("id"));
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
	 * 保存数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/saveVideoOnline")
	public void saveVideoOnline(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] fileds = {"id","name","tags","video_url","flash_url","embed_url","status"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			boolean saveResult = videoOnlineService.saveVideoOnline(paraMap);
			if(saveResult){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("保存不成功。");
			}
		}catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存发生错误。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 删除数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteVideoOnline")
	public void deleteVideoOnline(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			boolean delResult = videoOnlineService.deleteVideoOnline(request.getParameter("ids"));
			if(delResult){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("删除不成功。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("删除发生错误。");
		}
		PrintWriterUtil.write(response, result);
	}
}
