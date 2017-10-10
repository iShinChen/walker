package com.springmvc.walker.controller.customer;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.walker.constant.GlobalConstant;
import com.springmvc.walker.entity.Page;
import com.springmvc.walker.service.PhotoService;
import com.springmvc.walker.util.ContinueFTP;
import com.springmvc.walker.util.ParamUtil;

@Controller
@RequestMapping("/photo") 
public class PhotoController {

	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(PhotoController.class);
	
	@Autowired
	private PhotoService photoService;
	
	/**
	 * 获取信息列表
	 */
	@RequestMapping(value = "/getPhotoListPage")
	public void getPhotoListPage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		String[] fileds = {"start","limit","name"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = photoService.getPhotoPage(paraMap, page);
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
	
	@RequestMapping(value = "/getPhotoById")
	public void getPhotoById(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String id = request.getParameter("id");
		try {
			writer = response.getWriter();
			Map<String, Object> resultMap = photoService.getPhotoById(id);
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
	
	@RequestMapping(value = "/savePhoto")
	public void savePhoto(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String[] fileds = {"id","version","name","age","sex","birth","address"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			int result = photoService.savePhoto(paraMap);
			if(result > 0){
				jsonObj.put("success", true);
			}	
		} catch (Exception e) {
			logger.error("程序异常", e);
			jsonObj.put("success", false);
		} finally {
			writer.write(jsonObj.toString());
			writer.flush();
			writer.close();
		}
	}
	
	@RequestMapping(value = "/deletePhoto")
	public void deletePhoto(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String ids = request.getParameter("ids");
		try {
			writer = response.getWriter();
			String[] id = ids.split(",");
			for(String del_id: id){
				photoService.deletePhoto(del_id);
			}
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
	
	@RequestMapping(value = "/uploadPhoto")
	public void uploadPhoto(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="upload") MultipartFile mfile){
		
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		boolean result = false;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		ContinueFTP ftp = new ContinueFTP();
		boolean connected;
		try {
			response.setContentType("text/html;charset=utf-8");
			writer = response.getWriter();
			connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
					Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
			if(connected){
				//写入ftp
				CommonsMultipartFile cf= (CommonsMultipartFile)mfile;
				String fileName = cf.getOriginalFilename();
				result = ftp.uploadFile(cf,fileName);
				ftp.disconnect();
			}
			jsonObj.put("success", result);
			jsonObj.put("name", "图片");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.write(jsonObj.toString());
		writer.flush();
		writer.close();
		
	}
}
