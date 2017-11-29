package com.springmvc.walker.controller.xml;

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
import com.springmvc.walker.service.xml.XmlConfigService;
import com.springmvc.walker.thread.xml.BuildSeriesXmlThread;

@Controller
@RequestMapping("/series") 
public class SeriesController {
	
	private final static Logger logger = Logger.getLogger(SeriesController.class);
	
	@Autowired
	private SeriesService seriesService;
	@Autowired
	private XmlConfigService xmlConfigService;
	
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
}
