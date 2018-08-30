package com.springmvc.walker.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 优先级线程池队列执行器
 */

public class FixedThreadExecutor {
	private static ThreadPoolExecutor singleExecutorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(8);
	
	private FixedThreadExecutor() {
	}

	public static void execute(Thread t) {
		singleExecutorService.execute(t);
	}
	
}