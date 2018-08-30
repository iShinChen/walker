package com.springmvc.framework.domain;

public class FtpServer {
	private String host;

	private int port = 21;

	private String username;

	private String password;

	private String fileDir;

	private FtpServer() {
		super();
	}
	
	public FtpServer(String host, int port, String username, String password) {
		this();
		this.host = host;
		if (port > 0) {
			this.port = port;
		}
		this.username = username;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

}
