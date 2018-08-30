package com.springmvc.walker.service.xml.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.springmvc.walker.mapper.xml.XmlConfigMapper;
import com.springmvc.walker.service.xml.XmlConfigService;

@Service("xmlConfigService")
public class XmlConfigServiceImpl implements XmlConfigService{

	
	@Resource
	private XmlConfigMapper xmlConfigMapper;
	
	
	@Override
	public List<Map<String, Object>> getConfigList() {
		return xmlConfigMapper.getConfigList();
	}

	@Override
	public boolean updConfig(Map<String, Object> param) {
		return xmlConfigMapper.updConfig(param);
	}

	@Override
	public List<Map<String, Object>> getXMlElements(String XML_TYPE) {
		return xmlConfigMapper.getXMlElements(XML_TYPE);
	}

	@Override
	public Map<String, Object> getConfigById(String ID) {
		return xmlConfigMapper.getConfigById(ID);
	}

}
