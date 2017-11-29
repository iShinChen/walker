package com.springmvc.walker.mapper.xml;

import java.util.Map;

import com.springmvc.walker.xml.entity.ProgramEntity;

public interface ProgramMapper {

	public boolean insertProgram(ProgramEntity program);
	
	public boolean updateProgram(ProgramEntity program);
	
	public boolean updateStatusById(Map<String, Object> paraMap);
	
	public ProgramEntity getProgramById(String PROGRAM_ID);
	
	public ProgramEntity getProgramByOriginalId(String ORIGINAL_ID);
	
}
