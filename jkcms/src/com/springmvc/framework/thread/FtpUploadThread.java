package com.springmvc.framework.thread;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.springmvc.framework.domain.FtpServer;
import com.springmvc.framework.util.FtpUtils;

public class FtpUploadThread extends Thread {
	private final static Logger logger = Logger.getLogger(FtpUploadThread.class);
	
	private CountDownLatch latch = null;

	private FtpServer server;
	private String uploadFileUrl;
	private File file;
	
	/**
	 * 
	 * @param server FTP服务器信息
	 * @param url ftp文件路径
	 * @param file 上传文件
	 * @param latch 同步计数器
	 */
	public FtpUploadThread(FtpServer server, String url, File file, CountDownLatch latch) {
		this.server = server;
		this.uploadFileUrl = url;
		this.file = file;
		this.latch = latch;
	}
	
	public void run() {
		logger.info("文件" + server.getFileDir() + "上传开始...");
		FtpUtils ftpUtils = new FtpUtils();
		try {
			ftpUtils.connect(server, false);
			
			if (ftpUtils.isConnected()) {
				logger.info("FTP服务" + server.getHost() + "连接成功!");
				String root = server.getFileDir();
				if (root == null) {
					root = "";
				}
				logger.info("文件" + server.getFileDir() + "上传开始...");
				
				String ftpFileUrl = root + uploadFileUrl;
				if (root.endsWith("/") && uploadFileUrl.startsWith("/")) {
					ftpFileUrl = root.substring(0, root.length() - 1) + uploadFileUrl;
				}
				else if (!root.endsWith("/") && !uploadFileUrl.startsWith("/")) {
					ftpFileUrl = root + "/" + uploadFileUrl;
				}
				
				ftpUtils.upload(ftpFileUrl, file);
				logger.info("文件" + server.getFileDir() + "上传完成!");
			}
			else {
				logger.error("FTP服务" + server.getHost() + "连接不成功!");
			}
		} catch (Exception e) {
			logger.error("文件上传异常：", e);
		} finally {
			try {
				ftpUtils.disconnect();
				logger.error("FTP服务" + server.getHost() + "连接关闭!");
			} catch (IOException e) {
			}
			latch.countDown();
		}
	}
}
