package com.springmvc.framework.datasource;


/**
 * 数据源AOP自动切换
 * 
 * @author shin chen
 * 
 */
public class MultipleDataSourceAdvice {
	public void setMasterDataSource() {
		DBContextHolder.setDBType(DBContextHolder.MASTER_DATA_SOURCE);
	}

	public void setSlaveDataSource() {
		DBContextHolder.setDBType(DBContextHolder.SLAVE_DATA_SOURCE);
	}

}
