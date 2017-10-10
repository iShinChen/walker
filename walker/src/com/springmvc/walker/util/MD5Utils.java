package com.springmvc.walker.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	public static char hexDigits[] = { 'F', '0', '6', 'A', '1', '8', 'C', '2',
			'9', '3', 'B', '4', '5', 'D', '7', 'E' };

	/**
	 * 加密
	 * 
	 * @param s
	 * @return
	 */
	public static String md5s(String s) {
		try {

			byte[] btInput = s.getBytes();

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(btInput);// 使用指定的字节数组更新摘要。
			byte[] mdByte = md.digest();// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算。
			int mdLength = mdByte.length;
			char str[] = new char[mdLength * 2];
			int k = 0;
			for (int i = 0; i < mdLength; i++) {
				str[k++] = hexDigits[mdByte[i] >>> 4 & 0xf];
				str[k++] = hexDigits[mdByte[i] & 0xf];
			}
			return new String(str);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	// MD5加码,32位
	public static String MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();

		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}

	// 可逆的加密算法
	public static String KL(String inStr) {
		// String s = new String(inStr);
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String s = new String(a);
		return s;
	}

	// 加密后解密
	public static String JM(String inStr) {
		char[] a = inStr.toCharArray();
		for (int i = 0; i < a.length; i++) {
			a[i] = (char) (a[i] ^ 't');
		}
		String k = new String(a);
		return k;
	}
}
