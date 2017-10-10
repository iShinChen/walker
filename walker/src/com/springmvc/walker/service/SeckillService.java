package com.springmvc.walker.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.springmvc.walker.entity.Seckill;

public interface SeckillService {

	public int reduceNumber(long seckillId,Date killTime);
	
	public Seckill queryById(long seckilled);
	
	List<Seckill>  queryAll(int offset,int limit);
	
	public void seckillByProcedure(Map<String, Object> paramMap);
}
