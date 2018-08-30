package com.springmvc.walker.controller.file;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springmvc.framework.entity.PageResultBean;
import com.springmvc.framework.util.PrintWriterUtil;

@Controller
@RequestMapping("/compare") 
public class CompareAction {
	
	@RequestMapping(value = "/doCompare")
	public void getDetails(HttpServletRequest request,HttpServletResponse response){
		PageResultBean result = new PageResultBean();
		String diffType = request.getParameter("diffType");
		String requestId = request.getParameter("requestId");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String url = "jdbc:mysql://hcp01-testing-mb11.cp01.baidu.com:8306/aladdin_test";
		Connection conn = null;
		try{
			Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动 
			conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
			String sql ="select rd.diff_type,rd.diff_seg,rd.a_val,rd.b_val,rr.env_a,rr.enb_b from regression_diff rd left join regression_request rr on rr.id = rd.req_id "
					+ " where rd.diff_type = " + diffType+ " and rd.req_id = " + requestId;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
            	resultMap.put("diff_type", rs.getInt("diff_type"));
            	resultMap.put("diff_seg", rs.getString("diff_seg"));
            	resultMap.put("a_val", rs.getString("a_val"));
            	resultMap.put("b_val", rs.getString("b_val"));
            	resultMap.put("env_a", rs.getString("env_a"));
            	resultMap.put("enb_b", rs.getString("enb_b"));
            	list.add(resultMap);
			}
			result.setRows(list);
			result.setSuccess(true);
		}catch (Exception e) {
			result.setSuccess(false);
			result.setErr_msg("获取数据异常。");
        }
		PrintWriterUtil.write(response, result);
	}
}
