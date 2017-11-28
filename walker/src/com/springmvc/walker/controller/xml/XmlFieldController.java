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
import com.springmvc.walker.entity.Page;
import com.springmvc.walker.entity.PageResultBean;
import com.springmvc.walker.entity.ResultBean;
import com.springmvc.walker.service.xml.XmlFieldService;
import com.springmvc.walker.util.ParamUtil;
import com.springmvc.walker.util.PrintWriterUtil;

@Controller
@RequestMapping("/xmlField") 
public class XmlFieldController {

	private final static Logger logger = Logger.getLogger(XmlFieldController.class);
	
	@Autowired
	private XmlFieldService xmlFieldService;
	
	/**
	 * 查询公共代码列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/listPage")
	public void listPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit", "name", "parentId", "code" };
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = xmlFieldService.listPage(paraMap, page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 保存信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/saveXmlField")
	public void saveXmlField(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		String[] fileds = { "ID", "VALUE", "CODE", "PARENT_ID", "ORDERINDEX",
				"NAME", "STATUS", "DESCRIPTION" };
		Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
		try {
			if(xmlFieldService.save(paraMap)){
				GlobalConstant.initXmlBook();
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
	 * 删除信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteXmlField")
	public void deleteXmlField(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			logger.info("ID" + request.getParameter("ID"));
			if(xmlFieldService.delete(request.getParameter("ID"))){
				GlobalConstant.initXmlBook();
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
	
	/**
	 * 根据ID获取单条数据具体信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getXmlFieldById")
	public void getXmlFieldById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = xmlFieldService.selectById(request.getParameter("ID"));
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
