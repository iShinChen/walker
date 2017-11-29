package com.springmvc.walker.service.xml;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.Page;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.SeriesEntity;

public interface SeriesService {

	public SeriesEntity getSeriesById(String SERIES_ID);
	
	public List<Map<String, Object>> getPageList(Map<String, Object> paraMap, Page page);
	
	public List<PictureEntity> getPicturesBySeriesId(String SERIES_ID);
	
	public boolean saveSeries(SeriesEntity series);
	
	public boolean updateStatusById(String seriesId,String status);
	
}
