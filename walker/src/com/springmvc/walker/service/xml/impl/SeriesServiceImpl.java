package com.springmvc.walker.service.xml.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.walker.entity.Page;
import com.springmvc.walker.mapper.xml.SeriesMapper;
import com.springmvc.walker.service.xml.SeriesService;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.SeriesEntity;

@Service("seriesService")
public class SeriesServiceImpl implements SeriesService{
	
	private final static Logger logger = Logger.getLogger(SeriesServiceImpl.class);
	
	@Resource
	private SeriesMapper seriesMapper;

	@Override
	public SeriesEntity getSeriesById(String SERIES_ID) {
		return seriesMapper.getSeriesById(SERIES_ID);
	}

	@Override
	public List<Map<String, Object>> getPageList(Map<String, Object> paraMap, Page page) {
		logger.info("查询合集信息START");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = seriesMapper.listCount(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		list = seriesMapper.getPageList(paraMap);
		logger.info("查询合集信息SUCCESS,共" + list.size() + "条记录");
		return list;
	}

	@Override
	public List<PictureEntity> getPicturesBySeriesId(String SERIES_ID) {
		logger.info("根据合集ID查询有效图片START");
		List<PictureEntity> list = seriesMapper.getPicturesBySeriesId(SERIES_ID);
		logger.info("根据合集ID查询有效图片SUCCESS,共" + list.size() + "条记录");
		return list;
	}

}
