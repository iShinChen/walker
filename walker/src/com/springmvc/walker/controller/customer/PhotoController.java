package com.springmvc.walker.controller.customer;

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

import com.springmvc.walker.constant.GlobalConstant;
import com.springmvc.walker.entity.Page;
import com.springmvc.walker.entity.PageResultBean;
import com.springmvc.walker.entity.ResultBean;
import com.springmvc.walker.service.PhotoService;
import com.springmvc.walker.util.ContinueFTP;
import com.springmvc.walker.util.ParamUtil;
import com.springmvc.walker.util.PrintWriterUtil;

@Controller
@RequestMapping("/photo") 
public class PhotoController {
	
	private final static Logger logger = Logger.getLogger(PhotoController.class);
	
	@Autowired
	private PhotoService photoService;
	
	/**
	 * 获取数据列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getPhotoListPage")
	public void getPhotoListPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = {"start","limit","name"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = photoService.getPhotoPage(paraMap, page);
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
	@RequestMapping(value = "/getPhotoById")
	public void getPhotoById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = photoService.getPhotoById(request.getParameter("id"));
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
	@RequestMapping(value = "/savePhoto")
	public void savePhoto(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] fileds = {"id","version","name","age","sex","birth","address"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			boolean saveResult = photoService.savePhoto(paraMap);
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
	@RequestMapping(value = "/deletePhoto")
	public void deletePhoto(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] id = request.getParameter("ids").split(",");
			for(String del_id: id){
				photoService.deletePhoto(del_id);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("删除发生错误。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 上传图片
	 * @param request
	 * @param response
	 * @param mfile
	 */
	@RequestMapping(value = "/uploadPhoto")
	public void uploadPhoto(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="upload") MultipartFile mfile){
		ResultBean result = new ResultBean();
		boolean upload_result = false;
		Map<String, Object> data = new HashMap<String, Object>();
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
				ftp.disconnect();
			}
			data.put("name", "图片");
			result.setSuccess(upload_result);
			result.setData(data);
			if(!upload_result){
				result.setErr_msg("图片上传失败。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setErr_msg("图片上传发生异常。");
		}
		PrintWriterUtil.write_text_html(response, result);
	}
}
