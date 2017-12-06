package com.springmvc.walker.service.xml.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.framework.entity.Page;
import com.springmvc.walker.mapper.xml.ProgramMapper;
import com.springmvc.walker.service.xml.ProgramService;
import com.springmvc.walker.xml.entity.ProgramEntity;

@Service("programService")
public class ProgramServiceImpl implements ProgramService{

	private final static Logger logger = Logger.getLogger(ProgramServiceImpl.class);
	
	@Resource
	private ProgramMapper programMapper;
	
	@Override
	public List<Map<String, Object>> getProgramPage(Map<String, Object> paraMap, Page page) {
		logger.info("查询分集信息START");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = programMapper.getProgramCount(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		list = programMapper.getProgramPage(paraMap);
		logger.info("查询分集信息SUCCESS,共" + list.size() + "条记录");
		return list;
	}

	@Override
	public ProgramEntity getProgramMapById(String PROGRAM_ID) {
		return programMapper.getProgramById(PROGRAM_ID);
	}

	@Override
	public boolean saveProgram(ProgramEntity program) {
		ProgramEntity programEntity = programMapper.getProgramByOriginalId(program.getORIGINAL_ID());
		if(null != programEntity){
			program.setPROGRAM_ID(programEntity.getPROGRAM_ID());
			logger.info("存在对应Program数据，进行更新操作："+programEntity.getPROGRAM_ID());
			return programMapper.updateProgram(program);
		}else{
			logger.info("不存在对应Program数据，插入更新操作："+program.getPROGRAM_ID());
			return programMapper.updateProgram(program);
		}
	}

	@Override
	public boolean updateProgram(ProgramEntity program) {
		return programMapper.updateProgram(program);
	}

	@Override
	public boolean updateProgramStatus(String PROGRAM_ID, String STATUS) {
		logger.info("更新programId："+PROGRAM_ID+"的状态为："+STATUS);
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("PROGRAM_ID", PROGRAM_ID);
		paraMap.put("STATUS", STATUS);
		return programMapper.updateStatusById(paraMap);
	}

	@Override
	public boolean deleteProgram(String PROGRAM_ID) {
		return programMapper.deleteProgram(PROGRAM_ID);
	}

}
