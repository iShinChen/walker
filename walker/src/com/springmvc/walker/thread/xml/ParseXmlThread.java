package com.springmvc.walker.thread.xml;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.springmvc.framework.util.FileUtil;
import com.springmvc.walker.service.xml.CollectService;
import com.springmvc.walker.xml.entity.XmlEntity;
import com.springmvc.walker.xml.util.ParseXmlToEntity;


/**
 * 工单采集
 * 
 */
public class ParseXmlThread extends Thread {

	private final static Logger logger = Logger.getLogger(ParseXmlThread.class);
	
	private CollectService collectService;
	private String fileUrl;
	
	public ParseXmlThread(CollectService collectService,String fileUrl){
		this.collectService = collectService;
		this.fileUrl = fileUrl;
	}
	public void run() {
		File file = new File(fileUrl);
		try {
			String xml = FileUtil.read(file);
			XmlEntity xmlEntity = new XmlEntity();
			xmlEntity = ParseXmlToEntity.parseXml(xml);
			collectService.parseIntoDB(xmlEntity);
			logger.info("入库结束-END");
		} catch (IOException e) {
			logger.error("采集线程异常", e);
		}
	}

}