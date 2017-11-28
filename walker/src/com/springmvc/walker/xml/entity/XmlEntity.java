package com.springmvc.walker.xml.entity;

import java.util.List;

public class XmlEntity {

	private SeriesEntity series;
	private ProgramEntity program;
	private MovieEntity movie;
	private List<PictureEntity> picture;
	
	public SeriesEntity getSeries() {
		return series;
	}
	public void setSeries(SeriesEntity series) {
		this.series = series;
	}
	public ProgramEntity getProgram() {
		return program;
	}
	public void setProgram(ProgramEntity program) {
		this.program = program;
	}
	public MovieEntity getMovie() {
		return movie;
	}
	public void setMovie(MovieEntity movie) {
		this.movie = movie;
	}
	public List<PictureEntity> getPicture() {
		return picture;
	}
	public void setPicture(List<PictureEntity> picture) {
		this.picture = picture;
	}
}
