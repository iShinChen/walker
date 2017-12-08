package com.springmvc.walker.service.xml.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.walker.mapper.xml.PictureMapper;
import com.springmvc.walker.service.xml.PictureService;
import com.springmvc.walker.xml.entity.PictureEntity;

@Service("pictureService")
public class PictureServiceImpl implements PictureService{

	private final static Logger logger = Logger.getLogger(PictureServiceImpl.class);
	
	@Resource
	private PictureMapper pictureMapper;
	
	@Override
	public boolean savePicture(PictureEntity picture) {
		PictureEntity pictureEntity = pictureMapper.getPictureByOriginalId(picture.getORIGINAL_ID());
		if(null != pictureEntity){
			picture.setPICTURE_ID(pictureEntity.getPICTURE_ID());
			return pictureMapper.updatePicture(picture);
		}else{
			return pictureMapper.insertPicture(picture);
		}
	}

	@Override
	public boolean updatePicture(PictureEntity picture) {
		return pictureMapper.updatePicture(picture);
	}

	@Override
	public boolean deletePicture(String PICTURE_ID) {
		return pictureMapper.deletePicture(PICTURE_ID);
	}

	@Override
	public boolean updatePictureStatus(String PICTURE_ID, String STATUS) {
		logger.info("更新picture："+PICTURE_ID+"的状态为："+STATUS);
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("SERIES_ID", PICTURE_ID);
		paraMap.put("STATUS", STATUS);
		return pictureMapper.updateStatusById(paraMap);
	}

	@Override
	public boolean checkPictureBySeriesId(String SERIES_ID) {
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("SERIES_ID", seriesId);
//		List<Map<String, Object>> list = this.baseDao.find(
//				"tVodPicture.listBySeriesId", params);
//		if (list != null) {
//			String webServer = GlobalConstant.SYS_CONFIG_MAP
//					.get(GlobalConstant.FILE_WEB_SERVER);
//			for (Map<String, Object> map : list) {
//				String isRequirt = (String) map.get("IS_REQUIRED");
//				if ("1".equals(isRequirt)) {
//					String url = (String) map.get("FILE_URL");
//					if (StringUtils.isEmptyOrBlank(url)) {
//						return false;
//					}
//
//					String status = (String) map.get("STATUS");
//					if (!"01".equals(status)) {
//						return false;
//					}
//
//					if (!url.toLowerCase().startsWith("http://")) {
//						if (url.startsWith("/")) {
//							url = webServer + url;
//						} else {
//							url = webServer + "/" + url;
//						}
//					}
//					if (!ImageUtil.exists(url)) {
//						return false;
//					}
//				}
//			}
//		}
		return true;
	}

}
