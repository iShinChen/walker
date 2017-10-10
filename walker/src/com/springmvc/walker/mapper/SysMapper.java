package com.springmvc.walker.mapper;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface SysMapper {

	int countUserList(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getUserListPage(Map<String, Object> paraMap);
	
	Map<String, Object> getLoginUserInfo(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getLevelOneMenuList(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getLevelTwoMenuList(Map<String, Object> paraMap);
	
	void updateUser(Map<String, Object> paraMap);
	
	void updateRoleUser(Map<String, Object> paraMap);
	
	void insertRoleUser(Map<String, Object> paraMap);
	
	void insertUser(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getRoleStore();
	
	Map<String, Object> getUserInfo(Map<String, Object> paraMap);
	
	void updatePassword(Map<String, Object> paraMap);
	
	void deleteUser(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getPermissionList(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getLevelOneMenuListByRole(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getChildMenuListByRole(Map<String, Object> paraMap);
	
	void deleteRoleMenuByRoleId(String roleId);
	
	void insertRoleMenu(Map<String, Object> paraMap);
	
	int countRoleList(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getRoleListPage(Map<String, Object> paraMap);
	
	void deleteRole(Map<String, Object> paraMap);
	
	void deleteRoleAndUserRela(Map<String, Object> paraMap);
	
	void deleteRoleAndMenuRela(Map<String, Object> paraMap);
	
	void updateRole(Map<String, Object> paraMap);
	
	void insertRole(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getJsonLevelOneMenuList(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getJsonChildMenuList(Map<String, Object> paraMap);
	
	void deleteMenuById(String menuId);
	
	int countMenuById(Map<String, Object> paraMap);
	
	void updateMenuById(Map<String, Object> paraMap);
	
	void insertMenu(Map<String, Object> paraMap);
	
	int countSysConfigList(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getSysConfigListPage(Map<String, Object> paraMap);
	
	void updateSysConfigInfo(Map<String, Object> paraMap);
	
	void updateSysTaskState();
	
	public int countPublicCodeInfo(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getPublicCodeListPage(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getCodeNameInfo();
	
	public void insertPublicCodeInfo(Map<String, Object> paraMap);
	
	public void updatePublicCodeInfo(Map<String, Object> paraMap);
	
	public void deletePublicCodeInfo(String codeId);
	
	public Map<String, Object> selectPublicCodeInfoById(Map<String, Object> paraMap);
	
	public int checkCode(Map<String, Object> paraMap);
	
}
