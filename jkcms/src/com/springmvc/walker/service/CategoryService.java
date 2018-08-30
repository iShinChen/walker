package com.springmvc.walker.service;

import java.util.List;
import java.util.Map;

import com.springmvc.framework.entity.JsonTreeNode;


/**
 * 栏目管理
 * 
 * @author Administrator
 * 
 */

public interface CategoryService {

	/**
	 * 查询栏目树
	 * @param parentId
	 * @return
	 */
	public List<JsonTreeNode> getCategoryTree(String parentId, String status);
	
	/**
	 * 修改栏目数据
	 * @param paraMap
	 */
	public void modifyCategory(Map<String, Object> paraMap);
	
	/**
	 * 新增栏目数据
	 * @param paraMap
	 */
	public void addCategory(Map<String, Object> paraMap);
	
	/**
	 * 删除栏目数据
	 * @param categoryId
	 */
	public void delCategory(String categoryId);
	
	/**
	 * 修改排序位置
	 * @param paraMap
	 */
	public void updateOrderIndex(Map<String, Object> paraMap);

	public List<JsonTreeNode> getVodCategoryTree(String parentId);
	
	public Map<String, Object> getCategoryByCategoryId(String categoryId);
	
}
