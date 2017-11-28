package com.springmvc.walker.service.xml;

import java.util.List;
import java.util.Map;

public interface XmlConfigService {

	public List<Map<String, Object>> getConfigList();
	
	public boolean updConfig(Map<String, Object> param);
	
	public List<Map<String, Object>> getXMlElements(String XML_TYPE);

	public Map<String, Object> getConfigById(String ID);
}
