package com.springmvc.walker.service;

import com.springmvc.walker.entity.SuccessKill;

public interface SuccessKillService {

	 int insertSuccessKill(long seckillId,long userPhone);
	 
	 SuccessKill  queryByIdWithSeckill(long seckillId,long userPhone);
	 
}
