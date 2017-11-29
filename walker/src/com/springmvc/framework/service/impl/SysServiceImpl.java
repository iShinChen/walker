package com.springmvc.framework.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springmvc.framework.entity.JsonTreeNode;
import com.springmvc.framework.entity.Menu;
import com.springmvc.framework.entity.Page;
import com.springmvc.framework.mapper.SysMapper;
import com.springmvc.framework.service.SysService;
import com.springmvc.framework.util.UUIDUtil;

@Service("sysService")
public class SysServiceImpl implements SysService{
	
	private final static Logger logger = Logger.getLogger(SysServiceImpl.class);
	
	@Resource
	private SysMapper sysMapper;
	
	@Override
	public List<Map<String, Object>> getUserListPage(Map<String, Object> paraMap, Page page) {	
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = sysMapper.countUserList(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		
		logger.info("分页查询user集合START");
		list = sysMapper.getUserListPage(paraMap);
		logger.info("分页查询user集合SUCCESS,共"+list.size()+"条记录");
		return list;
	}

	@Override
	public Map<String, Object> getLoginUserInfo(Map<String, Object> paraMap) {
		logger.info("获取user");
		return sysMapper.getLoginUserInfo(paraMap);
	}

	@Override
	public List<Map<String, Object>> getLevelOneMenuList(Map<String, Object> userMap) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = sysMapper.getLevelOneMenuList(userMap);
		logger.info("获取一级菜单SUCCESS,共"+list.size()+"条记录");
		return list;
	
	}
	
	@Override
	public List<Menu> getLevelTwoMenuList(String parentId, Map<String, Object> userMap) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Menu> menuList = new ArrayList<Menu>();
		userMap.put("PARENT_ID", parentId);
		logger.info("获取二级菜单");
		list = sysMapper.getLevelTwoMenuList(userMap);
		logger.info("获取二级菜单SUCCESS,共"+list.size()+"条记录");
		if(list != null && list.size() > 0)
		{
			for(int i=0;i<list.size();i++)
			{
				Menu menu = new Menu();
				menu.setId(String.valueOf(list.get(i).get("ID")));
				menu.setName(String.valueOf(list.get(i).get("MENU_NAME")));
				menu.setUrl(String.valueOf(list.get(i).get("URL")));
				menu.setIcon(String.valueOf(list.get(i).get("ICON")));
				menu.setLeaf("1".equals(String.valueOf(list.get(i).get("LEAF")))?true:false);
				menuList.add(menu);
			}
		}
		return menuList;
	}

	@Override
	@Transactional
	public boolean saveUser(Map<String, Object> userMap) {
		if(userMap != null && userMap.get("PASSWORD") != null && !"".equals(userMap.get("PASSWORD"))){
			userMap.put("PASSWORD", userMap.get("PASSWORD").toString());
		}
		if(userMap != null && userMap.get("ID") != null && !"".equals(userMap.get("ID")))
		{
			sysMapper.updateUser(userMap);
			if(userMap.get("RU_ID") != null && !"".equals(userMap.get("RU_ID"))){
				return sysMapper.updateRoleUser(userMap);
			}else{
				userMap.put("RU_ID", UUIDUtil.getUUID());
				return sysMapper.insertRoleUser(userMap);
			}
		}else{
			userMap.put("ID", UUIDUtil.getUUID());
			logger.info("保存用户信息"+userMap.toString());
			sysMapper.insertUser(userMap);
			logger.info("保存角色与用户关系"+userMap.toString());
			userMap.put("RU_ID", UUIDUtil.getUUID());
			sysMapper.insertRoleUser(userMap);
			logger.info("保存用户信息及角色关系成功");
			return true;
		}
	}

	@Override
	public List<Map<String, Object>> getRoleStore() {
		List<Map<String, Object>> returnList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		logger.info("获取角色下拉数据");
		sysMapper.getRoleStore();
		logger.info("获取角色下拉数据SUCCESS,共"+list.size()+"条记录");
		Map<String,Object> map= new HashMap<String, Object>();
		map.put("ID", "");
		map.put("TEXT", "　");
		returnList.add(map);
		for(int i=0;i<list.size();i++){
			returnList.add(list.get(i));
		}
		return returnList;
	}

	@Override
	public boolean updatePassword(Map<String, Object> paraMap) {
		Map<String, Object> map = sysMapper.getUserInfo(paraMap);
		if(map!=null){
			if(paraMap != null){
				if(paraMap.get("NPASSWORD") != null && !"".equals(paraMap.get("NPASSWORD"))){
					paraMap.put("NPASSWORD", paraMap.get("NPASSWORD"));
				}
				return sysMapper.updatePassword(paraMap);
			}
		}
		return false;
	}

	@Override
	public boolean deleteUser(Map<String, Object> paraMap) {
		return sysMapper.deleteUser(paraMap);
	}

	@Override
	public List<Map<String, Object>> getPermissionList(String menuId, Map<String, Object> userMap) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		userMap.put("MENU_ID", menuId);
		logger.info("获取按钮权限");
		list = sysMapper.getPermissionList(userMap);
		logger.info("获取按钮权限SUCCESS,共"+list.size()+"条记录");
		return list;
	}

	@Override
	public List<JsonTreeNode> getTreeJsonPermissionListByRole(Map<String, Object> paraMap) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<JsonTreeNode> menuList = new ArrayList<JsonTreeNode>();
		logger.info("根据角色获取权限树");
		list = sysMapper.getLevelOneMenuListByRole(paraMap);
		logger.info("根据角色获取权限树SUCCESS,共"+list.size()+"条记录");
		if(list != null && list.size() > 0)
		{
			for(int i=0;i<list.size();i++)
			{
				JsonTreeNode jsonTreeNode = new JsonTreeNode();
				jsonTreeNode.setId(String.valueOf(list.get(i).get("ID")));
				jsonTreeNode.setText(String.valueOf(list.get(i).get("MENU_NAME")));
				jsonTreeNode.setUrl(String.valueOf(list.get(i).get("URL")));
				jsonTreeNode.setIconCls(String.valueOf(list.get(i).get("ICON")));
				jsonTreeNode.setLeaf("1".equals(String.valueOf(list.get(i).get("LEAF")))?true:false);
				jsonTreeNode.setChecked(Boolean.valueOf(String.valueOf(list.get(i).get("CHECKED"))));
				jsonTreeNode.setExpanded(true);
				
				//封装所有子节点
				setPermissionJsonTree(jsonTreeNode, paraMap.get("ROLE_ID").toString());
				menuList.add(jsonTreeNode);
			}
		}
		return menuList;
	}
	
	public void setPermissionJsonTree(JsonTreeNode jsonTreeNode, String roleId) {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		//根据parentId获取子节点集合
		List<JsonTreeNode> menuList = new ArrayList<JsonTreeNode>();
		
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("PARENT_ID", jsonTreeNode.getId());
		paraMap.put("ROLE_ID", roleId);
		
		logger.info("根据角色获取权限树");
		list = sysMapper.getChildMenuListByRole(paraMap);
		logger.info("根据角色获取权限树SUCCESS,共"+list.size()+"条记录");
		
		if(list != null && list.size() > 0)
		{
			jsonTreeNode.setLeaf(false);
			for(int i=0;i<list.size();i++)
			{
				JsonTreeNode jtn = new JsonTreeNode();
				jtn.setId(String.valueOf(list.get(i).get("ID")));
				jtn.setText(String.valueOf(list.get(i).get("MENU_NAME")));
				jtn.setUrl(String.valueOf(list.get(i).get("URL")));
				jtn.setIconCls(String.valueOf(list.get(i).get("ICON")));
				jtn.setLeaf("1".equals(String.valueOf(list.get(i).get("LEAF")))?true:false);
				jtn.setChecked(Boolean.valueOf(String.valueOf(list.get(i).get("CHECKED"))));
				jtn.setExpanded(true);
				menuList.add(jtn);
			}
		}
		
		if(menuList != null)
		{
			jsonTreeNode.setChildren(menuList);
			for(int i=0;i<menuList.size();i++)
			{
				JsonTreeNode j = menuList.get(i);
				this.setPermissionJsonTree(j, roleId);
			}
		}
	}

	@Override
	public boolean deleteRoleMenuByRoleId(String roleId) {
		logger.info("根据角色删除菜单数据"+roleId);
		return sysMapper.deleteRoleMenuByRoleId(roleId);
	}

	@Override
	public boolean saveRoleMenu(Map<String, Object> roleMenuMap) {
		logger.info("保存角色权限信息"+roleMenuMap.toString());
		roleMenuMap.put("ID", UUIDUtil.getUUID());
		return sysMapper.insertRoleMenu(roleMenuMap);
	}

	@Override
	public List<Map<String, Object>> getRoleListPage(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = sysMapper.countRoleList(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		
		logger.info("分页查询role集合START");
		list = sysMapper.getRoleListPage(paraMap);
		logger.info("分页查询role集合SUCCESS,共"+list.size()+"条记录");
		return list;
	}

	@Override
	@Transactional
	public boolean deleteRole(Map<String, Object> paraMap) {
		logger.info("删除角色"+paraMap.toString());
		sysMapper.deleteRole(paraMap);;		
		sysMapper.deleteRoleAndUserRela(paraMap);		
		sysMapper.deleteRoleAndMenuRela(paraMap);		
		return true;
	}

	@Override
	public boolean saveRole(Map<String, Object> roleMap) {
		logger.info("保存角色信息"+roleMap.toString());
		if(roleMap != null && roleMap.get("ID") != null && !"".equals(roleMap.get("ID")))
		{
			return sysMapper.updateRole(roleMap);
		}else{
			roleMap.put("ID", UUIDUtil.getUUID());
			roleMap.put("CODE", roleMap.get("ID"));
			return sysMapper.insertRole(roleMap);
		}
	}

	@Override
	public List<JsonTreeNode> getTreeMenuPermissionList(Map<String, Object> paraMap) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<JsonTreeNode> menuList = new ArrayList<JsonTreeNode>();

		logger.info("获取权限树");
		list = sysMapper.getJsonLevelOneMenuList(paraMap);
		logger.info("获取权限树SUCCESS,共"+list.size()+"条记录");
		
		if(list != null && list.size() > 0)
		{
			for(int i=0;i<list.size();i++)
			{
				JsonTreeNode jsonTreeNode = new JsonTreeNode();
				jsonTreeNode.setId(String.valueOf(list.get(i).get("ID")));
				jsonTreeNode.setText(String.valueOf(list.get(i).get("MENU_NAME")));
				jsonTreeNode.setUrl(String.valueOf(list.get(i).get("URL")));
				jsonTreeNode.setIconCls(String.valueOf(list.get(i).get("ICON")));
				jsonTreeNode.setLeaf("1".equals(String.valueOf(list.get(i).get("LEAF")))?true:false);
				jsonTreeNode.setParent(String.valueOf(list.get(i).get("PARENT_ID")));
				jsonTreeNode.setExpanded(true);
				jsonTreeNode.setOrderBy(String.valueOf(list.get(i).get("MENU_ORDER")));
				jsonTreeNode.setType(String.valueOf(list.get(i).get("MENU_TYPE")));
				jsonTreeNode.setRealLeaf("1".equals(String.valueOf(list.get(i).get("LEAF")))?true:false);
				jsonTreeNode.setLevel(String.valueOf(list.get(i).get("LEVEL")));
				
				//封装所有子节点
				setPermissionTree(jsonTreeNode);
				menuList.add(jsonTreeNode);
			}
		}
		return menuList;
	}
	
	public void setPermissionTree(JsonTreeNode jsonTreeNode) {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		//根据parentId获取子节点集合
		List<JsonTreeNode> menuList = new ArrayList<JsonTreeNode>();
		
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("PARENT_ID", jsonTreeNode.getId());
		
		logger.info("获取权限树");
		list = sysMapper.getJsonChildMenuList(paraMap);
		logger.info("获取权限树SUCCESS,共"+list.size()+"条记录");
		
		if(list != null && list.size() > 0)
		{
			jsonTreeNode.setLeaf(false);
			for(int i=0;i<list.size();i++)
			{
				JsonTreeNode jtn = new JsonTreeNode();
				jtn.setId(String.valueOf(list.get(i).get("ID")));
				jtn.setText(String.valueOf(list.get(i).get("MENU_NAME")));
				jtn.setUrl(String.valueOf(list.get(i).get("URL")));
				jtn.setIconCls(String.valueOf(list.get(i).get("ICON")));
				jtn.setLeaf("1".equals(String.valueOf(list.get(i).get("LEAF")))?true:false);
				jtn.setParent(String.valueOf(list.get(i).get("PARENT_ID")));
				jtn.setExpanded(true);
				jtn.setOrderBy(String.valueOf(list.get(i).get("MENU_ORDER")));
				jtn.setType(String.valueOf(list.get(i).get("MENU_TYPE")));
				jtn.setRealLeaf("1".equals(String.valueOf(list.get(i).get("LEAF")))?true:false);
				jtn.setLevel(String.valueOf(list.get(i).get("LEVEL")));
				menuList.add(jtn);
			}
		}
		if(menuList != null)
		{
			jsonTreeNode.setChildren(menuList);
			for(int i=0;i<menuList.size();i++)
			{
				JsonTreeNode j = menuList.get(i);
				this.setPermissionTree(j);
			}
		}
	}

	@Override
	public boolean deleteMenuById(String menuId) {
		logger.info("根据菜单删除菜单数据"+menuId);
		return sysMapper.deleteMenuById(menuId);

	}

	@Override
	public boolean saveMenu(Map<String, Object> menuMap) {
		logger.info("保存功能菜单信息"+menuMap.toString());
		int count = sysMapper.countMenuById(menuMap);
		if(count > 0)
		{
			logger.info("更新功能菜单");
			return sysMapper.updateMenuById(menuMap);
		}else{
			logger.info("新增功能菜单");
			menuMap.put("ID", UUIDUtil.getUUID());
			return sysMapper.insertMenu(menuMap);
		}
	}

	@Override
	public List<Map<String, Object>> getSysConfigListPage(Map<String, Object> paraMap, Page page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = sysMapper.countSysConfigList(paraMap);
		page.setTotalRow(count);
		page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		
		paraMap.put("start", page.getCurrPage());
		paraMap.put("limit", page.getPageRow());
		
		logger.info("分页查询系统配置集合START");
		list = sysMapper.getSysConfigListPage(paraMap);
		logger.info("分页查询系统配置集合SUCCESS,共"+list.size()+"条记录");
		return list;
	}

	@Override
	public boolean saveSysConfigInfo(Map<String, Object> paraMap) {
		logger.info("保存系统配置 参数："+paraMap.toString());
		return sysMapper.updateSysConfigInfo(paraMap);
	}

	@Override
	public boolean updateSysTaskState() {
		logger.info("初始化任务状态Start");
		return sysMapper.updateSysTaskState();
	}

	@Override
	public List<Map<String, Object>> getPublicCodeListPage(Map<String, Object> paraMap, Page page) {
		logger.info("查询公共代码信息START");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count = sysMapper.countPublicCodeInfo(paraMap);

		page.setStartRow(Integer.parseInt(paraMap.get("start").toString()));
		page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
		page.setTotalRow(count);

		list = sysMapper.getPublicCodeListPage(paraMap);
		logger.info("查询公共代码信息SUCCESS,共" + list.size() + "条记录");
		logger.info("查询公共代码信息END");
		return list;
	}

	@Override
	public List<Map<String, Object>> getCodeNameInfo() {
		logger.info("获取代码名字START");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = sysMapper.getCodeNameInfo();
		logger.info("获取代码名字SUCCESS,共" + list.size() + "条记录");
		logger.info("获取代码名字END");
		return list;
	}

	@Override
	public boolean checkCode(Map<String, Object> paraMap) {
		int result = sysMapper.checkCode(paraMap);
		if (result > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean savePublicCodeInfo(Map<String, Object> paraMap) {
		logger.info("保存公共代码信息 START");
		if (paraMap.get("ID") == null || "".equals(paraMap.get("ID"))) {
			paraMap.put("ID", UUIDUtil.getUUID());
			return sysMapper.insertPublicCodeInfo(paraMap);
		}else{
			return sysMapper.updatePublicCodeInfo(paraMap);
		}	
	}

	@Override
	public boolean deletePublicCodeInfo(String codeId) {
		logger.info("删除字典信息 START");
		return sysMapper.deletePublicCodeInfo(codeId);
	}

	@Override
	public Map<String, Object> selectPublicCodeInfoById(Map<String, Object> paraMap) {
		logger.info("根据ID查询公共代码信息 START");
		Map<String,Object> map = new HashMap<String, Object>();
		map = sysMapper.selectPublicCodeInfoById(paraMap);
		logger.info("根据ID查询公共代码信息SUCCESS,信息为：" + map.toString());	
		return map;
	}
	
}
