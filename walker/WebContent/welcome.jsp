<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<base href="<%=basePath%>">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<script type="text/javascript" src="resources/jquery/jquery.min.js"></script>
		
		<script type="text/javascript">
			function send(){
				$.ajax({
					type: "post",
					url: "index_send.do",
					dataType: "json",
					success: function(result){
						alert("推送成功！");
			        }
			    });
			}
			
			function receive(){
				$.ajax({
					type: "post",
					url: "index_receive.do",
					dataType: "json",
					success: function(result){
						alert("采集完成，共采集" + result.totalCount + "条数据!");
			        }
			    });
			}
			
			function getData() {
				$.ajax({
					type: "post",
					url: "index_getData.do",
					dataType: "json",
					success: function(result){
						alert("采集成功！");
			        }
			    });
			}
			
			function parseXml() {
				$.ajax({
					type: "post",
					url: "index_parseXml.do",
					dataType: "json",
					success: function(result){
						alert("采集完成，共采集" + result.totalCount + "条数据!");
			        }
			    });
			}
			
		</script>
	</head>

	<body>
		<p>昌亚全媒体内容中转平台</p>
		<hr style="height:1px;border:none;border-top:1px solid #555555;" />
		<!-- <button onclick="parseXml();">采集上游</button> -->
	</body>
</html>
