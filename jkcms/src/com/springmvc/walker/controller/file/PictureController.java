package com.springmvc.walker.controller.file;

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

import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.framework.entity.Page;
import com.springmvc.framework.entity.PageResultBean;
import com.springmvc.framework.entity.ResultBean;
import com.springmvc.framework.util.ContinueFTP;
import com.springmvc.framework.util.ParamUtil;
import com.springmvc.framework.util.PrintWriterUtil;
import com.springmvc.walker.service.PictureService;

@Controller
@RequestMapping("/picture") 
public class PictureController {
	
	private final static Logger logger = Logger.getLogger(PictureController.class);
	
	@Autowired
	private PictureService pictureService;
	
	/**
	 * 获取数据列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getPictureListPage")
	public void getPictureListPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = {"start","limit","name"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = pictureService.getPicturePage(paraMap, page);
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
	@RequestMapping(value = "/getPictureById")
	public void getPictureById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = pictureService.getPictureById(request.getParameter("id"));
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
	@RequestMapping(value = "/savePicture")
	public void savePicture(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] fileds = {"PICTURE_ID","TYPE","FILE_URL","RESLOUTION","PARENT_ID","STATUS"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			boolean saveResult = pictureService.savePicture(paraMap);
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
	@RequestMapping(value = "/deletePicture")
	public void deletePicture(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] id = request.getParameter("ids").split(",");
			for(String del_id: id){
				pictureService.deletePicture(del_id);
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
	@RequestMapping(value = "/uploadPicture")
	public void uploadPicture(HttpServletRequest request,HttpServletResponse response,
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
