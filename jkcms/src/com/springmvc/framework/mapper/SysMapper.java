package com.springmvc.framework.mapper;

import java.util.List;
import java.util.Map;

public interface SysMapper {

	public int countUserList(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getUserListPage(Map<String, Object> paraMap);
	
	public Map<String, Object> getLoginUserInfo(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getLevelOneMenuList(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getLevelTwoMenuList(Map<String, Object> paraMap);
	
	public boolean updateUser(Map<String, Object> paraMap);
	
	public boolean updateRoleUser(Map<String, Object> paraMap);
	
	public boolean insertRoleUser(Map<String, Object> paraMap);
	
	public boolean insertUser(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getRoleStore();
	
	public Map<String, Object> getUserInfo(Map<String, Object> paraMap);
	
	public boolean updatePassword(Map<String, Object> paraMap);
	
	public boolean deleteUser(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getPermissionList(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getLevelOneMenuListByRole(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getChildMenuListByRole(Map<String, Object> paraMap);
	
	public boolean deleteRoleMenuByRoleId(String roleId);
	
	public boolean insertRoleMenu(Map<String, Object> paraMap);
	
	public int countRoleList(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getRoleListPage(Map<String, Object> paraMap);
	
	boolean deleteRole(Map<String, Object> paraMap);
	
	boolean deleteRoleAndUserRela(Map<String, Object> paraMap);
	
	boolean deleteRoleAndMenuRela(Map<String, Object> paraMap);
	
	boolean updateRole(Map<String, Object> paraMap);
	
	boolean insertRole(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getJsonLevelOneMenuList(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getJsonChildMenuList(Map<String, Object> paraMap);
	
	boolean deleteMenuById(String menuId);
	
	int countMenuById(Map<String, Object> paraMap);
	
	boolean updateMenuById(Map<String, Object> paraMap);
	
	boolean insertMenu(Map<String, Object> paraMap);
	
	int countSysConfigList(Map<String, Object> paraMap);
	
	List<Map<String, Object>> getSysConfigListPage(Map<String, Object> paraMap);
	
	boolean updateSysConfigInfo(Map<String, Object> paraMap);
	
	boolean updateSysTaskState();
	
	public int countPublicCodeInfo(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getPublicCodeListPage(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> getCodeNameInfo();
	
	public boolean insertPublicCodeInfo(Map<String, Object> paraMap);
	
	public boolean updatePublicCodeInfo(Map<String, Object> paraMap);
	
	public boolean deletePublicCodeInfo(String codeId);
	
	public Map<String, Object> selectPublicCodeInfoById(Map<String, Object> paraMap);
	
	public int checkCode(Map<String, Object> paraMap);
	
	public List<Map<String, Object>> child(Map<String, Object> paraMap);
	
}
