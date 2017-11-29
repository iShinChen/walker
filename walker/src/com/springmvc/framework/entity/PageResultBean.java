package com.springmvc.framework.entity;

import java.util.List;
import java.util.Map;

public class PageResultBean {
	//总行数
	private int total;
	//页数
	private int page;
	//数据
	private List<Map<String, Object>> rows;
	//查询结果
	private  boolean success;
	//错误信息
	private String err_msg;

	public boolean isSuccess() {
		return success;
	}

	public String getErr_msg() {
		return err_msg;
	}

	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}


	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public void setPageResultBean(int total,int page,List<Map<String, Object>> rows,boolean success){
		this.total = total;
		this.page = page;
		this.rows = rows;
		this.success = success;		
	}

	public List<Map<String, Object>> getRows() {
		return rows;
	}

	public void setRows(List<Map<String, Object>> rows) {
		this.rows = rows;
	}
}
