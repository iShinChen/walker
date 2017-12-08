package com.springmvc.walker.service.xml.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springmvc.walker.service.xml.CollectService;
import com.springmvc.walker.service.xml.MovieService;
import com.springmvc.walker.service.xml.PictureService;
import com.springmvc.walker.service.xml.ProgramService;
import com.springmvc.walker.service.xml.SeriesService;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.XmlEntity;

@Service("collectService")
public class CollectServiceImpl implements CollectService{
	
	@Autowired
	private SeriesService seriesService;
	@Autowired
	private ProgramService programService;
	@Autowired
	private MovieService movieService;
	@Autowired
	private PictureService pictureService;
	
	
	@Override
	public void parseIntoDB(XmlEntity xmlEntity) {
		if(null != xmlEntity.getSeries()){
			xmlEntity.getSeries().setSTATUS("00");
			seriesService.saveSeries(xmlEntity.getSeries());
		}
		if(null != xmlEntity.getProgram()){
			xmlEntity.getProgram().setSTATUS("00");
			programService.saveProgram(xmlEntity.getProgram());
		}
		if(null != xmlEntity.getMovie()){
			xmlEntity.getMovie().setSTATUS("00");
			movieService.saveMovie(xmlEntity.getMovie());
		}
		if(null != xmlEntity.getPicture() && !xmlEntity.getPicture().isEmpty()){
			for(PictureEntity picture:xmlEntity.getPicture()){
				picture.setSTATUS("01");
				pictureService.savePicture(picture);
			}
		}
	}

}
