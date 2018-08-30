package com.springmvc.framework.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

import com.springmvc.framework.constant.GlobalConstant;

public class ContextListener extends ContextLoaderListener{

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		GlobalConstant.initSysConfig();
		GlobalConstant.initSysBook();
		GlobalConstant.resetTaskState();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
	}
}
