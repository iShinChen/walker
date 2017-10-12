package com.springmvc.walker.controller.file;

import java.io.IOException;
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

import com.springmvc.walker.constant.DownloadStatus;
import com.springmvc.walker.constant.GlobalConstant;
import com.springmvc.walker.entity.Page;
import com.springmvc.walker.entity.PageResultBean;
import com.springmvc.walker.entity.ResultBean;
import com.springmvc.walker.service.FileService;
import com.springmvc.walker.util.ContinueFTP;
import com.springmvc.walker.util.ParamUtil;
import com.springmvc.walker.util.PrintWriterUtil;
import com.springmvc.walker.util.Tools;

@Controller
@RequestMapping("/files") 
public class FileController {
	
	private final static Logger logger = Logger.getLogger(FileController.class);
	
	@Autowired
	private FileService fileService;
	
	/**
	 * 获取信息列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getFileListPage")
	public void getFileListPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit","name"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = fileService.getFilePage(paraMap, page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 根据ID获取单条数据具体信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getFileById")
	public void getFileById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = fileService.getFileById(request.getParameter("id"));
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
	 * 保存信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/saveFile")
	public void saveFile(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		String[] fileds = {"id","name","url"};
		Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
		result = save_reName(paraMap,result);
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 保存信息-文件改名
	 * @param paraMap
	 * @param result
	 * @return
	 */
	private ResultBean save_reName(Map<String, Object> paraMap,ResultBean result){
		try {
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
					result.setSuccess(true);
				}
				ftp.disconnect();
			}
		}catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存发生错误。");
		}
		return result;
	}
	
	/**
	 * 上传文件
	 * @param request
	 * @param response
	 * @param mfile
	 */
	@RequestMapping(value = "/uploadFile")
	public void uploadFile(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="filePath") MultipartFile mfile){
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
				upload_result = ftp.uploadFile(cf,fileName);
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
	 * 文件下载
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/downLoadFile")
	public void downLoadFile(HttpServletRequest request,HttpServletResponse response){
		String filePath = Tools.getFolderPath();
		//如果未输入路径，则直接返回
		if(null == filePath) return;
		ResultBean result = new ResultBean();
		String fileUrl = request.getParameter("fileUrl");
		result = downLoadFile_process(filePath,fileUrl,result);
		PrintWriterUtil.write_text_html(response, result);
	}
	
	/**
	 * 文件下载-下载到本地
	 * @param filePath
	 * @param fileUrl
	 * @param result
	 * @return
	 */
	private ResultBean downLoadFile_process(String filePath,String fileUrl,ResultBean result){
		boolean connected = false;
		ContinueFTP ftp = new ContinueFTP();	
		try {
			connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
					Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
			if(connected){
				//读取ftp
				DownloadStatus downloadStatus = ftp.downLoadFile(filePath, fileUrl);
				if(DownloadStatus.Download_New_Success.equals(downloadStatus)){
					result.setSuccess(true);
				}
				ftp.disconnect();
			}	
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setErr_msg("下载任务创建失败！");
		}
		return result;
	}
	
	/**
	 * 删除信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteFile")
	public void deleteFile(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			fileService.deleteFile(request.getParameter("ids"));
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("删除发生错误。");
		}
		PrintWriterUtil.write(response, result);
	}
}
