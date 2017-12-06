package com.springmvc.walker.service.xml;

import java.util.List;

import com.springmvc.walker.xml.entity.MovieEntity;

public interface MovieService {

	public List<MovieEntity> getMoviesByProgramId(String PROGRAM_ID);
	
	public boolean saveMovie(MovieEntity movie);
	
	public boolean updateMovie(MovieEntity movie);
	
	public boolean updateMovieStatus(String MOVIE_ID,String STATUS);
	
	public boolean deleteMovie(String MOVIE_ID);
}
