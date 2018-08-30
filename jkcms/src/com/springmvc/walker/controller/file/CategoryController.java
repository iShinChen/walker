package com.springmvc.walker.controller.file;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.framework.entity.JsonTreeNode;
import com.springmvc.framework.util.MapJsonUtil;
import com.springmvc.framework.util.StringUtils;
import com.springmvc.framework.util.UUIDUtil;
import com.springmvc.walker.service.CategoryService;

@Controller
@RequestMapping("/category") 
public class CategoryController {

	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(CategoryController.class);

	@Autowired
	private CategoryService categoryService;

	/**
	 * 查询栏目树
	 */
	@RequestMapping(value = "/getCategoryTree")
	public void getCategoryTree(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		List<JsonTreeNode> menuList = new ArrayList<JsonTreeNode>();
		String parentId = request.getParameter("parentId");
		if (parentId == null) {
			parentId = "0";
		}
		try {
			writer = response.getWriter();
			menuList = categoryService.getCategoryTree(parentId, "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.write(JSONObject.toJSONString(menuList));
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 查询已上线栏目树
	 */
	@RequestMapping(value = "/getOnLineCategoryTree")
	public void getOnLineCategoryTree(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		List<JsonTreeNode> menuList = new ArrayList<JsonTreeNode>();
		String parentId =  request.getParameter("parentId");
		if (parentId == null) {
			parentId = "0";
		}
		try {
			writer = response.getWriter();
			menuList = categoryService.getCategoryTree(parentId, "01");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.write(JSONObject.toJSONString(menuList));
			writer.flush();
			writer.close();
		}
	}

	/**
	 * 保存栏目
	 */
	@RequestMapping(value = "/saveCategory")
	@SuppressWarnings("unchecked")
	public void saveCategory(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		JSONObject json = new JSONObject();
		String jsonParams = request.getParameter("jsonParams");
		Map map = MapJsonUtil.jsonStr2Map(jsonParams);
		try {
			writer = response.getWriter();
			String categoryId = (String) map.get("CATEGORY_ID");
			if (StringUtils.isNotEmpty(categoryId)) {
				categoryService.modifyCategory(map);
			}
			else {
				categoryId = UUIDUtil.getUUID();
				map.put("CATEGORY_ID", categoryId);
				categoryService.addCategory(map);
			}
			json.put("success", "success");
			json.put("CATEGORY_ID", categoryId);
			Map<String, Object> category = categoryService.getCategoryByCategoryId(categoryId);
			if(null!= category && !category.isEmpty()){
				json.put("CDN_ID", category.get("CDN_ID").toString());
			}else{
				json.put("CDN_ID", "");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			json.put("error", e.getMessage());
		} finally {
			writer.write(json.toString());
			writer.flush();
			writer.close();
		}
	}

	/**
	 * 删除栏目
	 */
	@RequestMapping(value = "/delCategory")
	public void delCategory(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		JSONObject json = new JSONObject();
		String categoryId = request.getParameter("categoryId");
		try {
			writer = response.getWriter();
			categoryService.delCategory(categoryId);
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
	 * 修改排序位置
	 * 
	 * @param paraMap
	 */
	@RequestMapping(value = "/updateOrderIndex")
	public void updateOrderIndex(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		JSONObject json = new JSONObject();
		String[] fileds = { "sourceParentId", "targetParentId", "sourceId", "targetId", "point" };
		try {
			Map<String, Object> paraMap = getParamMap(request, fileds);
			writer = response.getWriter();
			categoryService.updateOrderIndex(paraMap);
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
	 * 更新图片
	 */
	@RequestMapping(value = "/updateImage")
	public void updateImage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		JSONObject json = new JSONObject();
		String[] fileds = { "CATEGORY_ID", "IMG_URL", "IMG_RESLOUTION" };
		try {
			Map<String, Object> paraMap = getParamMap(request, fileds);
			writer = response.getWriter();
			categoryService.modifyCategory(paraMap);
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
	 * 获取相关请求参数
	 * 
	 * @param request
	 *            系统获得
	 * @param fields
	 *            参数字段名数组
	 * @return Map<String,String> 参数集合
	 */
	public Map<String, Object> getParamMap(HttpServletRequest request, String[] fields) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (int i = 0; i < fields.length; i++) {
			if (!paramMap.containsKey(fields[i])) {
				paramMap.put(fields[i], getStringParameter(request.getParameter(fields[i])));
			}
		}
		return paramMap;
	}

	/**
	 * 获取一个String的参数，如果找不到则返回null
	 * 
	 * @param name
	 * @return String
	 */
	public String getStringParameter(String name) {
		if (name == null) {
			return "";
		}
		return name;
	}
}
