package com.springmvc.walker.service.xml;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.Page;

public interface XmlFieldService {

	public List<Map<String, Object>> child(String code, String status);

	public List<Map<String, Object>> listPage(Map<String, Object> paraMap, Page page);

	public boolean save(Map<String, Object> paraMap);

	public Map<String, Object> selectById(String id);

	public boolean delete(String codeId);

	public boolean checkCode(Map<String, Object> paraMap);
}
