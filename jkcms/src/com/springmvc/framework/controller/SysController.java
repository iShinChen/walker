package com.springmvc.framework.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.framework.entity.JsonTreeNode;
import com.springmvc.framework.entity.Menu;
import com.springmvc.framework.entity.Page;
import com.springmvc.framework.entity.PageResultBean;
import com.springmvc.framework.entity.ResultBean;
import com.springmvc.framework.service.SysService;
import com.springmvc.framework.util.MapJsonUtil;
import com.springmvc.framework.util.ParamUtil;
import com.springmvc.framework.util.PrintWriterUtil;
import com.springmvc.framework.util.StringUtils;

@Controller
@RequestMapping("/user") 
public class SysController {
	
	private final static Logger logger = Logger.getLogger(SysController.class);

	@Autowired
	private SysService sysService;
	
	/**
	 * 获取用户列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getUserListPage")
	public void getUserListPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit","userName","userId"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = sysService.getUserListPage(paraMap,page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取用户列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 用户登录
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/login")
	public void login(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			Map<String, Object> paraMap = new HashMap<String, Object>();
			Map<String, Object> userMap = new HashMap<String, Object>();
			HttpSession session = request.getSession();
			String sessionVerifyCode = (String) session.getAttribute("VERIFY_CODE");
			String verifyCode = request.getParameter("verifyCode");
			if (verifyCode == null
					|| !verifyCode.equalsIgnoreCase(sessionVerifyCode)) {
				result.setSuccess(false);
				result.setErr_msg("AUC");
				logger.info(JSONObject.toJSONString(result));
				PrintWriterUtil.write(response, result);
				return;
			}
			
			paraMap.put("loginName",  request.getParameter("loginName"));
			paraMap.put("password", request.getParameter("password"));
			userMap = sysService.getLoginUserInfo(paraMap);
			
			if(userMap != null)
			{
				session.setAttribute("userMap", userMap);
				session.setAttribute("VERIFY_CODE", null);
				result.setSuccess(true);
			} else{
				result.setSuccess(false);
				result.setErr_msg("PWD");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("LGN");
		}
		JSONObject.toJSONString(result);
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取用户Session
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getUserSession")
	public void getUserSession(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			@SuppressWarnings({"rawtypes", "unchecked" })
			Map<String, Object> userMap = (Map)request.getSession().getAttribute("userMap");
			if(userMap != null)
			{
				result.setData(userMap);
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("获取用户Session失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取用户Session发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取一级菜单
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getLevelOneMenuList")
	public void getLevelOneMenuList(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			@SuppressWarnings({"rawtypes", "unchecked" })
			Map<String, Object> userMap = (Map)request.getSession().getAttribute("userMap");
			list = sysService.getLevelOneMenuList(userMap);
			result.setRows(list);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取一级菜单异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取二级或三级菜单
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getLevelTwoMenuList")
	public void getLevelTwoMenuList(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		List<Menu> menuList=new ArrayList<Menu>();
		//获取当前的菜单ID
		String parentID=request.getParameter("trId");
		try {
			writer = response.getWriter();
			@SuppressWarnings({"rawtypes", "unchecked" })
			Map<String, Object> userMap = (Map)request.getSession().getAttribute("userMap");
			menuList = sysService.getLevelTwoMenuList(parentID, userMap);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			writer.write(JSONObject.toJSONString(JsonTreeNode.getJsonTreeNodeList(menuList)));
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 保存角色信息
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveUser")
	public void saveUser(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		String userInfo = request.getParameter("userDataJson");
		@SuppressWarnings("rawtypes")
		Map map = MapJsonUtil.jsonStr2Map(userInfo);
		try {
			// 获取session中的用户信息
			Map<String, Object> userMap = (Map<String, Object>)request.getSession().getAttribute("userMap");
			map.put("CREATE_USER", userMap.get("ID"));
			map.put("CREATE_USER_NAME", userMap.get("USER_NAME"));
			boolean saveResult = sysService.saveUser(map);
			if(saveResult){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("保存不成功。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存角色信息过程发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取角色下拉数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getRoleStore")
	public void getRoleStore(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = sysService.getRoleStore();
			result.setRows(list);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 修改密码
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/updatePassword")
	public void updatePassword(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			String[] fileds = { "userId","NPASSWORD"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			if(sysService.updatePassword(paraMap)){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("修改密码失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("修改密码发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 删除用户
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteUser")
	public void deleteUser(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			String[] fileds = { "ids"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			if(sysService.deleteUser(paraMap)){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("删除用户失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("删除用户发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取按钮权限
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getPermission")
	public void getPermission(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, Object> userMap = (Map)request.getSession().getAttribute("userMap");
		Map<String, Object> paraMap = new HashMap<String, Object>();
		System.out.println("菜单的ID为："+request.getParameter("menuId"));
		try {
			String permissionStr = "";
			list  = sysService.getPermissionList(request.getParameter("menuId"), userMap);
			if(list != null && list.size() > 0)
			{
				for(int i=0;i<list.size();i++)
				{
					permissionStr = permissionStr + "," + String.valueOf(list.get(i).get("ID"));
				}
				permissionStr = permissionStr.substring(1);
			}
			paraMap.put("permission", permissionStr);
			result.setData(paraMap);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 根据角色获取权限树
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getTreeMenuPermissionListByRole")
	public void getTreeMenuPermissionListByRole(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		List<JsonTreeNode> menuList=new ArrayList<JsonTreeNode>();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		String roleId = request.getParameter("roleId");
		paraMap.put("ROLE_ID", roleId);
		try {
			writer = response.getWriter();
			menuList = sysService.getTreeJsonPermissionListByRole(paraMap);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			writer.write(JSONObject.toJSONString(menuList));
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 保存角色权限信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/saveRoleMenuPermission")
	public void saveRoleMenuPermission(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		Map<String, Object> map = new HashMap<String, Object>();
		String roleMenuPermission = request.getParameter("roleMenuPermission");
		String roleId = request.getParameter("roleId");
		try {
			sysService.deleteRoleMenuByRoleId(roleId);
			roleMenuPermission = roleMenuPermission.substring(1);
			String[] roleMenuArr = roleMenuPermission.split(",");
			for(String roleMenu:roleMenuArr)
			{
				map.put("MENU_ID", roleMenu);
				map.put("ROLE_ID", roleId);
				sysService.saveRoleMenu(map);
			}
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存角色权限信息异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取角色列表
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getRoleListPage")
	public void getRoleListPage(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit","roleName","roleId"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = sysService.getRoleListPage(paraMap,page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取角色列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 删除角色
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteRole")
	public void deleteRole(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			String[] fileds = { "ids"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			boolean delResult = sysService.deleteRole(paraMap);
			if(delResult){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("删除不成功。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("删除角色发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 保存角色信息
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveRole")
	public void saveRole(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		String roleInfo = request.getParameter("roleDataJson");
		@SuppressWarnings("rawtypes")
		Map map = MapJsonUtil.jsonStr2Map(roleInfo);
		try {
			// 获取session中的用户信息
			Map<String, Object> userMap = (Map<String, Object>)request.getSession().getAttribute("userMap");
			map.put("CREATE_USER", userMap.get("ID"));
			map.put("CREATE_USER_NAME", userMap.get("USER_NAME"));
			boolean saveResult = sysService.saveRole(map);
			if(saveResult){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("保存不成功。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存角色信息异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取权限树
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getTreeMenuPermissionList")
	public void getTreeMenuPermissionList(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		List<JsonTreeNode> menuList=new ArrayList<JsonTreeNode>();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		try {
			writer = response.getWriter();
			menuList = sysService.getTreeMenuPermissionList(paraMap);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			writer.write(JSONObject.toJSONString(menuList));
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 根据ID删除菜单数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteMenu")
	public void deleteMenu(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			boolean delResult = sysService.deleteMenuById(request.getParameter("MENU_ID"));
			if(delResult){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("删除不成功。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("删除菜单数据异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 保存功能菜单信息
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveMenu")
	public void saveMenu(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		@SuppressWarnings("rawtypes")
		Map map = MapJsonUtil.jsonStr2Map(request.getParameter("menuPermissionDataJson"));
		try {
			// 获取session中的用户信息
			Map<String, Object> userMap = (Map<String, Object>)request.getSession().getAttribute("userMap");
			map.put("CREATE_USER", userMap.get("ID"));
			map.put("CREATE_USER_NAME", userMap.get("USER_NAME"));			
			boolean saveResult = sysService.saveMenu(map);
			if(saveResult){
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("保存不成功。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存功能菜单异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取系统配置列表
	 */
	@RequestMapping(value = "/getSysConfigListPage")
	public void getSysConfigListPage(HttpServletRequest request,HttpServletResponse response){
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit","code","name"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = sysService.getSysConfigListPage(paraMap,page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取系统配置列表异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 保存系统配置
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/saveSysConfigInfo")
	public void saveSysConfigInfo(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			String[] fileds = {"ID","CODE","NAME","VALUE"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			boolean updResult = sysService.saveSysConfigInfo(paraMap);
			if(updResult)
			{
				GlobalConstant.SYS_MAP.put(String.valueOf(paraMap.get("CODE")), String.valueOf(paraMap.get("VALUE")));
				GlobalConstant.initSysConfig();
				logger.info("更新系统缓存["+String.valueOf(paraMap.get("CODE"))+"]成功");
			}
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取数据异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 刷新缓存
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/flushSysConfig")
	public void flushSysConfig(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			GlobalConstant.initSysConfig();
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("刷新缓存发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 查询公共代码列表
	 */
	@RequestMapping(value = "/getPublicCodeListPage")
	public void getPublicCodeListPage(HttpServletRequest request,HttpServletResponse response){
		PageResultBean result = new PageResultBean();
		try {
			String[] fileds = { "start", "limit","name","parentId","code","value"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);

			Page page = new Page();
			List<Map<String, Object>> list = sysService.getPublicCodeListPage(paraMap, page);
			result.setPageResultBean(page.getTotalRow(), page.getPageRow(), list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("查询公共代码列表发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取公共代码名称
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getCodeNameInfo")
	public void getCodeNameInfo(HttpServletRequest request,HttpServletResponse response){
		PageResultBean result = new PageResultBean();
		try{
			List<Map<String, Object>> list = sysService.getCodeNameInfo();
			if("1".equals(request.getParameter("isFirstBlank")))
			{
				Map<String,Object> map= new HashMap<String, Object>();
				map.put("ID", "");
				map.put("TEXT", "　");
				list.add(0, map);
			}
			result.setRows(list);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取公共代码名称异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 保存公共代码信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/savePublicCodeInfo")
	public void savePublicCodeInfo(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			String[] fileds = { "ID","VALUE","CODE","PARENT_ID","ORDERINDEX","NAME","DESC"};
			Map<String, Object> paraMap = ParamUtil.getParamMap(request,fileds);
			boolean isExists = sysService.checkCode(paraMap);
			if(isExists){
				result.setSuccess(false);
				result.setErr_msg("编码已存在。");
			}else{
				boolean saveResult = sysService.savePublicCodeInfo(paraMap);
				if(saveResult){
					result.setSuccess(true);
				}else{
					result.setSuccess(false);
					result.setErr_msg("保存不成功。");
				}
			}
		} catch (Exception e) {
			logger.error("发生异常", e);
			result.setSuccess(false);
			result.setErr_msg("保存公共代码信息异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 删除公共代码信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deletePublicCodeInfo")
	public void deletePublicCodeInfo(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			if (sysService.deletePublicCodeInfo(request.getParameter("codeId"))) {
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("删除公共代码信息错误。");
			}
		} catch (Exception e) {
			logger.error("删除发生异常", e);
			result.setSuccess(false);
			result.setErr_msg("删除公共代码信息异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 根据ID查询公共代码信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/selectPublicCodeInfoById")
	public void selectPublicCodeInfoById(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			String[] fileds = {"ID"};
			Map<String,Object> resultMap = new HashMap<String, Object>();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request,fileds);
			resultMap = sysService.selectPublicCodeInfoById(paraMap);
			result.setData(resultMap);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("查询公共代码信息异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 *  刷新缓存
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/flushBookConfig")
	public void flushBookConfig(HttpServletRequest request,HttpServletResponse response){
		ResultBean result = new ResultBean();
		try {
			GlobalConstant.initSysBook();
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("刷新缓存发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 获取CODE对应名称信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getPubCode")
	public void getPubCode(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		String PUB_CODE_NAME = request.getParameter("PUB_CODE_NAME");
		try {
			List<Map<String, Object>> list = GlobalConstant.SYS_BOOK.get(PUB_CODE_NAME);
			result.setRows(list);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("获取CODE对应名称信息异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 用户登出
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/loginOut")
	public void loginOut(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			HttpSession session = request.getSession();
			session.setAttribute("userMap", null);
			result.setSuccess(true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("用户登出时发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 用户登出
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/childForSelect")
	public void childForSelect(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String parentCode = request.getParameter("parentCode");
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

			if (StringUtils.isNotEmpty(parentCode)) {
				String needBlank = request.getParameter("needBlank");
				if ("1".equals(needBlank)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("CODE", null);
					map.put("VALUE", "请选择...");
					mapList.add(map);
				}

				List<Map<String, Object>> list = sysService.child(parentCode, "0");
				if (list != null && list.size() > 0) {
					for (Map<String, Object> map : list) {
						Map<String, Object> m = new HashMap<String, Object>();
						m.put("CODE", map.get("CODE"));
						m.put("VALUE", map.get("VALUE"));
						mapList.add(m);
					}
				}
			}
			result.setPageResultBean(mapList.size(), mapList.size(), mapList, true);
		}catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("用户登出时发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
}
