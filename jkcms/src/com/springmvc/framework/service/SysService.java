package com.springmvc.framework.service;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.JsonTreeNode;
import com.springmvc.framework.entity.Menu;
import com.springmvc.framework.entity.Page;

public interface SysService {
	
	public List<Map<String, Object>> getUserListPage(Map<String, Object> paraMap, Page page);
	
	public Map<String, Object> getLoginUserInfo(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getLevelOneMenuList(Map<String, Object> userMap);
	
	public List<Menu> getLevelTwoMenuList(String parentId, Map<String, Object> userMap);
	
	public boolean saveUser(Map<String, Object> userMap);
	
	public List<Map<String, Object>> getRoleStore();
	
	public boolean updatePassword(Map<String, Object> paraMap);
	
	public boolean deleteUser(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getPermissionList(String menuId, Map<String, Object> userMap);
	
	public List<JsonTreeNode> getTreeJsonPermissionListByRole(Map<String, Object> paraMap);
	
	public boolean deleteRoleMenuByRoleId(String roleId);
	
	public boolean saveRoleMenu(Map<String, Object> roleMenuMap);
	
	public List<Map<String, Object>> getRoleListPage(Map<String, Object> paraMap, Page page);
	
	public boolean deleteRole(Map<String, Object> paraMap);
	
	public boolean saveRole(Map<String, Object> roleMap);
	
	public List<JsonTreeNode> getTreeMenuPermissionList(Map<String, Object> paraMap);
	
	public boolean deleteMenuById(String menuId);
	
	public boolean saveMenu(Map<String, Object> menuMap);
	
	public List<Map<String, Object>> getSysConfigListPage(Map<String, Object> paraMap, Page page);
	
	public boolean saveSysConfigInfo(Map<String, Object> paraMap);
	
	public boolean updateSysTaskState();
	
	public List<Map<String, Object>> getPublicCodeListPage(Map<String, Object> paraMap, Page page);
	
	public List<Map<String, Object>> getCodeNameInfo();
	
	public boolean checkCode(Map<String, Object> paraMap);
	
	public boolean savePublicCodeInfo(Map<String, Object> paraMap);
	
	public boolean deletePublicCodeInfo(String codeId);
	
	public Map<String, Object>selectPublicCodeInfoById(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> child(String code,String status);
	
}
