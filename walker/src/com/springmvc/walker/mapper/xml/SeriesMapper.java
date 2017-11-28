package com.springmvc.walker.mapper.xml;

import java.util.List;
import java.util.Map;

import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.SeriesEntity;

public interface SeriesMapper {

	public SeriesEntity getSeriesById(String SERIES_ID);
	
	public int listCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getPageList(Map<String, Object> paraMap);
	
	public List<PictureEntity> getPicturesBySeriesId(String SERIES_ID);
}
