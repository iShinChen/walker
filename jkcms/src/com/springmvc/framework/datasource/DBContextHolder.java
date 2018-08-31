package com.springmvc.framework.datasource;

/**
 * @author shin chen
 *
 */
public class DBContextHolder {
	public static final String MASTER_DATA_SOURCE = "masterDataSource";
	public static final String SLAVE_DATA_SOURCE = "slaveDataSource";
	
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

	public static void setDBType(String dbType) {
		contextHolder.set(dbType);
	}

	public static String getDBType() {
		return contextHolder.get();
	}

	public static void clearDBType() {
		contextHolder.remove();
	}

}
