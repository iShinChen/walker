<%@ page pageEncoding="utf-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>调查兵团</title>
		<link rel="shortcut icon" href="favicon.ico" />
		<link rel="icon" href="favicon.ico" type="image/ico" />
		<%@include file="loader/loader.jsp"%>
		<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ux/Ext.util.MD5.js"></script>
		
		<script type="text/javascript">
			if (top.location != window.location) {
				top.location.reload(true);
			}
			
			function login() {
				var loginName = document.getElementById("id").value;
				var password = document.getElementById("pwd").value;
				
				if(loginName.replace(/(^s*)|(s*$)/g, "").length == 0)
				{
					Ext.Msg.alert("提示","请输入用户名！");
					return false;
				}
				
				if(password.replace(/(^s*)|(s*$)/g, "").length == 0)
				{
					Ext.Msg.alert("提示","请输入密码！");
					return false;
				}
				
				Ext.Ajax.request({
					url : '/walker/user/login',
					success : function(response){
						var result = Ext.util.JSON.decode(response.responseText);
						if(result.success)
						{
							window.location.href = "index.jsp";
						} else{
							Ext.Msg.alert("提示",result.err_msg);
						}
					},
					params : {
						loginName : loginName,
						password : Ext.util.MD5(password)
					}
				});
			}
			var ENTER_ACTION = login;

			jQuery(document).ready(function(){
				jQuery("#id").focus();
			});
		</script>
		
		<style type="text/css">
			body {
				margin-left: 0px;
				margin-top: 0px;
				margin-right: 0px;
				margin-bottom: 0px;
				font-size:12px;
				font-family:Verdana;
				background: url("resources/images/login/bg_4.jpg") repeat-x top;
		 	}
		 	
			#bgTable {
				background: url("resources/images/login/bg.jpg") #eeeeee repeat-x;
			}
			
			#titleTD {
				height: 45px;
				*height: 40px;
			}
			
			#systemname {
				color: white;
				font-family: 隶书;
				font-size: 30px;
				font-weight: bold;
			}
		</style>
	</head>
	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
		marginheight="0">
		<table width="421" border="0" align="center" cellpadding="0" id="bgTable"
			cellspacing="0"
			style="margin-top: 15%">
			<tr>
				<td width="13" align="left" valign="top">
					<img src="resources/images/login/left.jpg" width="13" height="251" />
				</td>
				<td align="left" valign="top">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="11" align="left" valign="top">
								<img src="resources/images/login/dian.jpg" width="1" height="1" />
							</td>
						</tr>
						<tr>
							<td align="center" valign="middle" id="titleTD">
								<span id="systemname">芒果TV福建内容中转平台</span>
							</td>
						</tr>
						<tr>
							<td height="163" align="left" valign="top">
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="146" height="163" align="left" valign="middle"
											style="padding-left: 12px;">
											<img src="resources/images/logo/cy_logo.png" width="124"
												height="125" />
										</td>
										<td width="249">
											<input type="hidden" name="regname" id="regname" value="" />
											<table width="100%" height="118" border="0" cellpadding="0"
												cellspacing="0" style="border-left: 1px solid #489b9b;">
												<tr>
													<td width="27%" align="right" valign="middle"
														style="font-weight: bold; color: #000; font-size: 12px;">
														用户名：
													</td>
													<td width="73%">
														<input type=text id="id"
															style="width: 157px; height: 19px; border: 1px solid #000;"
															value="" />
													</td>
												</tr>
												<tr>
													<td align="right" valign="middle"
														style="font-weight: bold; color: #000; font-size: 12px;">
														密&nbsp;&nbsp; 码：
													</td>
													<td>
														<input type="password" id="pwd" type="password"
															style="width: 157px; height: 19px; border: 1px solid #000;" />
													</td>
												</tr>
												<tr>
													<td colspan="2" align="center" valign="middle">
														<input type="button" name="button" id="button" value="登 录"
															onclick="login()" />
															&nbsp;&nbsp;
															<input type="button" name="button2" id="button2"
																value="重 置" onclick="clear()" />
													</td>
												</tr>
												<tr>
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="21" align="center" valign="middle"
								style="font-size: 12px; color: #222222; letter-spacing: 2px;">
								Copyright 南京昌亚智能科技有限公司
							</td>
						</tr>
					</table>
				</td>
				<td width="13" align="right" valign="top">
					<img src="resources/images/login/right.jpg" width="13" height="251" />
				</td>
			</tr>
		</table>
	</body>
</html>



