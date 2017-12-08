package com.springmvc.walker.xml.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.springmvc.walker.entity.Upstream_baseBean;
import com.springmvc.walker.xml.entity.MovieEntity;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.ProgramEntity;
import com.springmvc.walker.xml.entity.SeriesEntity;
import com.springmvc.walker.xml.entity.XmlEntity;

public class ParseXmlToEntity {

	private final static Logger logger = Logger.getLogger(ParseXmlToEntity.class);
	private final static Log collectLogger = LogFactory.getLog("CollectLog");
	
	public static XmlEntity parseXml(String xml){
		collectLogger.info("ParseXmlToEntity：解析上游xml开始："+xml);
		XmlEntity xmlEntity = new XmlEntity();
		Upstream_baseBean baseBean = new Upstream_baseBean();
		try {
			
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			
			@SuppressWarnings("unchecked")
			List<Element> elements = root.elements();
			
			if(elements == null) return xmlEntity;
			for(Element element:elements){
				if("assettype".equals(element.getName())) baseBean.setAssettype(element.getText());
				if("assetdesc".equals(element.getName())) baseBean.setAssetdesc(element.getText());
				if("assetoperation".equals(element.getName())) baseBean.setAssetoperation(element.getText());
				if("info".equals(element.getName()))
				{
					@SuppressWarnings("unchecked")
					List<Element> infoNodes = element.elements();
					for(Element infoNode:infoNodes){
						if("pushcode".equals(infoNode.getName())) {
							logger.info("出库编码："+infoNode.getText()+"消息反馈");
							//Thread mqFeedBack = new RabbitMQFeedBackThread(infoNode.getText());
							//mqFeedBack.start();
						}
					}
				}
			}
			collectLogger.info("ParseXMLServiceImpl：assettype="+baseBean.getAssettype()+",assetdesc="+baseBean.getAssetdesc()+",assetoperation="+baseBean.getAssetoperation()+",filmorsingleset="+baseBean.getFilmorsingleset());

			if("1".equals(baseBean.getAssetdesc())){
				logger.info("解析合集类型工单");
				xmlEntity = parseSeries(xmlEntity,elements);
			}else if("2".equals(baseBean.getAssetdesc())){
				logger.info("解析分集类型工单");
				xmlEntity = parseProgram(xmlEntity,elements);
			}else if("3".equals(baseBean.getAssetdesc())){
				logger.info("解析片源类型工单");
				xmlEntity = parseMovie(xmlEntity,elements);
			}
		}catch(Exception e){
			collectLogger.error("解析发生异常",e);
		}
		return xmlEntity;
	}
	
	/**
	 * 解析合集工单
	 * @param xmlEntity
	 * @param elements
	 * @return
	 */
	private static XmlEntity parseSeries(XmlEntity xmlEntity,List<Element> elements){
		SeriesEntity series = new SeriesEntity();
		PictureEntity picture = new PictureEntity();
		List<PictureEntity> pictures = new ArrayList<PictureEntity>();
		for(Element element:elements){
			if("content".equals(element.getName())){
				@SuppressWarnings("unchecked")
				List<Element> contentNodes = element.elements();
				
				logger.info("封装Series");
				if(contentNodes == null) continue;
				for(Element cNode:contentNodes){
					//反射Series
					//series.setSERIES_ID(UUIDUtil.getUUID());
					series = ParseElements.invokeSeries(series,cNode);
					if("images".equals(cNode.getName())){
						logger.info("封装Pictrue");
						@SuppressWarnings("unchecked")
						List<Element> images = cNode.elements();
						
						if(images == null) continue;
						for(Element image:images){
							picture = new PictureEntity();
							picture.setSERIES_ID(series.getSERIES_ID());
							@SuppressWarnings("unchecked")
							List<Element> imageNodes = image.elements();
							for(Element imageNode:imageNodes){
								//反射Picture
								picture = ParseElements.invokePicture(picture,imageNode);
							}
							pictures.add(picture);
						}
					}
				}
				xmlEntity.setSeries(series);
				xmlEntity.setPicture(pictures);
			}
		}
		return xmlEntity;
	}
	
	/**
	 * 解析分集工单
	 * @param xmlEntity
	 * @param elements
	 * @return
	 */
	private static XmlEntity parseProgram(XmlEntity xmlEntity,List<Element> elements){
		ProgramEntity program = new ProgramEntity();
		for(Element element:elements){
			if("content".equals(element.getName())){
				@SuppressWarnings("unchecked")
				List<Element> contentNodes = element.elements();
				logger.info("封装Program");
				if(contentNodes == null) continue;
				
				for(Element cNode:contentNodes){
					//反射Program
					program = ParseElements.invokeProgram(program,cNode);
				}
				xmlEntity.setProgram(program);
			}
		}
		return xmlEntity;
	}
	
	/**
	 * 解析片源工单
	 * @param xmlEntity
	 * @param elements
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static XmlEntity parseMovie(XmlEntity xmlEntity,List<Element> elements){
		MovieEntity movie = new MovieEntity();
		for(Element element:elements){
			if("content".equals(element.getName())){
				List<Element> contentNodes = element.elements();
				for(Element cNode:contentNodes){
					if("videos".equals(cNode.getName())){
						List<Element> fileNodes = cNode.elements();
						for(Element fNode:fileNodes){
							if("file".equals(fNode.getName())){
								logger.info("封装Movie");
								List<Element> files = fNode.elements();
								for(Element file:files){
									//反射Movie
									movie = ParseElements.invokeMovie(movie, file);
								}
								xmlEntity.setMovie(movie);
							}
						}
					}
				}
			}
		}
		return xmlEntity;
	}
}
