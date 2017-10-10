package com.springmvc.walker.entity;

public class TSysConfig {
	
	private String id;
	private String code;
	private String value;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "TSysConfig [code=" + code + ", id=" + id + ", value=" + value
				+ "]";
	}

}
