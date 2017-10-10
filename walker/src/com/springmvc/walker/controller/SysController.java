package com.springmvc.walker.controller;

import java.io.IOException;
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
import com.springmvc.walker.constant.GlobalConstant;
import com.springmvc.walker.entity.JsonTreeNode;
import com.springmvc.walker.entity.Menu;
import com.springmvc.walker.entity.Page;
import com.springmvc.walker.service.SysService;
import com.springmvc.walker.util.MapJsonUtil;
import com.springmvc.walker.util.ParamUtil;

@Controller
@RequestMapping("/user") 
public class SysController {
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(SysController.class);

	@Autowired
	private SysService sysService;
	
	/**
	 * 获取用户列表
	 */
	@RequestMapping(value = "/getUserListPage")
	public void getUserListPage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		String[] fileds = { "start", "limit","userName","userId"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = sysService.getUserListPage(paraMap,page);
			jsonMap.put("total",page.getTotalRow());
			jsonMap.put("rows",list);
			jsonMap.put("page",page.getPageRow());
			jsonMap.put("success", true);
			
		} catch (Exception e) {
			logger.error(e);
			jsonMap.put("success", false);
		}
		
		writer.write(JSONObject.toJSONString(jsonMap));
		writer.flush();
		writer.close();
	}
	
	/**
	 * 用户登录
	 * @return
	 */
	@RequestMapping(value = "/login")
	public void login(HttpServletRequest request,HttpServletResponse response) {
		
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = new HashMap<String, Object>();
			Map<String, Object> userMap = new HashMap<String, Object>();
			
			paraMap.put("loginName",  request.getParameter("loginName"));
			paraMap.put("password", request.getParameter("password"));
			userMap = sysService.getLoginUserInfo(paraMap);
			
			if(userMap != null)
			{
				request.getSession().setAttribute("userMap", userMap);
				json.put("success", true);
			} else{
				json.put("success", false);
				json.put("errorMSG", "Can not get Login UserMap");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	@RequestMapping(value = "/getUserSession")
	public void getUserSession(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		try {
			writer = response.getWriter();
			Map<String, Object> userMap = (Map)request.getSession().getAttribute("userMap");
			if(userMap != null)
			{
				json.put("ROLE_NAME", userMap.get("ROLE_NAME"));
				json.put("USER_NAME", userMap.get("USER_NAME"));
				json.put("success", true);
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			json.put("success", false);
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 获取一级菜单
	 */
	@RequestMapping(value = "/getLevelOneMenuList")
	public void getLevelOneMenuList(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			writer = response.getWriter();
			Map<String, Object> userMap = (Map)request.getSession().getAttribute("userMap");
			list = sysService.getLevelOneMenuList(userMap);
			
		} catch (Exception e) {
			logger.error(e);
		} finally {
			writer.write(JSONObject.toJSONString(list));
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 获取二级或三级菜单
	 */
	@RequestMapping(value = "/getLevelTwoMenuList")
	public void getLevelTwoMenuList(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		List<Menu> menuList=new ArrayList<Menu>();
		//获取当前的菜单ID
		String parentID=request.getParameter("trId");
		try {
			writer = response.getWriter();
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
	 */
	@RequestMapping(value = "/saveUser")
	public void saveUser(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		
		String userInfo = request.getParameter("userDataJson");
		Map map = MapJsonUtil.jsonStr2Map(userInfo);
		try {
			writer = response.getWriter();
			
			// 获取session中的用户信息
			Map<String, Object> userMap = (Map<String, Object>)request.getSession().getAttribute("userMap");
			map.put("CREATE_USER", userMap.get("ID"));
			map.put("CREATE_USER_NAME", userMap.get("USER_NAME"));
						
			sysService.saveUser(map);
			json.put("success", "success");
		} catch (Exception e) {
			logger.error("程序异常", e);
			json.put("error", e);
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 获取角色下拉数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getRoleStore")
	public void getRoleStore(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			writer = response.getWriter();
			list = sysService.getRoleStore();
			json.put("rows", list);
		} catch (Exception e) {
			logger.error("程序异常", e);
			json.put("error", e);
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 修改密码
	 */
	@RequestMapping(value = "/updatePassword")
	public void updatePassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject obj = null;
		String[] fileds = { "userId","NPASSWORD"};
		
		PrintWriter writer=null;
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			obj = sysService.updatePassword(paraMap);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer!=null){
				writer.write(obj.toString());
				writer.flush();
				writer.close();
			}
		}
	}
	
	/**
	 * 删除用户
	 */
	@RequestMapping(value = "/deleteUser")
	public void deleteUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject obj = null;
		String[] fileds = { "ids"};
		
		PrintWriter writer=null;
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			obj = sysService.deleteUser(paraMap);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer!=null){
				writer.write(obj.toString());
				writer.flush();
				writer.close();
			}
		}
	}
	
	@RequestMapping(value = "/getPermission")
	public void getPermission(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> userMap = (Map)request.getSession().getAttribute("userMap");
		String menuId = request.getParameter("menuId");
		System.out.println("菜单的ID为："+menuId);
		try {
			writer=response.getWriter();
			String permissionStr = "";
			list  = sysService.getPermissionList(menuId, userMap);
			
			if(list != null && list.size() > 0)
			{
				for(int i=0;i<list.size();i++)
				{
					permissionStr = permissionStr + "," + String.valueOf(list.get(i).get("ID"));
				}
				permissionStr = permissionStr.substring(1);
			}
			json.put("permission", permissionStr);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
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
	
	@RequestMapping(value = "/saveRoleMenuPermission")
	public void saveRoleMenuPermission(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		String roleMenuPermission = request.getParameter("roleMenuPermission");
		String roleId = request.getParameter("roleId");
		try {
			writer = response.getWriter();
			sysService.deleteRoleMenuByRoleId(roleId);
			roleMenuPermission = roleMenuPermission.substring(1);
			String[] roleMenuArr = roleMenuPermission.split(",");
			for(String roleMenu:roleMenuArr)
			{
				map.put("MENU_ID", roleMenu);
				map.put("ROLE_ID", roleId);
				sysService.saveRoleMenu(map);
			}
			json.put("success", "success");
		} catch (Exception e) {
			logger.error("程序异常", e);
			json.put("error", e);
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 获取角色列表
	 */
	@RequestMapping(value = "/getRoleListPage")
	public void getRoleListPage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		String[] fileds = { "start", "limit","roleName","roleId"};
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = sysService.getRoleListPage(paraMap,page);
			jsonMap.put("total",page.getTotalRow());
			jsonMap.put("rows",list);
			jsonMap.put("page",page.getPageRow());
			jsonMap.put("success", true);
			
		} catch (Exception e) {
			logger.error(e);
			jsonMap.put("success", false);
		}
		
		writer.write(JSONObject.toJSONString(jsonMap));
		writer.flush();
		writer.close();
	}
	
	/**
	 * 删除角色
	 */
	@RequestMapping(value = "/deleteRole")
	public void deleteRole(HttpServletRequest request,HttpServletResponse response){
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		try {
			writer = response.getWriter();
			
			String[] fileds = { "ids"};
			
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			sysService.deleteRole(paraMap);
			
			json.put("success", "success");
		} catch (Exception e) {
			logger.error("程序异常", e);
			json.put("error", e);
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	@RequestMapping(value = "/saveRole")
	public void saveRole(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		
		String roleInfo = request.getParameter("roleDataJson");
		Map map = MapJsonUtil.jsonStr2Map(roleInfo);
		try {
			writer = response.getWriter();
			
			// 获取session中的用户信息
			Map<String, Object> userMap = (Map<String, Object>)request.getSession().getAttribute("userMap");
			map.put("CREATE_USER", userMap.get("ID"));
			map.put("CREATE_USER_NAME", userMap.get("USER_NAME"));
			
			sysService.saveRole(map);
			json.put("success", "success");
		} catch (Exception e) {
			logger.error("程序异常", e);
			json.put("error", e);
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
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
	
	@RequestMapping(value = "/deleteMenu")
	public void deleteMenu(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		String menuId = request.getParameter("MENU_ID");
		try {
			writer=response.getWriter();
			sysService.deleteMenuById(menuId);
			json.put("success", "success");
		} catch (Exception e) {
			logger.error("程序异常", e);
			json.put("success", "fail");
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	@RequestMapping(value = "/saveMenu")
	public void saveMenu(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		String menuInfo = request.getParameter("menuPermissionDataJson");
		Map map = MapJsonUtil.jsonStr2Map(menuInfo);
		try {
			writer=response.getWriter();
			
			// 获取session中的用户信息
			Map<String, Object> userMap = (Map<String, Object>)request.getSession().getAttribute("userMap");
			map.put("CREATE_USER", userMap.get("ID"));
			map.put("CREATE_USER_NAME", userMap.get("USER_NAME"));
						
			sysService.saveMenu(map);
			json.put("success", "success");
		} catch (Exception e) {
			logger.error("程序异常", e);
			json.put("success", "fail");
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 获取系统配置列表
	 */
	@RequestMapping(value = "/getSysConfigListPage")
	public void getSysConfigListPage(HttpServletRequest request,HttpServletResponse response){
		PrintWriter writer = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		String[] fileds = { "start", "limit","code","name"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = sysService.getSysConfigListPage(paraMap,page);
			
			jsonMap.put("total",page.getTotalRow());
			jsonMap.put("rows",list);
			jsonMap.put("page",page.getPageRow());
			jsonMap.put("success", true);
			
		} catch (Exception e) {
			logger.error(e);
			jsonMap.put("success", false);
		}
		
		writer.write(JSONObject.toJSONString(jsonMap));
		writer.flush();
		writer.close();
	}
	
	/**
	 * 保存系统配置
	 */
	@RequestMapping(value = "/saveSysConfigInfo")
	public void saveSysConfigInfo(HttpServletRequest request,HttpServletResponse response){
		PrintWriter writer = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		String[] fileds = {"ID","CODE","NAME","VALUE"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			int updateCount = sysService.saveSysConfigInfo(paraMap);
			if(updateCount > 0)
			{
				GlobalConstant.SYS_MAP.put(String.valueOf(paraMap.get("CODE")), String.valueOf(paraMap.get("VALUE")));
				logger.info("更新系统缓存["+String.valueOf(paraMap.get("CODE"))+"]成功");
			}
			
			jsonMap.put("success", true);
			
		} catch (Exception e) {
			logger.error(e);
			jsonMap.put("success", false);
		}
		
		writer.write(JSONObject.toJSONString(jsonMap));
		writer.flush();
		writer.close();
	}
	
	/**
	 * 刷新缓存
	 */
	@RequestMapping(value = "/flushSysConfig")
	public void flushSysConfig(HttpServletRequest request,HttpServletResponse response){
		PrintWriter writer = null;
		JSONObject jsonObj = new JSONObject();
		try {
			writer = response.getWriter();

			GlobalConstant.initSysConfig();
			
			jsonObj.put("success", true);
			
		} catch (Exception e) {
			logger.error(e);
			jsonObj.put("success", false);
		}
		
		writer.write(jsonObj.toJSONString());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 查询公共代码列表
	 */
	@RequestMapping(value = "/getPublicCodeListPage")
	public void getPublicCodeListPage(HttpServletRequest request,HttpServletResponse response){
		PrintWriter writer = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		String[] fileds = { "start", "limit","name","parentId","code","value"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);

			Page page = new Page();
			List<Map<String, Object>> list = sysService.getPublicCodeListPage(paraMap, page);
			jsonMap.put("total", page.getTotalRow());
			jsonMap.put("rows", list);
			jsonMap.put("page", page.getPageRow());
			jsonMap.put("success", true);

		} catch (Exception e) {
			logger.error("查询公共代码列表 发生异常",e);
			jsonMap.put("success", false);
		}
		writer.write(JSONObject.toJSONString(jsonMap));
		writer.flush();
		writer.close();
	}
	
	/**
	 * 获取公共代码名称
	 * */
	@RequestMapping(value = "/getCodeNameInfo")
	public void getCodeNameInfo(HttpServletRequest request,HttpServletResponse response){
		PrintWriter writer = null;
		JSONObject jsonObj = new JSONObject();
		String isFirstBlank = request.getParameter("isFirstBlank");
		try{
			writer = response.getWriter();
			List<Map<String, Object>> list = sysService.getCodeNameInfo();
			if("1".equals(isFirstBlank))
			{
				Map<String,Object> map= new HashMap<String, Object>();
				map.put("ID", "");
				map.put("TEXT", "　");
				list.add(0, map);
			}
			jsonObj.put("rows", list);
		}catch(Exception e){
			logger.error("获取所属类型 异常", e);
			jsonObj.put("error", e);
		}
		writer.write(jsonObj.toJSONString());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 保存公共代码信息
	 */
	@RequestMapping(value = "/savePublicCodeInfo")
	public void savePublicCodeInfo(HttpServletRequest request,HttpServletResponse response){
		PrintWriter writer = null;
		JSONObject obj = new JSONObject();
		
		String[] fileds = { "ID","VALUE","CODE","PARENT_ID","ORDERINDEX","NAME","DESC"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request,fileds);
			
			boolean isExists = sysService.checkCode(paraMap);
			if(isExists){
				obj.put("desc", "编码已存在!");
			}else{
				sysService.savePublicCodeInfo(paraMap);
				obj.put("success", true);
			}
		} catch (Exception e) {
			logger.error("保存发生异常", e);
			obj.put("success", false);
		}
		writer.write(obj.toJSONString());
		writer.flush();
		writer.close();
	}
	/**
	 * 删除公共代码信息
	 */
	@RequestMapping(value = "/deletePublicCodeInfo")
	public void deletePublicCodeInfo(HttpServletRequest request,HttpServletResponse response){
		JSONObject obj = new JSONObject();
		PrintWriter writer = null;
		obj.put("success", false);
		String codeId = request.getParameter("codeId");
		
		try {
			writer = response.getWriter();
			boolean success = sysService.deletePublicCodeInfo(codeId);
			if (success) {
				obj.put("success", true);
			}
		} catch (IOException e) {
			logger.error("删除发生异常", e);
		}
		writer.write(obj.toJSONString());
		writer.flush();
		writer.close();
	}
	/**
	 * 根据ID查询公共代码信息
	 */
	@RequestMapping(value = "/selectPublicCodeInfoById")
	public void selectPublicCodeInfoById(HttpServletRequest request,HttpServletResponse response){
		JSONObject obj = new JSONObject();
		PrintWriter writer = null;
		obj.put("success", false);
		String[] fileds = { "ID"};
		try {
			writer = response.getWriter();
			Map<String,Object> result = new HashMap<String, Object>();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request,fileds);
			result = sysService.selectPublicCodeInfoById(paraMap);
			obj.put("success", true);
			obj.put("data", result);
		} catch (IOException e) {
			logger.error("查询发生异常", e);
		}
		writer.write(obj.toJSONString());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 刷新缓存
	 */
	@RequestMapping(value = "/flushBookConfig")
	public void flushBookConfig(HttpServletRequest request,HttpServletResponse response){
		PrintWriter writer = null;
		JSONObject jsonObj = new JSONObject();
		try {
			writer = response.getWriter();
			GlobalConstant.initSysBook();
			jsonObj.put("success", true);
		} catch (Exception e) {
			logger.error("刷新缓存 发生异常",e);
			jsonObj.put("success", false);
		}
		writer.write(jsonObj.toJSONString());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 获取CODE对应名称信息
	 */
	@RequestMapping(value = "/getPubCode")
	public void getPubCode(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		String PUB_CODE_NAME = request.getParameter("PUB_CODE_NAME");
		JSONObject jsonObj = new JSONObject();
		try {
			writer = response.getWriter();
			List<Map<String, Object>> list = GlobalConstant.SYS_BOOK.get(PUB_CODE_NAME);
			jsonObj.put("success", true);
			jsonObj.put("data", list);
		} catch (Exception e) {
			logger.error("获取公共字典数据发生异常");
			jsonObj.put("success", false);
		}
		writer.write(jsonObj.toJSONString());
		writer.flush();
		writer.close();
	}
	
	/**
	 * 用户登出
	 * @return
	 */
	@RequestMapping(value = "/loginOut")
	public void loginOut(HttpServletRequest request,HttpServletResponse response) {
		
		PrintWriter writer=null;
		JSONObject json=new JSONObject();
		json.put("success", false);
		try {
			writer = response.getWriter();
			
			HttpSession session = request.getSession();
			session.setAttribute("userMap", null);
			json.put("success", true);
		} catch (Exception e) {
			logger.error("程序异常", e);
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}
}
