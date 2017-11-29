package com.springmvc.walker.xml.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.walker.constant.MappingConstant;
import com.springmvc.walker.xml.entity.MovieEntity;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.ProgramEntity;
import com.springmvc.walker.xml.entity.SeriesEntity;


public class CreateXmlObject {

	private final static Log logger = LogFactory.getLog("XmlLog");
	
	/**
	 * 封装合集对象
	 * @param series
	 * @param objects
	 * @return
	 */
	public static Element createSeriesObj(SeriesEntity series,Element objects){
		Element object = objects.addElement("Object");
		object.addAttribute("Action", MappingConstant.ACTION_REGIST)
			.addAttribute("ElementType", "Series")
			.addAttribute("ID", series.getSERIES_ID())
			.addAttribute("Code", series.getSERIES_ID());
		
		for(Map<String, Object> map : GlobalConstant.XML_BOOK.get("SERIES")){
			Class<? extends SeriesEntity> clazz = series.getClass();
			Method m;
			String seriesStr = "";
			try {
				m = clazz.getDeclaredMethod("get" + map.get("CODE"));
				Object seriesObj = (Object)m.invoke(series);
				seriesStr = seriesObj == null?seriesStr:seriesObj.toString();
			} catch (IllegalArgumentException e) {
				logger.error("操作发生异常", e);
			} catch (IllegalAccessException e) {
				logger.error("操作发生异常", e);
			} catch (InvocationTargetException e) {
				logger.error("操作发生异常", e);
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			}
			object.addElement("Property").addAttribute("Name", map.get("VALUE").toString()).addCDATA(seriesStr);
		}
		return objects;
	}
	
	/**
	 * 封装分集对象
	 * @param program
	 * @param objects
	 * @return
	 */
	public static Element createProgramObj(ProgramEntity program,Element objects){
		Element object = objects.addElement("Object");
		object.addAttribute("Action", MappingConstant.ACTION_REGIST)
			.addAttribute("ElementType", "Program")
			.addAttribute("ID", program.getPROGRAM_ID())
			.addAttribute("Code", program.getPROGRAM_ID());
		
		for(Map<String, Object> map : GlobalConstant.XML_BOOK.get("PROGRAM")){
			Class<? extends ProgramEntity> clazz = program.getClass();
			Method m;
			String programStr = "";
			try {
				m = clazz.getDeclaredMethod("get" + map.get("CODE"));
				Object programObj = (Object)m.invoke(program);
				programStr = programObj == null?programStr:programObj.toString();
			} catch (IllegalArgumentException e) {
				logger.error("操作发生异常", e);
			} catch (IllegalAccessException e) {
				logger.error("操作发生异常", e);
			} catch (InvocationTargetException e) {
				logger.error("操作发生异常", e);
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			}
			object.addElement("Property").addAttribute("Name", map.get("VALUE").toString()).addCDATA(programStr);
		}
		return objects;
	}
	
	/**
	 * 封装片源对象
	 * @param movie
	 * @param objects
	 * @return
	 */
	public static Element createMovieObj(MovieEntity movie,Element objects){
		Element object = objects.addElement("Object");
		object.addAttribute("Action", MappingConstant.ACTION_REGIST)
			.addAttribute("ElementType", "Movie")
			.addAttribute("ID", movie.getMOVIE_ID())
			.addAttribute("Code", movie.getMOVIE_ID());
		
		for(Map<String, Object> map : GlobalConstant.XML_BOOK.get("MOVIE")){
			Class<? extends MovieEntity> clazz = movie.getClass();
			Method m;
			String movieStr = "";
			try {
				m = clazz.getDeclaredMethod("get" + map.get("CODE"));
				Object movieObj = (Object)m.invoke(movie);
				movieStr = movieObj == null?movieStr:movieObj.toString();
			} catch (IllegalArgumentException e) {
				logger.error("操作发生异常", e);
			} catch (IllegalAccessException e) {
				logger.error("操作发生异常", e);
			} catch (InvocationTargetException e) {
				logger.error("操作发生异常", e);
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			}
			object.addElement("Property").addAttribute("Name", map.get("VALUE").toString()).addCDATA(movieStr);
		}
		return objects;
	}
	
	/**
	 * 封装图片对象
	 * @param picture
	 * @param objects
	 * @return
	 */
	public static Element createPictureObj(PictureEntity picture,Element objects){
		Element object = objects.addElement("Object");
		object.addAttribute("Action", MappingConstant.ACTION_REGIST)
			.addAttribute("ElementType", "Picture")
			.addAttribute("ID", picture.getPICTURE_ID())
			.addAttribute("Code", picture.getPICTURE_ID());
		
		for(Map<String, Object> map : GlobalConstant.XML_BOOK.get("PICTURE")){
			Class<? extends PictureEntity> clazz = picture.getClass();
			Method m;
			String pictureStr = "";
			try {
				m = clazz.getDeclaredMethod("get" + map.get("CODE"));
				Object pictureObj = (Object)m.invoke(picture);
				pictureStr = pictureObj == null?pictureStr:pictureObj.toString();
			} catch (IllegalArgumentException e) {
				logger.error("操作发生异常", e);
			} catch (IllegalAccessException e) {
				logger.error("操作发生异常", e);
			} catch (InvocationTargetException e) {
				logger.error("操作发生异常", e);
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			}
			object.addElement("Property").addAttribute("Name", map.get("VALUE").toString()).addCDATA(pictureStr);
		}
		return objects;
	}
	
}
