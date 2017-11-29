package com.springmvc.walker.thread.xml;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.walker.service.xml.SeriesService;
import com.springmvc.walker.xml.entity.XmlEntity;
import com.springmvc.walker.xml.util.BuildXmlFile;

public class BuildSeriesXmlThread extends Thread{

	private final static Logger logger = Logger.getLogger(BuildSeriesXmlThread.class);
	
	private String seriesId;
	private SeriesService seriesService;
	
	public BuildSeriesXmlThread(String seriesId,SeriesService seriesService){
		this.seriesId = seriesId;
		this.seriesService = seriesService;
	}
	
	public void run(){
		String fileUrl = "/C2XML/series/series_" + seriesId + ".xml";
		//工单对象封装
		XmlEntity xml = new XmlEntity();
		xml.setSeries(seriesService.getSeriesById(seriesId));
		xml.setPicture(seriesService.getPicturesBySeriesId(seriesId));
		//工单生成及写入FTP
		Document document = BuildXmlFile.createXml(xml,GlobalConstant.XML_ELEMENTS.get(GlobalConstant.XML_BOOK_SERIES));
		String dataFlag = BuildXmlFile.writeXml(document, fileUrl);
		if("01".equals(dataFlag)){
			logger.info("工单生成成功。");
		}else{
			logger.info("工单生成失败。");
		}
	}
}
