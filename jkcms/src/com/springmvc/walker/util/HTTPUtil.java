package com.springmvc.walker.util;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

public class HTTPUtil {
	private static final Logger logger = Logger.getLogger(HTTPUtil.class);
	
	public static String httpGet(String url, String parameter) {
		String responseStr = "";
    	String getUrl = url + "?" + parameter;
    	
		RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(config).build();
	    HttpGet get = null;
	    
	    try {
	    	logger.info("开始HTTP请求<" + getUrl + ">");
	    	get = new HttpGet(getUrl);
	    	CloseableHttpResponse response = httpclient.execute(get);
	    	HttpEntity resEntity = response.getEntity();
            InputStreamReader reader = new InputStreamReader(resEntity.getContent(), "UTF-8"); 
            
	    	char[] buff = new char[1024];
            int length = 0;
            while ((length = reader.read(buff)) != -1) {
            	responseStr += new String(buff, 0, length);
            }
	    } catch (IOException e) {
	    	logger.info("HTTP请求<" + getUrl + ">异常!", e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
			}
		}
	    
	    logger.info("HTTP请求<" + getUrl + ">响应：" + responseStr);
	    
	    return responseStr;
	}
	
	
	public static String httpPost(String url, String json) {
		
		RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
	    CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(config).build();
	    String responseStr = "";
		HttpPost post = null;
		StringEntity entity = null;
		try {
			post = new HttpPost(url);
			entity = new StringEntity(json, "UTF-8");
		    post.setEntity(entity);
		    
		    CloseableHttpResponse response = httpclient.execute(post);
		    HttpEntity resEntity = response.getEntity(); 
            InputStreamReader reader = new InputStreamReader(resEntity.getContent(), "UTF-8"); 
            
            char[] buff = new char[1024];
            int length = 0;
            while ((length = reader.read(buff)) != -1) {
            	responseStr += new String(buff, 0, length);
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return responseStr;
	}
}
