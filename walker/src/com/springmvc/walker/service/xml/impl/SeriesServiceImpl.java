package com.springmvc.walker.service.xml.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.framework.entity.Page;
import com.springmvc.walker.mapper.xml.ProgramMapper;
import com.springmvc.walker.mapper.xml.SeriesMapper;
import com.springmvc.walker.service.xml.SeriesService;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.ProgramEntity;
import com.springmvc.walker.xml.entity.SeriesEntity;

@Service("seriesService")
public class SeriesServiceImpl implements SeriesService{
	
	private final static Logger logger = Logger.getLogger(SeriesServiceImpl.class);
	
	@Resource
	private SeriesMapper seriesMapper;
	@Resource
	private ProgramMapper programMapper;
	

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
			series.setSERIES_ID(seriesEntity.getSERIES_ID());
			logger.info("存在对应Series数据，进行更新操作："+seriesEntity.getSERIES_ID());
			return seriesMapper.updateSeries(series);
		}else{
			logger.info("不存在对应Series数据，进行插入操作："+series.getSERIES_ID());
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

	@Override
	public void checkSeries(String SERIES_ID) {
		logger.info("缺集检查开始...");
		if (SERIES_ID != null && SERIES_ID.trim().length() > 0) {
			seriesMapper.proc_check_series(SERIES_ID);
		}
		logger.info("缺集检查完成!");
	}

	@Override
	public void checkProgram(String PROGRAM_ID) {
		logger.info("缺集检查开始...");
		if (PROGRAM_ID != null && PROGRAM_ID.trim().length() > 0) {
			seriesMapper.proc_check_program(PROGRAM_ID);
		}
		logger.info("缺集检查完成!");
		
	}

	@Override
	public void initMediaCheck() {
		logger.info("初始化媒资检查结果开始...");
		seriesMapper.proc_init_media_check();
		logger.info("初始化媒资检查结果完成!");
	}

	@Override
	public boolean updateSeries(SeriesEntity series) {
		logger.info("对Series数据进行更新操作："+series.getSERIES_ID());
		return seriesMapper.updateSeries(series);
	}

	@Override
	public List<ProgramEntity> getProgramPageBySeriesId(Map<String, Object> paraMap, Page page) {
		int count = programMapper.getProgramCountBySeriesId(paraMap);
		page.setStartRow(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		page.setTotalRow(count);
		logger.info("分页查询合集集合START");
		List<ProgramEntity> list = programMapper.getProgramBySeriesId(paraMap);
		logger.info("分页查询分集集合SUCCESS,共" + list.size() + "条记录");
		return list;
	}

	@Override
	public boolean deleteSeries(String SERIES_ID) {
		// TODO Auto-generated method stub
		return false;
	}
}
