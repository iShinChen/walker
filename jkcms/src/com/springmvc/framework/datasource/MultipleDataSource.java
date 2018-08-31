package com.springmvc.framework.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 多数据源切换实现
 * @author shin chen
 *
 */
public class MultipleDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
    	String dbType = DBContextHolder.getDBType();
    	if (dbType == null) {
    		logger.info("数据源:" + DBContextHolder.MASTER_DATA_SOURCE + "[默认]");
		}
    	else {
    		logger.info("数据源:" + dbType);
    	}
        return dbType;
    }
}
