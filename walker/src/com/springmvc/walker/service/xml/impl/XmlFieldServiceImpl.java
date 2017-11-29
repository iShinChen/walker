package com.springmvc.walker.service.xml.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springmvc.framework.entity.Page;
import com.springmvc.framework.util.UUIDUtil;
import com.springmvc.walker.mapper.xml.XmlFieldMapper;
import com.springmvc.walker.service.xml.XmlFieldService;

@Service("xmlFieldService")
public class XmlFieldServiceImpl implements XmlFieldService{

	private final static Logger logger = Logger.getLogger(XmlFieldServiceImpl.class);
	
	@Resource
	private XmlFieldMapper xmlFieldMapper;

	@Override
	public List<Map<String, Object>> child(String code, String status) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("code", code);
		paraMap.put("status", status);
		list = xmlFieldMapper.child(paraMap);
		return list;
	}

	@Override
	public List<Map<String, Object>> listPage(Map<String, Object> paraMap, Page page) {
		logger.info("查询公共代码信息START");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = xmlFieldMapper.listCount(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		list = xmlFieldMapper.listPage(paraMap);
		logger.info("查询公共代码信息SUCCESS,共" + list.size() + "条记录");
		return list;
	}

	@Override
	public boolean save(Map<String, Object> paraMap) {
		logger.info("保存公共代码信息 START");
		if (paraMap.get("ID") == null || "".equals(paraMap.get("ID"))) {
			paraMap.put("ID", UUIDUtil.getUUID().replaceAll("-", ""));
			return xmlFieldMapper.insert(paraMap);
		} else {
			return xmlFieldMapper.update(paraMap);
		}
		
	}

	@Override
	public Map<String, Object> selectById(String id) {
		logger.info("根据ID查询公共代码信息 START");
		Map<String, Object> map = xmlFieldMapper.selectById(id);
		logger.info("根据ID查询公共代码信息SUCCESS,id为"+id+"的信息为：" + map == null ? "null" : map.toString());
		return map;
	}

	@Override
	@Transactional
	public boolean delete(String parentId) {
		logger.info("删除字典信息 START");
		boolean success1 = xmlFieldMapper.deleteById(parentId);
		boolean success2 = xmlFieldMapper.deleteByParentId(parentId);
		return success1 || success2;
	}

	@Override
	public boolean checkCode(Map<String, Object> paraMap) {
		int result = xmlFieldMapper.checkCode(paraMap);
		return result > 0;
	}
	
}
