package com.springmvc.framework.entity;

import java.util.ArrayList;
import java.util.List;

public class JsonTreeNode {

	public JsonTreeNode() {
		type = "";
	}

	public JsonTreeNode(String id, String text, String parent) {
		type = "";
		this.id = id;
		this.text = text;
		this.parent = parent;
		print = 0;
	}

	/**
	 * 递归遍历树
	 * 
	 * @param listInfo
	 * @return 返回树集合
	 */
	public static List<JsonTreeNode> getJsonTreeNodeList(List<Menu> listInfo) {
		List<JsonTreeNode> arrs = new ArrayList<JsonTreeNode>();

		for (Menu menu : listInfo) {
			JsonTreeNode jtn = new JsonTreeNode();
			jtn.setId(menu.getId());
			jtn.setText(menu.getName());
			jtn.setUrl(menu.getUrl());
			jtn.setExpanded(false);
			jtn.setIconCls(menu.getIcon());
			jtn.setLeaf(menu.getLeaf());
			arrs.add(jtn);
		}
		return arrs;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public String getHref() {
		return href;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getHrefTarget() {
		return hrefTarget;
	}

	public void setHrefTarget(String hrefTarget) {
		this.hrefTarget = hrefTarget;
	}

	public Object getChecked() {
		return checked;
	}

	public void setChecked(Object checked) {
		this.checked = checked;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBusiCode() {
		return busiCode;
	}

	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getIsPop() {
		return isPop;
	}

	public void setIsPop(String isPop) {
		this.isPop = isPop;
	}

	public String getIsInit() {
		return isInit;
	}

	public void setIsInit(String isInit) {
		this.isInit = isInit;
	}

	public String getIsLog() {
		return isLog;
	}

	public void setIsLog(String isLog) {
		this.isLog = isLog;
	}

	public String getContent1() {
		return content1;
	}

	public void setContent1(String content1) {
		this.content1 = content1;
	}

	public String getContent2() {
		return content2;
	}

	public void setContent2(String content2) {
		this.content2 = content2;
	}

	public String getContent3() {
		return content3;
	}

	public void setContent3(String content3) {
		this.content3 = content3;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public int getPrint() {
		return print;
	}

	public void setPrint(int print) {
		this.print = print;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

//	public JSONArray getChildren() {
//		return children;
//	}
//
//	public void setChildren(JSONArray children) {
//		this.children = children;
//	}

	public List<JsonTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<JsonTreeNode> children) {
		this.children = children;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isRealLeaf() {
		return realLeaf;
	}

	public void setRealLeaf(boolean realLeaf) {
		this.realLeaf = realLeaf;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	protected String id;
	protected String text;
	protected String icon;
	protected String iconCls;
	protected boolean leaf;
	protected boolean realLeaf;
	protected boolean expanded;
	protected String href;
	protected String hrefTarget;
	protected Object checked;
	protected boolean disabled;
	protected String isPop;
	protected String isLog;
	protected String busiCode;
	protected String isInit;
	protected String type;
	protected String url;
	protected String systemId;
	protected String content1;
	protected String content2;
	protected String content3;
	protected int print;
	protected String parent;
	//protected JSONArray children;
	protected List<JsonTreeNode> children;
	protected String orderBy;
	protected String level;
	protected String status;
	protected String cdnId;
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCdnId() {
		return cdnId;
	}

	public void setCdnId(String cdnId) {
		this.cdnId = cdnId;
	}
}
