package com.springmvc.walker.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.walker.entity.Page;
import com.springmvc.walker.mapper.file.FileMapper;
import com.springmvc.walker.service.FileService;
import com.springmvc.walker.util.UUIDUtil;

@Service("fileService")
public class FileServiceImpl implements FileService{
	
	private final static Logger logger = Logger.getLogger(FileServiceImpl.class);
	
	@Resource
	private FileMapper fileMapper;

	@Override
	public List<Map<String, Object>> getFilePage(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			int count = fileMapper.getFileCount(paraMap);
			page.setTotalRow(count);
			page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
			page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
			
			paraMap.put("start", page.getCurrPage());
			paraMap.put("limit", page.getPageRow());
			
			logger.info("分页查询文件信息START");
			list = fileMapper.getFilePage(paraMap);
			logger.info("分页查询文件信息SUCCESS,共"+list.size()+"条记录");
		} catch (Exception e) {
			logger.error("查询信息集合发生异常", e);
		}
		return list;}

	@Override
	public void saveFile(Map<String, Object> paraMap) {
		if(null == paraMap){
			return;
		}
		try {
			if(null != paraMap.get("id") && !"".equals(paraMap.get("id"))){
				logger.info("更新文件数据"+paraMap.toString());
				fileMapper.updateFile(paraMap);
				logger.info("更新文件数据成功");
			}else{
				paraMap.put("id", UUIDUtil.getUUID());
				logger.info("插入文件数据"+paraMap.toString());
				fileMapper.insertFile(paraMap);
				logger.info("插入文件数据成功");
			}
			
		} catch (Exception e) {
			logger.error("保存发生异常", e);
		}
	}

	@Override
	public void deleteFile(String ids) {
		try {
			if(null != ids && !"".equals(ids)){
				logger.info("逻辑删除信息数据");
				String[] id = ids.split(",");
//				fileMapper.deleteFile(id);
				fileMapper.deleteCompletely(id);
				logger.info("删除数据成功");
			}else{
				logger.info("物理删除信息数据");
				String[] id = ids.split(",");
				fileMapper.deleteCompletely(id);
				logger.info("删除数据成功");
			}
		} catch (Exception e) {
			logger.error("操作发生异常", e);
		}
	}

	@Override
	public Map<String, Object> getFileById(String id) {
		Map<String, Object> file = new HashMap<String, Object>();
		try {
			logger.info("查询单条信息id:" + id);
			file = fileMapper.getFileById(id);
			logger.info("查询单条信息成功:" + file.toString());
		} catch (Exception e) {
			logger.error("查询单条信息发生异常", e);
			file = null;
		}
		return file;
	}

	@Override
	public Map<String, Object> getFileByName(String name) {
		Map<String, Object> file = new HashMap<String, Object>();
		try {
			logger.info("查询同名信息:" + name);
			file = fileMapper.getFileByName(name);
			if(null!=file){
				logger.info("查询同名信息成功:" + file.toString());
			}else{
				logger.info("查询同名信息成功:无同名数据");
			}
			
		} catch (Exception e) {
			logger.error("查询同名信息发生异常", e);
			file = null;
		}
		return file;
	}

}
