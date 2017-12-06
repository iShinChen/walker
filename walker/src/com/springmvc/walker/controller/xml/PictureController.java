package com.springmvc.walker.controller.xml;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.framework.entity.ResultBean;
import com.springmvc.framework.util.ParamUtil;
import com.springmvc.framework.util.PrintWriterUtil;
import com.springmvc.framework.util.StringUtils;
import com.springmvc.framework.util.UUIDUtil;
import com.springmvc.walker.service.xml.PictureService;
import com.springmvc.walker.xml.entity.PictureEntity;

@Controller
@RequestMapping("/picture") 
public class PictureController {

	private final static Logger logger = Logger.getLogger(PictureController.class);
	
	@Autowired
	private PictureService pictureService;
	
	/**
	 * 更新海报内容
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/savePicture")
	public void savePicture(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		String[] fileds = { "SERIES_ID", "TYPE", "FILE_URL", "RESLOUTION","FILE_SIZE" ,"PICTURE_ID"};
		try {
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			PictureEntity picture = (PictureEntity)ParamUtil.convertMapToBean(PictureEntity.class, paraMap);
			boolean save = false;
			if (StringUtils.isEmptyOrBlank(paraMap.get("PICTURE_ID"))) {
				// 海报不存在
				picture.setPICTURE_ID(UUIDUtil.getUUID());
				picture.setSTATUS("00");
				save = pictureService.insertPicture(picture);
			} else {
				save = pictureService.updatePicture(picture);
			}
			if(save){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("更新海报内容失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("更新海报内容发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 删除海报内容
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deletePicture")
	public void deletePicture(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String pictureId = request.getParameter("pictureId");
			boolean del = false;
			if (StringUtils.isNotEmpty(pictureId)) {
				del = pictureService.deletePicture(pictureId);
			}
			if(del){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("删除海报内容失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("更新海报内容发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 上线海报内容
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/onlinePicture")
	public void onlinePicture(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String pictureId = request.getParameter("pictureId");
			boolean online = false;
			if (StringUtils.isNotEmpty(pictureId)) {
				online = pictureService.updatePictureStatus(pictureId, "01");
			}
			if(online){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("上线海报失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("上线海报内容发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 下线海报内容
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/offlinePicture")
	public void offlinePicture(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String pictureId = request.getParameter("pictureId");
			boolean offline = false;
			if (StringUtils.isNotEmpty(pictureId)) {
				offline = pictureService.updatePictureStatus(pictureId, "00");
			}
			if(offline){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("下线海报失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("下线海报发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 检查海报缺失情况
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/checkPictureBySeriesId")
	public void checkPictureBySeriesId(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String seriesId = request.getParameter("seriesId");
			if (!StringUtils.isEmptyOrBlank(seriesId)) {
				boolean isOk = pictureService.checkPictureBySeriesId(seriesId);
				result.setSuccess(false);
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("isLacked", !isOk);
			}
			else {
				result.setSuccess(false);
				result.setErr_msg("检查海报缺失情况失败。");
			}
		}catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("检查海报缺失情况发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
}
