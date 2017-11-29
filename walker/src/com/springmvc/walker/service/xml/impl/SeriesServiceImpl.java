package com.springmvc.walker.service.xml.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.framework.entity.Page;
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

	@Override
	public boolean saveSeries(SeriesEntity series) {
		SeriesEntity seriesEntity = seriesMapper.getSeriesByOriginalId(series.getORIGINAL_ID());
		if(null != seriesEntity){
			logger.info("存在对应数据，进行更新操作："+seriesEntity.toString());
			series.setSERIES_ID(seriesEntity.getSERIES_ID());
			return seriesMapper.updateSeries(series);
		}else{
			logger.info("不存在对应数据，进行插入操作："+series.toString());
			return seriesMapper.insertSeries(series);
		}
	}

	@Override
	public boolean updateStatusById(String seriesId, String status) {
		logger.info("更新seriesId："+seriesId+"的状态为："+status);
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("SERIES_ID", seriesId);
		paraMap.put("STATUS", status);
		return seriesMapper.updateStatusById(paraMap);
	}
}
