package com.springmvc.walker.service.xml;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.Page;
import com.springmvc.walker.xml.entity.ProgramEntity;

public interface ProgramService {

	public List<Map<String, Object>> getProgramPage(Map<String, Object> paraMap, Page page);
	
	public ProgramEntity getProgramMapById(String PROGRAM_ID);
	
	public boolean saveProgram(ProgramEntity program);
	
	public boolean updateProgram(ProgramEntity program);
	
	public boolean updateProgramStatus(String PROGRAM_ID ,String STATUS);
	
	public boolean deleteProgram(String PROGRAM_ID);
	
}
