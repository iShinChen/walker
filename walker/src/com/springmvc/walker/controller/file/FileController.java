package com.springmvc.walker.controller.file;

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
import com.springmvc.walker.constant.DownloadStatus;
import com.springmvc.walker.constant.GlobalConstant;
import com.springmvc.walker.entity.Page;
import com.springmvc.walker.service.FileService;
import com.springmvc.walker.util.ContinueFTP;
import com.springmvc.walker.util.ParamUtil;
import com.springmvc.walker.util.Tools;

@Controller
@RequestMapping("/files") 
public class FileController {

	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(FileController.class);
	
	@Autowired
	private FileService fileService;
	/**
	 * 获取信息列表
	 */
	@RequestMapping(value = "/getFileListPage")
	public void getFileListPage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		String[] fileds = { "start", "limit","name"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = fileService.getFilePage(paraMap, page);
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
	
	@RequestMapping(value = "/getFileById")
	public void getFileById(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String id = request.getParameter("id");
		try {
			writer = response.getWriter();
			Map<String, Object> resultMap = fileService.getFileById(id);
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
	@RequestMapping(value = "/saveFile")
	public void saveFile(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String[] fileds = {"id","name","url"};
		jsonObj.put("success", false);
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			//连接ftp
			ContinueFTP ftp = new ContinueFTP();
			boolean connected = false;
			connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
					Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
			if(connected){
				//如果改名成功,则更新数据
				if(ftp.reNameFile(paraMap.get("url").toString(), paraMap.get("name").toString())){
					paraMap.put("url", paraMap.get("url").toString().substring(0, paraMap.get("url").toString().lastIndexOf("/")+1) + paraMap.get("name").toString());
					fileService.saveFile(paraMap);
					jsonObj.put("success", true);
				}
				ftp.disconnect();
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
		} finally {
			writer.write(jsonObj.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 上传文件
	 */
	@RequestMapping(value = "/uploadFile")
	public void uploadFile(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="filePath") MultipartFile mfile){
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
				paraMap = fileService.getFileByName(fileName);
				//查询是否有同名文件，若有，则为覆盖更新；反之上传插入
				if(null == paraMap){
					paraMap = new HashMap<String, Object>();
					paraMap.put("name",fileName);
					paraMap.put("type", fileName.substring(fileName.lastIndexOf(".")+1));
				}
				paraMap.put("file_size", String.valueOf(cf.getSize()));
				paraMap.put("url", GlobalConstant.SYS_MAP.get(GlobalConstant.IMPORT_FILE)+fileName);
				ftp.disconnect();
			}
			fileService.saveFile(paraMap);
			jsonObj.put("success", result);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.write(jsonObj.toString());
		writer.flush();
		writer.close();
		
	}
	
	/**
	 * 文件下载
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/downLoadFile")
	public void downLoadFile(HttpServletRequest request,HttpServletResponse response){
		String filePath = Tools.getFolderPath();
		//如果未输入路径，则直接返回
		if(null == filePath) return;
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("success", false);
		String fileUrl = request.getParameter("fileUrl");
		boolean connected = false;
		ContinueFTP ftp = new ContinueFTP();	
		try {
			response.setContentType("text/html;charset=utf-8");
			writer = response.getWriter();
			connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
					Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
			if(connected){
				//读取ftp
				DownloadStatus result = ftp.downLoadFile(filePath, fileUrl);
				if(DownloadStatus.Download_New_Success.equals(result)){
					jsonObj.put("success", true);
				}
				ftp.disconnect();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		writer.write(jsonObj.toString());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 删除信息
	 */
	@RequestMapping(value = "/deleteFile")
	public void deleteFile(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String ids = request.getParameter("ids");
		try {
			writer = response.getWriter();
			fileService.deleteFile(ids);
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
