package com.springmvc.walker.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.walker.entity.JsonTreeNode;
import com.springmvc.walker.entity.Menu;
import com.springmvc.walker.entity.Page;

public interface SysService {
	
	public List<Map<String, Object>> getUserListPage(Map<String, Object> paraMap, Page page);
	
	public Map<String, Object> getLoginUserInfo(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getLevelOneMenuList(Map<String, Object> userMap);
	
	public List<Menu> getLevelTwoMenuList(String parentId, Map<String, Object> userMap);
	
	public void saveUser(Map<String, Object> userMap);
	
	public List<Map<String, Object>> getRoleStore();
	
	public JSONObject updatePassword(Map<String, Object> paraMap);
	
	public JSONObject deleteUser(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getPermissionList(String menuId, Map<String, Object> userMap);
	
	public List<JsonTreeNode> getTreeJsonPermissionListByRole(Map<String, Object> paraMap);
	
	public void deleteRoleMenuByRoleId(String roleId);
	
	public void saveRoleMenu(Map<String, Object> roleMenuMap);
	
	public List<Map<String, Object>> getRoleListPage(Map<String, Object> paraMap, Page page);
	
	public void deleteRole(Map<String, Object> paraMap);
	
	public void saveRole(Map<String, Object> roleMap);
	
	public List<JsonTreeNode> getTreeMenuPermissionList(Map<String, Object> paraMap);
	
	public void deleteMenuById(String menuId);
	
	public void saveMenu(Map<String, Object> menuMap);
	
	public List<Map<String, Object>> getSysConfigListPage(Map<String, Object> paraMap, Page page);
	
	public int saveSysConfigInfo(Map<String, Object> paraMap);
	
	public void updateSysTaskState();
	
	public List<Map<String, Object>> getPublicCodeListPage(Map<String, Object> paraMap, Page page);
	
	public List<Map<String, Object>> getCodeNameInfo();
	
	public boolean checkCode(Map<String, Object> paraMap);
	
	public void savePublicCodeInfo(Map<String, Object> paraMap);
	
	public boolean deletePublicCodeInfo(String codeId);
	
	public Map<String, Object>selectPublicCodeInfoById(Map<String, Object> paraMap);
	
}
