package com.springmvc.walker.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.framework.entity.JsonTreeNode;
import com.springmvc.framework.util.StringUtils;
import com.springmvc.walker.mapper.file.CategoryMapper;
import com.springmvc.walker.service.CategoryService;


/**
 * 栏目管理
 * 
 * @author Administrator
 * 
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

	private final static Logger logger = Logger
			.getLogger(CategoryServiceImpl.class);

	@Resource
	private CategoryMapper categoryMapper;

	/**
	 * 查询栏目树
	 */
	@Override
	public List<JsonTreeNode> getCategoryTree(String parentId, String status) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<JsonTreeNode> nodeList = new ArrayList<JsonTreeNode>();

		logger.info("获取权限树");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("STATUS", status);
		list = categoryMapper.getCategory(param);
		logger.info("获取权限树SUCCESS,共" + list.size() + "条记录");
		
		Map<String, List<JsonTreeNode>> childsMap = new HashMap<String, List<JsonTreeNode>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			String pId = String.valueOf(map.get("PARENTID"));
			
			JsonTreeNode node = new JsonTreeNode();
			node.setId(String.valueOf(map.get("CATEGORY_ID")));
			node.setText(String.valueOf(map.get("NAME")));
			node.setOrderBy(String.valueOf(map.get("ORDERINDEX")));
			node.setParent(pId);
			node.setContent1(String.valueOf(map.get("DESCRIPTION")));
			
			String FILE_WEB_SERVER = GlobalConstant.SYS_MAP.get(GlobalConstant.FILE_WEB_SERVER);
			String imgUrl = (String) map.get("IMG_URL");
			if (!StringUtils.isEmptyOrBlank(imgUrl)) {
				if (!imgUrl.startsWith("http")) {
					if (imgUrl.startsWith("/")) {
						imgUrl = FILE_WEB_SERVER + imgUrl;
					}
					else {
						imgUrl = FILE_WEB_SERVER + "/" + imgUrl;
					}
				}
			}
			else {
				imgUrl = null;
			}
			node.setContent2(imgUrl);
			
			String iconUrl = (String) map.get("ICON_URL");
			if (!StringUtils.isEmptyOrBlank(iconUrl)) {
				if (!iconUrl.startsWith("http")) {
					if (iconUrl.startsWith("/")) {
						iconUrl = FILE_WEB_SERVER + iconUrl;
					}
					else {
						iconUrl = FILE_WEB_SERVER + "/" + iconUrl;
					}
				}
			}
			else {
				iconUrl = null;
			}
			node.setContent3(iconUrl);
			node.setType(String.valueOf(map.get("TYPE")));
			node.setStatus(String.valueOf(map.get("STATUS")));
			node.setUrl(String.valueOf(map.get("IMG_RESLOUTION")));
			node.setExpanded(false);
			node.setLeaf(false);
			node.setIconCls("");
			
			List<JsonTreeNode> childs = childsMap.get(pId);
			if (!childsMap.containsKey(pId)) {
				childs = new ArrayList<JsonTreeNode>();
			}
			childs.add(node);
			childsMap.put(pId, childs);
		}
		
		nodeList = childsMap.get(parentId);
		if (nodeList != null) {
			for (JsonTreeNode node : nodeList) {
				setChildNodes(node, childsMap);
			}
		}
		return nodeList;
	}
	
	private void setChildNodes(JsonTreeNode node, Map<String, List<JsonTreeNode>> childsMap) {
		List<JsonTreeNode> childs = childsMap.get(node.getId());
		if (childs != null && !childs.isEmpty()) {
			node.setLeaf(false);
			node.setChildren(childs);
			for (JsonTreeNode child : childs) {
				setChildNodes(child, childsMap);
			}
		}
		else {
			node.setLeaf(true);
		}
	}

	/**
	 * 修改栏目数据
	 * 
	 * @param paraMap
	 */
	public void modifyCategory(Map<String, Object> paraMap) {
		logger.info("修改栏目数据信息" + paraMap.toString());
		try {
			String categoryId = (String) paraMap.get("CATEGORY_ID");
			if (StringUtils.isNotEmpty(categoryId)) {
				categoryMapper.updateGategoryById(paraMap);
			} else {
				logger.error("栏目ID不能为空!");
			}
		} catch (Exception e) {
			logger.error("修改栏目发生异常!", e);
		}
	}

	/**
	 * 新增栏目数据
	 * 
	 * @param paraMap
	 */
	public void addCategory(Map<String, Object> paraMap) {
		logger.info("新增栏目数据信息" + paraMap.toString());
		try {
			String categoryId = (String) paraMap.get("CATEGORY_ID");
			if (StringUtils.isNotEmpty(categoryId)) {
				categoryMapper.insertCategory(paraMap);
			} else {
				logger.error("栏目ID不能为空!");
			}
		} catch (Exception e) {
			logger.error("新增栏目发生异常!", e);
		}
	}

	/**
	 * 删除栏目数据
	 * 
	 * @param categoryId
	 */
	public void delCategory(String categoryIds) {
		logger.info("删除栏目" + categoryIds);
		String[] categoryId = categoryIds.replace("'", "").split(",");
		for(String id : categoryId){
			categoryMapper.deleteGategoryById(id);
			categoryMapper.deleteGategoryRef(id);
		}
		logger.info("删除栏目数据成功");
	}

	/**
	 * 修改排序位置
	 * 
	 * @param paraMap
	 */
	public void updateOrderIndex(Map<String, Object> paraMap) {
		String point = paraMap.get("point").toString();
		String sourceParentId = paraMap.get("sourceParentId").toString();
		String targetParentId = paraMap.get("targetParentId").toString();
		String sourceId = paraMap.get("sourceId").toString();
		String targetId = paraMap.get("targetId").toString();
		//源移动到目标上面
		if (point.equals("above")) {
			//同级移动排序
			if (sourceParentId.equals(targetParentId)) {
				Map<String, Object> refMap = new HashMap<String, Object>();
				refMap.put("PARENTID", sourceParentId);
				refMap.put("TARGET_CATEGORY_ID", targetId);
				refMap.put("SOURCE_CATEGORY_ID", sourceId);
				categoryMapper.moveToAbove(refMap);
			}
			else {
				Map<String, Object> refMap = new HashMap<String, Object>();
				refMap.put("TARGET_CATEGORY_ID", targetId);
				refMap.put("SOURCE_CATEGORY_ID", sourceId);
				categoryMapper.moveToOtherAbove(refMap);
			}
		}
		//源移动到目标下面
		else if (point.equals("below")) { 
			//同级移动排序
			if (sourceParentId.equals(targetParentId)) {
				Map<String, Object> refMap = new HashMap<String, Object>();
				refMap.put("PARENTID", sourceParentId);
				refMap.put("TARGET_CATEGORY_ID", targetId);
				refMap.put("SOURCE_CATEGORY_ID", sourceId);
				categoryMapper.moveToBelow(refMap);
			}
			else {
				Map<String, Object> refMap = new HashMap<String, Object>();
				refMap.put("TARGET_CATEGORY_ID", targetId);
				refMap.put("SOURCE_CATEGORY_ID", sourceId);
				categoryMapper.moveToOtherBelow(refMap);
			}
		}
		else if (point.equals("append")) { 
			//节点移动到其他父级
			if (!sourceParentId.equals(targetId)) {
				Map<String, Object> refMap = new HashMap<String, Object>();
				refMap.put("TARGET_PARENTID", targetId);
				refMap.put("SOURCE_CATEGORY_ID", sourceId);
				categoryMapper.moveToOtherParent(refMap);
			}
		}
		//刷新栏目LEVEL_CODE
		//this.baseDao.getSqlMapClientRef().queryForList("tCategory.proc_refresh_category_levelcode");
		logger.info("刷新栏目LEVEL_CODE完成!");
	}

	@Override
	public List<JsonTreeNode> getVodCategoryTree(String parentId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<JsonTreeNode> nodeList = new ArrayList<JsonTreeNode>();

		logger.info("获取权限树");
		list = categoryMapper.getCategory(null);
		logger.info("获取权限树SUCCESS,共" + list.size() + "条记录");
		
		Map<String, List<JsonTreeNode>> childsMap = new HashMap<String, List<JsonTreeNode>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			String pId = String.valueOf(map.get("PARENTID"));
			String status = String.valueOf(map.get("STATUS"));
			
			if (!"01".equals(status)) {
				continue;
			}
			
			JsonTreeNode node = new JsonTreeNode();
			
			node.setId(String.valueOf(map.get("CATEGORY_ID")));
			node.setText(String.valueOf(map.get("NAME")));
			node.setOrderBy(String.valueOf(map.get("ORDERINDEX")));
			node.setParent(pId);
			
			node.setType(String.valueOf(map.get("TYPE")));
			//node.setStatus(status);
			node.setExpanded(false);
			node.setLeaf(false);
			node.setIconCls("");
			
			List<JsonTreeNode> childs = childsMap.get(pId);
			if (!childsMap.containsKey(pId)) {
				childs = new ArrayList<JsonTreeNode>();
			}
			childs.add(node);
			childsMap.put(pId, childs);
		}
		
		nodeList = childsMap.get(parentId);
		if (nodeList != null) {
			for (JsonTreeNode node : nodeList) {
				setChildNodes(node, childsMap);
			}
		}
		return nodeList;
	}

	@Override
	public Map<String, Object> getCategoryByCategoryId(String categoryId) {
		Map<String, Object> category = new HashMap<String, Object>();
		category = categoryMapper.getCategoryByCategoryId(categoryId);
		return category;
	}

}
