package com.springmvc.walker.thread;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.framework.constant.UploadStatus;
import com.springmvc.framework.util.ContinueFTP;
import com.springmvc.walker.constant.MappingConstant;
import com.springmvc.walker.service.VideoService;

public class UploadVideo2FtpThread extends Thread{

	private final static Logger logger = Logger.getLogger(UploadVideo2FtpThread.class);
	private String name;
	private String videoId;
	private String remoteUrl;
	private String localUrl;
	private VideoService videoService;
	private ContinueFTP ftp = new ContinueFTP();
	
	public UploadVideo2FtpThread(Map<String, Object> video, VideoService videoService,String localUrl){
		this.name = video.get("name").toString();
		this.videoId = video.get("id").toString();
		this.remoteUrl = video.get("url").toString();
		this.videoService = videoService;
		this.localUrl =localUrl;
		
	}
	
	public void run(){
		boolean connected = false;
		UploadStatus result;
		try {
			connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
					Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
			
			logger.info(name+"--源FTP连接状态："+connected);
			if(connected){
				logger.info(name+"******************FTP连接成功******************");
				videoService.updVideoStatusById(videoId, "01");
				result = ftp.upload(localUrl, remoteUrl,videoId);
				if(UploadStatus.Upload_New_File_Success.equals(result)){
					videoService.updVideoStatusById(videoId, "10");
				}else{
					videoService.updVideoStatusById(videoId, "02");
				}
				logger.info(name+",片源ID："+videoId+"的上传FTP结果为："+result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}try {
			MappingConstant.UPLOADING_THREAD.remove(videoId);
			ftp.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
