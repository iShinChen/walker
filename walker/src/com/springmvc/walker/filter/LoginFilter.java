package com.springmvc.walker.filter;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginFilter extends HttpServlet implements Filter{

	private static final long serialVersionUID = 1L;
	
	/**
     * session失效时触发。
     * 
     * @param request
     *            HttpSessionEvent事件
     * @param response
     *            HttpSessionEvent事件
     * @param filter
     *            HttpSessionEvent事件
     * @throws IOException
     *             E
     * @throws ServletException
     *             EE
     */
    @SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest request , ServletResponse response , FilterChain filter) 
			throws IOException,ServletException
	{
    	String contentType = request.getContentType();
    	if (contentType != null && contentType.toLowerCase(Locale.ENGLISH).contains("multipart/form-data") && !contentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/form-data"))
    	{
    		response.getWriter().write("Reject!");
    		return;
    	}
    	
    	// 获得在下面代码中要用的request,response,session对象
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        HttpSession session = servletRequest.getSession();

        // 获得用户请求的URI
        String path = servletRequest.getRequestURI();
        
        // 登陆页面无需过滤
        if(path.indexOf("/login.jsp") > -1 || path.indexOf("login.do") > -1) {
        	filter.doFilter(servletRequest, servletResponse);
            return;
        }
        
        String userId = "";
        
        Map<String, Object> userMap = (Map) session.getAttribute("userMap");
        if(userMap != null)
        {
        	userId = String.valueOf(userMap.get("ID"));
        }

        if (userId == null || "".equals(userId)) {
            // 跳转到登陆页面
            servletResponse.sendRedirect(servletRequest.getContextPath() +"/login.jsp");
        } else {
            // 已经登陆,继续此次请求
        	filter.doFilter(request, response);
        }
    }

    /**
     * 初始化
     * 
     * @param arg0
     *            FilterConfig事件
     * @throws ServletException
     *             EE
     */
    public void init(FilterConfig arg0) throws ServletException
    {
        // TODO Auto-generated method stub
    }
    
    /**
     * 销毁
     */
    public void destroy()
    {
        // TODO Auto-generated method stub
    }

}
