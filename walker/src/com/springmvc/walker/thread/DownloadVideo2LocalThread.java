package com.springmvc.walker.thread;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.springmvc.walker.constant.DownloadStatus;
import com.springmvc.walker.constant.GlobalConstant;
import com.springmvc.walker.constant.MappingConstant;
import com.springmvc.walker.service.VideoService;
import com.springmvc.walker.util.ContinueFTP;



public class DownloadVideo2LocalThread extends Thread {
	
	private final static Logger logger = Logger.getLogger(DownloadVideo2LocalThread.class);
	private String name;
	private String videoId;
	private String url;
	private VideoService videoService;
	private boolean downloading = true;
	private ContinueFTP ftp = new ContinueFTP();
	private String localPath;
	
	public DownloadVideo2LocalThread(Map<String, Object> video, VideoService videoService,String localPath) {
		this.name = video.get("name").toString();
		this.videoId = video.get("id").toString();
		this.url = video.get("url").toString();
		this.videoService = videoService;
		this.localPath = localPath;
		this.setName(videoId);
	}
	
	public void stopDownload() {
		this.downloading = false;
		this.ftp.stopDownLoad();
		logger.info("暂停片源下载线程<" + videoId + ">成功!");
	}
	
	public void run() {
		boolean connected = false;
		DownloadStatus result;
		try {
			connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
					Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
			
			logger.info(name+"--源FTP连接状态："+connected);
			
			if(connected)
			{
				logger.info(name+"******************FTP连接成功******************");
				videoService.updVideoFlagById(videoId, "01");
				// 进行片源下载
				result = ftp.download(name, videoId, url, localPath);
				logger.info(name+",片源ID："+videoId+"的下载结果为："+result);
				
				if(DownloadStatus.Download_New_Success.equals(result) || DownloadStatus.Download_From_Break_Success.equals(result) || DownloadStatus.Local_Equals_Remote.equals(result))
				{
					videoService.updVideoFlagById(videoId, "10");
				}else if(DownloadStatus.Remote_File_Noexist.equals(result)){
					logger.info(name+"==="+videoId+":下载失败原因"+result);
					videoService.updVideoFlagById(videoId, "03");
				}else if(DownloadStatus.Download_New_Failed.equals(result) || DownloadStatus.Download_From_Break_Failed.equals(result) || DownloadStatus.Create_Directory_Fail.equals(result)){
					logger.info(name+"==="+videoId+":下载失败原因"+result);
					videoService.updVideoFlagById(videoId, "02");
				}else if(DownloadStatus.Download_Interrupt.equals(result)){
					logger.info(name+"==="+videoId+":下载失败原因"+result);
					videoService.updVideoFlagById(videoId, "04");
				}else{
					logger.info(name+"==="+videoId+":下载失败原因"+result);
					videoService.updVideoFlagById(videoId, "02");
				}
			}
		} catch (Exception e) {
			logger.error(name+",片源ID："+videoId+",FTP连接出错" ,e);
			if (!downloading) {
				videoService.updVideoFlagById(videoId, "04");
			}else{
				videoService.updVideoFlagById(videoId, "02");
			}
		} finally {
			try {
				MappingConstant.DOWNLOADING_THREAD.remove(videoId);
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
