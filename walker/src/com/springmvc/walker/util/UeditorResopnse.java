package com.springmvc.walker.util;

public class UeditorResopnse {

	private String state;
	private String url;
	private String title;
	private String original;
	
	public UeditorResopnse(){
		this.state = "SUCCESS";
	}
	
	public UeditorResopnse(String title,String url,  String original) {
		super();
		this.title = title;
		this.url = url;
		this.original = original;
		this.state = "SUCCESS";
	}



	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOriginal() {
		return original;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	
	
}
