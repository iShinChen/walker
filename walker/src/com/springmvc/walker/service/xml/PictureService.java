package com.springmvc.walker.service.xml;

import com.springmvc.walker.xml.entity.PictureEntity;

public interface PictureService {

	public boolean savePicture(PictureEntity picture);
	
	public boolean updatePicture(PictureEntity picture);
	
	public boolean deletePicture(String PICTURE_ID);
	
	public boolean updatePictureStatus(String PICTURE_ID,String STATUS);
	
	public boolean checkPictureBySeriesId(String SERIES_ID);
	
}
