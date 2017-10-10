package com.springmvc.walker.util;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class UUIDUtil {
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	private static CountDownLatch COUNTDOWNLATCH_5LEN = new CountDownLatch(99999);
	
	public static String getNextCountDown() {
		long countDown = COUNTDOWNLATCH_5LEN.getCount();
		COUNTDOWNLATCH_5LEN.countDown();

		if (COUNTDOWNLATCH_5LEN.getCount() == 0) {
			COUNTDOWNLATCH_5LEN = new CountDownLatch(99999);
		}
		if (countDown < 10) {
			return "0000" + countDown;
		}
		else if (countDown < 100) {
			return "000" + countDown;
		}
		else if (countDown < 1000) {
			return "00" + countDown;
		}
		else if (countDown < 10000) {
			return "0" + countDown;
		}
		return String.valueOf(countDown);
	}
}
