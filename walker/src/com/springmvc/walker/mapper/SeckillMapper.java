package com.springmvc.walker.mapper;

import java.util.List;
import java.util.Map;

import com.springmvc.walker.entity.Seckill;
import com.springmvc.walker.entity.SuccessKill;

public interface SeckillMapper {

	public int reduceNumber(Map<String, Object> paraMap);
	
	public Seckill queryById(String seckillId);
	
	public List<Seckill> queryAll(Map<String, Object> paraMap);
	
	public int insertSuccessKill(Map<String, Object> paraMap);
	
	public SuccessKill queryByIdWithSeckill(Map<String, Object> paraMap);
	
}
