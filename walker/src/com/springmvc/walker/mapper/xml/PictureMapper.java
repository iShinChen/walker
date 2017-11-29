package com.springmvc.walker.mapper.xml;

import java.util.Map;

import com.springmvc.walker.xml.entity.PictureEntity;

public interface PictureMapper {

	public boolean insertPicture(PictureEntity picture);
	
	public boolean updatePicture(PictureEntity picture);
	
	public boolean updateStatusById(Map<String, Object> paraMap);
	
	public PictureEntity getPictureById(String PICTURE_ID);
	
	public PictureEntity getPictureByOriginalId(String ORIGINAL_ID);
}
