package com.springmvc.walker.mapper.xml;

import java.util.List;
import java.util.Map;

import com.springmvc.walker.xml.entity.ProgramEntity;

public interface ProgramMapper {

	public boolean insertProgram(ProgramEntity program);
	
	public boolean updateProgram(ProgramEntity program);
	
	public boolean updateStatusById(Map<String, Object> paraMap);
	
	public boolean deleteProgram(String PROGRAM_ID);
	
	public ProgramEntity getProgramById(String PROGRAM_ID);
	
	public ProgramEntity getProgramByOriginalId(String ORIGINAL_ID);
	
	public int getProgramCountBySeriesId(Map<String, Object> paraMap);
	
	public List<ProgramEntity> getProgramBySeriesId(Map<String, Object> paraMap);
	
	public int getProgramCount(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getProgramPage(Map<String, Object> paraMap);
	
}
