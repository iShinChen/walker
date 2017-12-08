package com.springmvc.walker.thread.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import com.springmvc.framework.constant.GlobalConstant;


/**
 * MangGuo_RabbitMQ_FeedBack
 * @author Administrator
 *
 */
public class RabbitMQFeedBackThread extends Thread {
	
	private String pushcode;
	
	public RabbitMQFeedBackThread(String pushcode) {
		this.pushcode = pushcode;
	}
	
	private final static Logger logger = Logger.getLogger(RabbitMQFeedBackThread.class);
	
	public void run() {
		String urlStr = GlobalConstant.SYS_MAP.get("FEEDBACK_URL");//http://10.200.8.204:80/cmsfeedback/SubCmsApi/Feedback.action
		PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
		
        try {
            URL realUrl = new URL(urlStr);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("content-type", "text/html");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            String xmlInfo = getFeedBackContent();
            logger.info("回执cmsresult="+xmlInfo);
            out.print("cmsresult="+xmlInfo);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            logger.info("回执结果："+result);
        } catch (Exception e) {
            logger.error("发送 POST 请求出现异常！", e);
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
	}
	
	/*
	public void run() {
		RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
	    CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(config).build();
//	    HttpPost post = new HttpPost("http://175.6.15.136:20080/cmsfeedback/SubCmsApi/Feedback.action");//外网
	    HttpPost post = new HttpPost("http://10.200.8.204:80/cmsfeedback/SubCmsApi/Feedback.action");//内网
	
	    try {
		    post.setHeader("Content-Type", "text/xml");
		    
		    JSONObject jsonParam = new JSONObject();
            jsonParam.put("cmsresult", getFeedBackContent());
		    
		    StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");
		    post.setEntity(entity);
		    
		    CloseableHttpResponse response = httpclient.execute(post);
		    HttpEntity resEntity = response.getEntity(); 
            InputStreamReader reader = new InputStreamReader(resEntity.getContent(), "UTF-8"); 
            
            char[] buff = new char[1024];
            int length = 0;
            while ((length = reader.read(buff)) != -1) {
                System.out.println(new String(buff, 0, length));
            }
	    }
	    catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	
	private String getFeedBackContent() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlresult>");
		sb.append("<msgid>").append(pushcode).append("</msgid><state>1</state><msg></msg>");
		sb.append("</xmlresult>");
		
		return sb.toString();
	}
	
}
