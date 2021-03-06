package com.springmvc.framework.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.framework.entity.PageResultBean;
import com.springmvc.framework.entity.ResultBean;

public class PrintWriterUtil {
	
	public static void write(HttpServletResponse response, PageResultBean result) 
	{
		PrintWriter pw=null;
		try 
		{
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			pw = response.getWriter();
			
			if(pw!=null)
			{
				if(result!=null)
				{
					pw.write(JSONObject.toJSONString(result));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			if(pw!=null)
			{
				pw.flush();
				pw.close();
			}
		}
	}
	
	public static void write(HttpServletResponse response, ResultBean result) 
	{
		PrintWriter pw=null;
		try 
		{
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			pw = response.getWriter();
			
			if(pw!=null)
			{
				if(result!=null)
				{
					pw.write(JSONObject.toJSONString(result));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			if(pw!=null)
			{
				pw.flush();
				pw.close();
			}
		}
	}
	
	public static void write_text_html(HttpServletResponse response, ResultBean result) 
	{
		PrintWriter pw=null;
		try 
		{
			response.setHeader("Content-Type", "text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			pw = response.getWriter();
			
			if(pw!=null)
			{
				if(result!=null)
				{
					pw.write(JSONObject.toJSONString(result));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			if(pw!=null)
			{
				pw.flush();
				pw.close();
			}
		}
	}
}
