package com.springmvc.walker.service.xml.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.walker.mapper.xml.MovieMapper;
import com.springmvc.walker.service.xml.MovieService;
import com.springmvc.walker.xml.entity.MovieEntity;

@Service("movieService")
public class MovieServiceImpl implements MovieService{

private final static Logger logger = Logger.getLogger(MovieServiceImpl.class);
	
	@Resource
	private MovieMapper movieMapper;
	
	@Override
	public List<MovieEntity> getMoviesByProgramId(String PROGRAM_ID) {
		return movieMapper.getMoviesByProgramId(PROGRAM_ID);
	}

	@Override
	public boolean saveMovie(MovieEntity movie) {
		MovieEntity movieEntity = movieMapper.getMovieByOriginalId(movie.getORIGINAL_ID());
		if(null != movieEntity){
			movie.setMOVIE_ID(movieEntity.getMOVIE_ID());
			logger.info("存在对应Movie数据，进行更新操作："+movie.getMOVIE_ID());
			return movieMapper.updateMovie(movie);
		}else{
			logger.info("不存在对应Movie数据，插入更新操作："+movie.getMOVIE_ID());
			return movieMapper.insertMovie(movie);
		}
	}

	@Override
	public boolean updateMovie(MovieEntity movie) {
		return movieMapper.updateMovie(movie);
	}

	@Override
	public boolean updateMovieStatus(String MOVIE_ID, String STATUS) {
		logger.info("更新movieId："+MOVIE_ID+"的状态为："+STATUS);
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("MOVIE_ID", MOVIE_ID);
		paraMap.put("STATUS", STATUS);
		return movieMapper.updateStatusById(paraMap);
	}

	@Override
	public boolean deleteMovie(String MOVIE_ID) {
		return movieMapper.deleteMovie(MOVIE_ID);
	}

}
