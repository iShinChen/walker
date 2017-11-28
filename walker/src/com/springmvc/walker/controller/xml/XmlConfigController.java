package com.springmvc.walker.controller.xml;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springmvc.walker.constant.GlobalConstant;
import com.springmvc.walker.entity.PageResultBean;
import com.springmvc.walker.entity.ResultBean;
import com.springmvc.walker.service.xml.XmlConfigService;
import com.springmvc.walker.util.ParamUtil;
import com.springmvc.walker.util.PrintWriterUtil;

@Controller
@RequestMapping("/xmlConfig") 
public class XmlConfigController {

	private final static Logger logger = Logger.getLogger(XmlConfigController.class);
	
	@Autowired
	private XmlConfigService xmlConfigService;
	
	/**
	 * 查询公共代码列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/configList")
	public void configList(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			List<Map<String, Object>> list = xmlConfigService.getConfigList();
			result.setPageResultBean(1, list.size(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/saveXmlConfig")
	public void saveXmlConfig(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		String[] fileds = { "ID", "CODE", "XML_TYPE", "STATUS"};
		Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
		try {
			if(xmlConfigService.updConfig(paraMap)){
				GlobalConstant.initXmlElements();
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("保存未成功。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 根据ID获取单条数据具体信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getConfigById")
	public void getConfigById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = xmlConfigService.getConfigById(request.getParameter("ID"));
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
