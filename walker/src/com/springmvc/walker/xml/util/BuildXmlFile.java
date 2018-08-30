package com.springmvc.walker.xml.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.framework.constant.UploadStatus;
import com.springmvc.framework.util.ContinueFTP;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.XmlEntity;

public class BuildXmlFile {

	private final static Log logger = LogFactory.getLog("XmlLog");
	
	/**
	 * 工单创建
	 * @param xml
	 * @param xmlElements
	 * @return
	 */
	public static Document createXml(XmlEntity xml,List<Map<String, Object>> xmlElements){
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("ADI");
		root.addAttribute("BizDomain", "2");
		root.addAttribute("Priority", "1");
		root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		Element objects = root.addElement("Objects");
		boolean mapFlag = false;
		Element maps = null;
		for(Map<String, Object> element : xmlElements){
			//监测工单中是否有mapping对象
			int id = Integer.valueOf(element.get("ID").toString());
			if(!mapFlag && (5 == id || 6 == id || 7 == id)){
				maps = root.addElement("Mappings");
				mapFlag = true;
			}
			switch (id) {
			case 1:
				objects = CreateXmlObject.createSeriesObj(xml.getSeries(), objects);
				break;
			case 2:
				objects = CreateXmlObject.createProgramObj(xml.getProgram(), objects);
				break;
			case 3:
				objects = CreateXmlObject.createMovieObj(xml.getMovie(), objects);
				break;
			case 4:
				for(PictureEntity picture: xml.getPicture()){
					objects = CreateXmlObject.createPictureObj(picture, objects);
				}
				break;
			case 5:
				maps = CreateXmlMapping.CreateS_P_Mapping(xml,maps);
				break;
			case 6:
				maps = CreateXmlMapping.CreateP_M_Mapping(xml,maps);
				break;
			case 7:
				maps = CreateXmlMapping.CreateS_Pic_Mapping(xml,maps);
				break;
			default:
				break;
			}
		}
		return document;
	}
	
	/**
	 * 工单写入ftp
	 * @param document
	 * @param fileUrl
	 * @return
	 */
	public static String writeXml(Document document,String fileUrl){
		ContinueFTP ftp = new ContinueFTP();
		boolean connected = false;
		UploadStatus result = null;
		
		try {
			connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
			
			if(connected)
			{
				result = ftp.uploadXML(document, fileUrl);
			}else{
				logger.info("FTP断开！");
			}
			
		} catch (IOException e) {
			logger.error("操作发生异常", e);
		} finally {
			try {
				ftp.disconnect();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if(UploadStatus.Upload_New_File_Success.equals(result))
		{
			return "01";
		}else{
			return "02";
		}
	}
}
