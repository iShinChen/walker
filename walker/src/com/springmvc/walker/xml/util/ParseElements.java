package com.springmvc.walker.xml.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.walker.xml.entity.MovieEntity;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.ProgramEntity;
import com.springmvc.walker.xml.entity.SeriesEntity;

public class ParseElements {

	private final static Logger logger = Logger.getLogger(ParseElements.class);
	
	/**
	 * 反射合集对象
	 * @param series
	 * @param node
	 * @return
	 */
	public static SeriesEntity invokeSeries(SeriesEntity series,Element node) {
		for(Map<String, Object> map : GlobalConstant.XML_BOOK.get("PARSE_S")){
			if(!node.getName().equals(map.get("CODE"))) continue;
			Class<? extends SeriesEntity> clazz = series.getClass();
			Method m;
			try {
				m = clazz.getDeclaredMethod("set" + map.get("VALUE"), String.class);
				m.invoke(series, node.getText());
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
		}
		return series;
	}
	
	/**
	 * 反射分集对象
	 * @param program
	 * @param node
	 * @return
	 */
	public static ProgramEntity invokeProgram(ProgramEntity program,Element node) {
		for(Map<String, Object> map : GlobalConstant.XML_BOOK.get("PARSE_P")){
			if(!node.getName().equals(map.get("CODE"))) continue;
			Class<? extends ProgramEntity> clazz = program.getClass();
			Method m;
			try {
				m = clazz.getDeclaredMethod("set" + map.get("VALUE"), String.class);
				m.invoke(program, node.getText());
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
		}
		return program;
	}
	
	/**
	 * 反射片源对象
	 * @param movie
	 * @param node
	 * @return
	 */
	public static MovieEntity invokeMovie(MovieEntity movie,Element node) {
		for(Map<String, Object> map : GlobalConstant.XML_BOOK.get("PARSE_M")){
			if(!node.getName().equals(map.get("CODE"))) continue;
			Class<? extends MovieEntity> clazz = movie.getClass();
			Method m;
			try {
				m = clazz.getDeclaredMethod("set" + map.get("VALUE"), String.class);
				m.invoke(movie, node.getText());
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
		}
		return movie;
	}
	
	/**
	 * 反射图片对象
	 * @param picture
	 * @param node
	 * @return
	 */
	public static PictureEntity invokePicture(PictureEntity picture,Element node) {
		for(Map<String, Object> map : GlobalConstant.XML_BOOK.get("PARSE_PIC")){
			if(!node.getName().equals(map.get("CODE"))) continue;
			Class<? extends PictureEntity> clazz = picture.getClass();
			Method m;
			try {
				m = clazz.getDeclaredMethod("set" + map.get("VALUE"), String.class);
				m.invoke(picture, node.getText());
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
		}
		return picture;
	}
}
