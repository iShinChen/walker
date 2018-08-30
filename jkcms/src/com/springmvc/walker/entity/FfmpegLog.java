package com.springmvc.walker.entity;

import java.util.ArrayList;
import java.util.List;

public class FfmpegLog {
	
	private String customerId;
	private String taskId;
	private String logUrl;
	private List<String> list;
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getLogUrl() {
		return logUrl;
	}
	public void setLogUrl(String logUrl) {
		this.logUrl = logUrl;
	}
	public List<String> getList() {
		if(list == null) list = new ArrayList<String>();
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}

}
