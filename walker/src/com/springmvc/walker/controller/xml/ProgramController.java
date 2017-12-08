package com.springmvc.walker.controller.xml;

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
import com.springmvc.walker.service.xml.CollectService;
import com.springmvc.walker.service.xml.ProgramService;
import com.springmvc.walker.thread.xml.ParseXmlThread;
import com.springmvc.walker.xml.entity.ProgramEntity;

@Controller
@RequestMapping("/program")
public class ProgramController {

	private final static Logger logger = Logger.getLogger(ProgramController.class);
	
	@Autowired
	private ProgramService programService;
	@Autowired
	private CollectService collectService;
	/**
	 * 查询媒资分集列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getProgramPage")
	public void getProgramPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit", "NAME", "STATUS", "CDN_TIME", "SERIES_NAME", "SERIES_ID" ,"UNLINE_TIME"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			Page page = new Page();
			List<Map<String, Object>> list = programService.getProgramPage(paraMap, page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("查询媒资分集列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 根据ID获取分集信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getProgramById")
	public void getProgramById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String programId = request.getParameter("programId");
			ProgramEntity program = programService.getProgramMapById(programId);
			result.setData(ParamUtil.convertBeanToMap(program));	
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("查询媒资分集列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 保存分集信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/updProgram")
	public void updProgram(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] fileds = { "PROGRAM_ID", "NAME", "SERIALNO", "DURATION",
					"AWARDS", "DESCRIPTION", "KEYWORDS", "DIRECTOR", "ADAPTOR",
					"ACTOR", "UPDATE_TIME" };
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			ProgramEntity program = (ProgramEntity)ParamUtil.convertMapToBean(ProgramEntity.class, paraMap);
			boolean upd = programService.updateProgram(program);
			if(upd){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("保存分集信息失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存分集信息发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 分集上线
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/onlineProgram")
	public void onlineProgram(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] programIds = request.getParameter("programIds").split(",");
			int successCount = 0;
			for (String programId : programIds) {
				if(programService.updateProgramStatus(programId, "01")) {
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
			result.setErr_msg("分集上线发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 分集下线
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/offlineProgram")
	public void offlineProgram(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] programIds = request.getParameter("programIds").split(",");
			int successCount = 0;
			for (String programId : programIds) {
				if(programService.updateProgramStatus(programId, "02")) {
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
			result.setErr_msg("分集下线发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}

	/**
	 * 媒资拆条
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/splitProgram")
	public void splitProgram(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
//			String programId = request.getParameter("programId");
//			String resultCode = mediaService.splitProgram(programId);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("媒资拆条发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 分集删除
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteProgram")
	public void deleteProgram(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			if(programService.deleteProgram(request.getParameter("programId"))) {
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("分集删除失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("分集删除发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 工单解析
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/downloadXml")
	public void downloadXml(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] fileUrl ={"D://工作区/files/series.xml","D://工作区/files/program.xml","D://工作区/files/movie.xml"};
			for(String url :fileUrl){
				ParseXmlThread t = new ParseXmlThread(collectService,url);
				t.start();
			}
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("工单解析发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}

}
