package com.springmvc.walker.entity;

import java.util.List;

public class Menu {

	/**
	 * 菜单ID
	 */
	private String id ;

	/**
	 * 菜单编码
	 */
	private String code ;

	/**
	 * 菜单名称
	 */
	private String name ;
	
	private String text ;

	/**
	 * 菜单链接地址
	 */
	private String url ;

	/**
	 * 菜单级别
	 */
	private String level ;

	/**
	 * 父菜单ID
	 */
	private String parentId ;

	/**
	 * 删除标志（1可用  0已删除）
	 */
	private String deleteFlag ;

	/**
	 * createTime
	 */
	private String createTime ;
	
	/**
	 * 菜单图标
	 */
	private String icon ;
	
	private String iconCls ;
	
	/**
	 * 是否为叶子结点
	 */
	private boolean leaf ;
	
	/**
	 * 复选框
	 */
	private boolean checked;
	
	/**
	 * 子节点
	 */
	private List<Menu> children;

	/**
	 * 取得菜单ID
	 *
	 * @return 菜单ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 设置菜单ID
	 *
	 * @param 新的菜单ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 取得菜单编码
	 *
	 * @return 菜单编码
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * 设置菜单编码
	 *
	 * @param 新的菜单编码
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 取得菜单名称
	 *
	 * @return 菜单名称
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 设置菜单名称
	 *
	 * @param 新的菜单名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 取得菜单链接地址
	 *
	 * @return 菜单链接地址
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * 设置菜单链接地址
	 *
	 * @param 新的菜单链接地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 取得菜单级别
	 *
	 * @return 菜单级别
	 */
	public String getLevel() {
		return this.level;
	}

	/**
	 * 设置菜单级别
	 *
	 * @param 新的菜单级别
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * 取得父菜单ID
	 *
	 * @return 父菜单ID
	 */
	public String getParentId() {
		return this.parentId;
	}

	/**
	 * 设置父菜单ID
	 *
	 * @param 新的父菜单ID
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * 取得删除标志（1可用  0已删除）
	 *
	 * @return 删除标志（1可用  0已删除）
	 */
	public String getDeleteFlag() {
		return this.deleteFlag;
	}

	/**
	 * 设置删除标志（1可用  0已删除）
	 *
	 * @param 新的删除标志（1可用  0已删除）
	 */
	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	/**
	 * 取得createTime
	 *
	 * @return createTime
	 */
	public String getCreateTime() {
		return this.createTime;
	}

	/**
	 * 设置createTime
	 *
	 * @param 新的createTime
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * 取得icon
	 *
	 * @return icon
	 */
	public String getIcon() {
		return icon;
	}
	
	/**
	 * 设置icon
	 *
	 * @param 新的icon
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	
	public boolean getChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	/**
	 * 调试用的方法， 可以将所有字段的数据输出
	 *
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:		" + id + "\n");
		sb.append("code:		" + code + "\n");
		sb.append("name:		" + name + "\n");
		sb.append("url:		" + url + "\n");
		sb.append("level:		" + level + "\n");
		sb.append("parentId:		" + parentId + "\n");
		sb.append("deleteFlag:		" + deleteFlag + "\n");
		sb.append("createTime:		" + createTime + "\n");
		return sb.toString();
	}

}
