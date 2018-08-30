package com.springmvc.walker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class HttpFileUtil {
	
	private final static Logger logger = Logger.getLogger(HttpFileUtil.class);
	
	public static void main(String[] args) {
		String u = "http://121.46.19.90/m3u8?&start=245.319&end=274.4&k=hWODtfkIlB6XWOcORYcbom63yMP3TEW1bB6NoVKG0JvbPTdF5m47fFo70F2twmfVZDeHfDXswm6AZDAXRhAS0psdyF2sWDo70ScAZMc20w&a=hWqbzHsWsU5COkquOfkIWh1tfh6swmNCNF2OfGbsRYdXwm1mRD6S0MEAZMX2gLsSoMAARDx&sig=tQkDl8rT1tzsK43Kn-B8q2jXolLm5B6v";
		System.out.println(download(u, "D:/test01.ts"));
	}
	
	public static boolean download(String httpUrl, String saveFile) {
		int byteread = 0;

		URL url = null;
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e) {
			logger.error("HTTP远程文件【" + httpUrl + "】地址错误!" + e);
			return false;
		}
		
		FileOutputStream fs = null;
		InputStream inStream = null;

		try {
			URLConnection conn = url.openConnection();
			inStream = conn.getInputStream();

			File file = new File(saveFile);
			if (file.exists()) {
				file.delete();
			} else {
				String path = saveFile.substring(0, saveFile.lastIndexOf("/"));
				new File(path).mkdirs();
			}
			
			fs = new FileOutputStream(saveFile);

			byte[] buffer = new byte[1024];
			
			int i = 0;
			int mb = 0;
			while ((byteread = inStream.read(buffer)) != -1) {
				i++;
				mb = i / (1024);
				if (i % (1024 * 10) == 0) {
					logger.info("HTTP远程文件【" + httpUrl + "】下载中(" + mb + "MB)...");
				}
				
				fs.write(buffer, 0, byteread);
			}

			logger.info("成功下载HTTP远程文件【" + httpUrl + "】,共" + mb + "MB!");
			return true;
		} catch (FileNotFoundException e) {
			logger.error("HTTP远程文件【" + httpUrl + "】不存在!" + e);
			return false;
		} catch (IOException e) {
			logger.error("HTTP远程文件【" + httpUrl + "】下载IO异常!" + e);
			return false;
		}
		finally{
			try {
				fs.flush();
				fs.close();
			} catch (Exception e2) {
			}
			try {
				inStream.close();
			} catch (Exception e2) {
			}
		}
	}
}
