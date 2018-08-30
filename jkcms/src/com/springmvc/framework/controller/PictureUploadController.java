package com.springmvc.framework.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.framework.domain.FtpServer;
import com.springmvc.framework.entity.ResultBean;
import com.springmvc.framework.thread.FtpUploadThread;
import com.springmvc.framework.util.ContinueFTP;
import com.springmvc.framework.util.PrintWriterUtil;
import com.springmvc.framework.util.StringUtils;
import com.springmvc.framework.util.UUIDUtil;
import com.springmvc.walker.util.ImageUtil;


@Controller
@RequestMapping("/picture") 
public class PictureUploadController{

	private final static Logger logger = Logger.getLogger(PictureUploadController.class);
	
	private String FILE_WEB_SERVER = GlobalConstant.SYS_MAP.get(GlobalConstant.FILE_WEB_SERVER);
	
	/**
	 * 上传文件
	 * @param request
	 * @param response
	 * @param mfile
	 */
	@RequestMapping(value = "/upload")
	public void uploadFile(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="uploadFile") MultipartFile mfile){
		ResultBean result = new ResultBean();
		result = uploadFile_process(mfile,result);
		PrintWriterUtil.write_text_html(response, result);
		
	}
	
	/**
	 * 上传文件-上传到ftp
	 * @param mfile
	 * @param result
	 * @return
	 */
	private ResultBean uploadFile_process(MultipartFile mfile,ResultBean result){
		boolean upload_result = false;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		ContinueFTP ftp = new ContinueFTP();
		boolean connected;
		try {
			connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
					Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
			if(connected){
				//写入ftp
				CommonsMultipartFile cf= (CommonsMultipartFile)mfile;
				String fileName = cf.getOriginalFilename();
				fileName = UUIDUtil.getUUID() + fileName.substring(fileName.lastIndexOf("."));
				String fileUrl = GlobalConstant.SYS_MAP.get("FTP_TARGET_ROOTPATH")+ "/" + StringUtils.getYMDpath() + "/";
				upload_result = ftp.uploadPicture(cf,fileUrl + fileName);

				Map<String, Object> data = new HashMap<String, Object>();
				data.put("fileUrl", fileUrl + fileName);
				data.put("viewUrl", FILE_WEB_SERVER + fileUrl.replace(GlobalConstant.SYS_MAP.get("FTP_TARGET_ROOTPATH"), "") + fileName);
				result.setData(data);
				ftp.disconnect();
			}
//			fileService.saveFile(paraMap);
			result.setSuccess(upload_result);
			if(!upload_result){
				result.setErr_msg("上传失败。");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setErr_msg("数据格式发生异常！");
		} catch (IOException e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setErr_msg("IO传输发生异常！");
		}
		return result;
	}

	/**
	 * 获取用户列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/uploadFile")
	public void upload(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="uploadFile") MultipartFile mfile) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", false);
		try {
			if (mfile == null) {
				logger.error("文件不存在!");
				json.put("success", false);
				json.put("errorMsg", "文件不存在!");
				return;
			}

			String resloution = "";
			Integer originalWidth = null;
			Integer originalHeight = null;
			try {
				BufferedImage bfImg = ImageIO.read(uploadFile);
				originalWidth = bfImg.getWidth();
				originalHeight = bfImg.getHeight();
				resloution = bfImg.getWidth() + "*" + bfImg.getHeight();
			} catch (Exception e) {
				logger.error("无效的图片文件!", e);
				json.put("success", false);
				json.put("errorMsg", "无效的图片文件!");
			}

			String fileUrl = getFileUrl(request.getParameter("fileUrl"));


			logger.info("加载海报服务器集群开始...");
			List<FtpServer> ftps = new ArrayList<FtpServer>();
			FtpServer ftp = new FtpServer(GlobalConstant.SYS_MAP.get("FTP_TARGET_IP"),
						Integer.valueOf(GlobalConstant.SYS_MAP.get("FTP_TARGET_PORT")),
						GlobalConstant.SYS_MAP.get("FTP_TARGET_USER"),
						GlobalConstant.SYS_MAP.get("FTP_TARGET_PASSWORD"));
			ftps.add(ftp);
			if (ftps != null && ftps.size() > 0) {
				logger.info("加载海报服务器集群完成,共" + ftps.size() +"台服务器...");
				logger.info("开始写入图片到服务器集群...");
				
				CountDownLatch latch = new CountDownLatch(ftps.size());
				for (int i = 0; i < ftps.size(); i++) {
					FtpServer server = ftps.get(i);
					new FtpUploadThread(server, fileUrl, uploadFile, latch).start();
				}
				latch.await();
				logger.info("写入图片到服务器集群完成!");

				try {
					// 删除本地文件
					uploadFile.delete();
				} catch (Exception e) {
				}
			}
			else {
				logger.error("没有有效的海报服务器,上传失败!");
				json.put("success", false);
				json.put("errorMsg", "没有有效的海报服务器!");
				return;
			}
			
			JSONObject data = new JSONObject();
			data.put("fileUrl", fileUrl);
			data.put("viewUrl", FILE_WEB_SERVER + fileUrl);
//			data.put("fileSize", fileSize);
			data.put("originalWidth", originalWidth);
			data.put("originalHeight", originalHeight);
			data.put("resloution", resloution);

			json.put("success", true);
			json.put("data", data);
		} catch (Exception e) {
			logger.error("图片上传发生错误!", e);
			json.put("errorMsg", e.getMessage());
		} finally {
			response.setHeader("Content-Type", "text/html;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 获取用户列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/exists")
	public void exists(HttpServletRequest request,HttpServletResponse response) throws Exception {
		PrintWriter writer = null;
		JSONObject json = new JSONObject();
		json.put("success", false);
		try {
			writer = response.getWriter();
			String url = request.getParameter("url");
			if (StringUtils.isEmptyOrBlank(url)) {
				json.put("errorMsg", "图片地址不能为空!");
				return;
			}
			json.put("exists", ImageUtil.exists(url));
			json.put("success", true);
		} catch (Exception e) {
			logger.error("判断图片是否存在异常!", e);
			json.put("errorMsg", e.getMessage());
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}

	/**
	 * 获取用户列表
	 * @param request
	 * @param response
	 */
	private String getFileUrl(String fileUrl) {
		if (!StringUtils.isEmptyOrBlank(fileUrl)) {
			if (!fileUrl.startsWith("/")) {
				fileUrl = "/" + fileUrl;
			}
		} else {
			fileUrl = getNewFtpPath() + getNewFileName();
		}
		return fileUrl;
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/");

	
	private String getNewFtpPath() {
		String ftpRoot = "/img";
		if (StringUtils.isEmptyOrBlank(ftpRoot)) {
			ftpRoot = "/";
		} else {
			ftpRoot = ftpRoot.trim();
			if (!ftpRoot.startsWith("/")) {
				ftpRoot = "/" + ftpRoot;
			}

			if (!ftpRoot.endsWith("/")) {
				ftpRoot += "/";
			}
		}
		return ftpRoot + sdf.format(new Date());
	}

	private String getNewFileName() {
		if (this.uploadFile != null) {
			String extName = uploadFileName.substring(uploadFileName.lastIndexOf("."));
			return UUIDUtil.getUUID() + extName;
		}
		return null;
	}

	private File uploadFile;

	public void setUploadFile(File uploadfile) {
		this.uploadFile = uploadfile;
	}

	private String uploadFileName;

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
}
