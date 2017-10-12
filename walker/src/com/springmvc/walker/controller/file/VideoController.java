package com.springmvc.walker.controller.file;

import java.io.File;
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
import com.springmvc.walker.constant.MappingConstant;
import com.springmvc.walker.entity.Page;
import com.springmvc.walker.entity.PageResultBean;
import com.springmvc.walker.entity.ResultBean;
import com.springmvc.walker.service.VideoService;
import com.springmvc.walker.thread.DownloadVideo2LocalThread;
import com.springmvc.walker.thread.FixedThreadExecutor;
import com.springmvc.walker.thread.UploadVideo2FtpThread;
import com.springmvc.walker.util.ParamUtil;
import com.springmvc.walker.util.PrintWriterUtil;
import com.springmvc.walker.util.StringUtils;
import com.springmvc.walker.util.Tools;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

@Controller
@RequestMapping("/video") 
public class VideoController {
	
	private final static Logger logger = Logger.getLogger(VideoController.class);
	
	@Autowired
	private VideoService videoService;
	
	/**
	 * 获取数据列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getVideoListPage")
	public void getVideoListPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit","name"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = videoService.getVideoPage(paraMap, page);
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
	@RequestMapping(value = "/getVideoById")
	public void getVideoById(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> resultMap = videoService.getVideoById(request.getParameter("id"));
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
	@RequestMapping(value = "/saveVideo")
	public void saveVideo(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String[] fileds = {"id","name","video_size","duration","url","flag"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			boolean saveResult = videoService.saveVideo(paraMap);
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
	@RequestMapping(value = "/deleteVideo")
	public void deleteVideo(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			boolean delResult = videoService.deleteVideo(request.getParameter("ids"));
			if(delResult){
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
	 * 上传视频
	 * @param request
	 * @param response
	 * @param mfile
	 */
	@RequestMapping(value = "/uploadVideo")
	public void uploadVideo(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="filePath") MultipartFile mfile){
		ResultBean result = new ResultBean();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		String name = request.getParameter("name");
		try {
			paraMap.put("name", name);
			paraMap.put("video_size", mfile.getSize()/(1024*1024));
			paraMap.put("url", GlobalConstant.SYS_MAP.get(GlobalConstant.IMPORT_VIDEO)
						+StringUtils.getYearMonth()+"/"+mfile.getOriginalFilename());
			videoService.saveVideo(paraMap);
			Map<String, Object> video = videoService.getVideoByUrl(paraMap.get("url").toString());
			if(null !=video){
				CommonsMultipartFile cf= (CommonsMultipartFile)mfile;
				String uploadPath = request.getSession().getServletContext().getRealPath("/upload/");
				File url = new File(uploadPath);
		  		if (!url.exists()) {
		  			url.mkdirs();
				}
		        File file = new File(uploadPath+"/"+cf.getOriginalFilename());
		        cf.getFileItem().write(file);
		        //获取时长
		        Encoder encoder = new Encoder();
		        MultimediaInfo mInfo = encoder.getInfo(file);
		        video.put("duration", StringUtils.formatDuration(mInfo.getDuration()));
		        videoService.saveVideo(video);
				Thread t = new UploadVideo2FtpThread(video ,videoService,uploadPath+"/"+cf.getOriginalFilename());
				videoService.updVideoStatusById(video.get("id").toString(), "01");
				MappingConstant.UPLOADING_THREAD.put(video.get("id").toString(), t);
				FixedThreadExecutor.execute(t);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setErr_msg("上传线程创建发生异常。");
		}
		PrintWriterUtil.write_text_html(response, result);
	}
	
	/**
	 * 下载视频
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/downloadVideo")
	public void downloadVideo(HttpServletRequest request,HttpServletResponse response){
		String localPath = Tools.getFolderPath();
		//如果未输入路径，则直接返回
		ResultBean result = new ResultBean();
		String ids = request.getParameter("ids");
		try {
			List<Map<String, Object>> list = videoService.getVideoListByIds(ids);
			for(Map<String, Object> video : list){
				File dir = new File(localPath);
				if(!dir.exists() && !dir.isDirectory()) dir.mkdirs();
				Thread t = new DownloadVideo2LocalThread(video, videoService,localPath);
				videoService.updVideoFlagById(video.get("id").toString(), "05");
				MappingConstant.DOWNLOADING_THREAD.put(video.get("id").toString(), t);
				FixedThreadExecutor.execute(t);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setErr_msg("下载线程创建发生异常。");
		}
		PrintWriterUtil.write_text_html(response, result);
	}
	
	/**
	 * 下载进度监听
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/findDownloadProgress")
	public void findDownloadProgress(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		try{
			writer=response.getWriter();
			String videoId = request.getParameter("videoId");
			Object downloadprogress = MappingConstant.DOWNLOADING_PROGRESS.get(videoId);
			Map<String, Object> resultMap = videoService.getVideoById(videoId);
			int count = 0;
			if (downloadprogress != null) {
				logger.info("videoId:"+videoId+"的下载进度为："+downloadprogress);
				count = Integer.valueOf(String.valueOf(downloadprogress));
			}
			if(resultMap != null && "01".equals(resultMap.get("flag")))
			{
				Thread thread = MappingConstant.DOWNLOADING_THREAD.get(videoId);
				if (thread == null) {
					MappingConstant.DOWNLOADING_PROGRESS.remove(videoId);
					videoService.updVideoFlagById(videoId, "02");
					json.put("downloading", false);
				} else {
					json.put("downloading", true);
				}
			}else{
				json.put("downloading", false);
				MappingConstant.DOWNLOADING_PROGRESS.remove(videoId);
			}
			if(count == 100)
			{
				videoService.updVideoFlagById(videoId, "10");
				MappingConstant.DOWNLOADING_PROGRESS.remove(videoId);
			}
			json.put("success", true);
			json.put("downloadprogress", count);
		} catch(Exception e) {
			logger.error("程序异常"+e.getMessage());
			json.put("error", e.getMessage());
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 上传进度监听
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/findUploadProgress")
	public void findUploadProgress(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		try{
			writer=response.getWriter();
			String videoId = request.getParameter("videoId");
			Object uploadprogress = MappingConstant.UPLOADING_PROGRESS.get(videoId);
			Map<String, Object> resultMap = videoService.getVideoById(videoId);
			int count = 0;
			if (uploadprogress != null) {
				logger.info("videoId:"+videoId+"的上传进度为："+uploadprogress);
				count = Integer.valueOf(String.valueOf(uploadprogress));
			}
			if(resultMap != null && "01".equals(resultMap.get("status")))
			{
				Thread thread = MappingConstant.UPLOADING_THREAD.get(videoId);
				if (thread == null) {
					MappingConstant.UPLOADING_PROGRESS.remove(videoId);
					videoService.updVideoStatusById(videoId, "02");
					json.put("uploading", false);
				} else {
					json.put("uploading", true);
				}
			}else{
				json.put("uploading", false);
				MappingConstant.UPLOADING_PROGRESS.remove(videoId);
			}
			if(count == 100)
			{
				videoService.updVideoStatusById(videoId, "10");
				MappingConstant.UPLOADING_PROGRESS.remove(videoId);
			}
			json.put("success", true);
			json.put("uploadprogress", count);
		} catch(Exception e) {
			logger.error("程序异常"+e.getMessage());
			json.put("error", e.getMessage());
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
}
