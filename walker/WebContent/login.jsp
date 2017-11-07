<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>全媒体内容管理平台</title>
		<link rel="shortcut icon" href="favicon.ico" />
		<link rel="icon" href="favicon.ico" type="image/ico" />
		<base href="<%=basePath%>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<script type="text/javascript" src="resources/jquery/jquery.min.js"></script>
		<script type="text/javascript" src="resources/jquery/ux/jquery.md5.js"></script>
		
		<style type="text/css">
			body {
				background: url("resources/images/login/bg.jpg") repeat-x #EAEAEA;
				overflow: hidden;
				text-align: center;
			}
			
			#login_form {
				position: absolute;
				top: 66px;
				width: 521px;
				height: 392px;
				background: url("resources/images/login/form_bg.png") no-repeat;
			}
			
			#logo {
				position: absolute;
				top: 44px;
				left: 35px;
				width: 198px;
				height: 32px;
				background: url("resources/images/login/title.png") no-repeat;
			}
			
			input {
				position: absolute;
				width: 250px;
				height: 35px;
				font-size: 20px;
				line-height: 20px;
				color: #555555;
				border-width: 2px;
				border-style: solid;
				border-color: white;
				padding-left: 10px;
				*padding-top: 5px;
			}
			
			input.x-form-focus {
				border-color: #4D90EF;
			}
			
			#loginName {
				top: 118px;
				left: 138px;
			}
			
			#password {
				top: 169px;
				left: 138px;
			}
			
			#authCode {
				top: 220px;
				left: 138px;
				width: 164px;
			}
		
			#authCodeImage {
				position: absolute;
				top: 220px;
				left: 313px;
				width: 75px;
				height: 35px;
				cursor: pointer;
			}
			
			#btn_login {
				position: absolute;
				top: 300px;
				left: 34px;
				width: 200px;
				height: 32px;
				background: url("resources/images/login/btn_login.png");
				cursor: pointer;
			}
			
			#btn_reset {
				position: absolute;
				top: 300px;
				left: 286px;
				width: 200px;
				height: 32px;
				background: url("resources/images/login/btn_reset.png");
				cursor: pointer;
			}
			
			div.required-tip {
				background: url("resources/images/login/tip-required.png") no-repeat;
			}
			
			#loginNameError {
				position: absolute;
				width: 75px;
				height: 20px;
				left: 400px;
				top: 125px;
			}
			
			#loginNameError.error-tip {
				background: url("resources/images/login/error-loginName.png") no-repeat;
			}
			
			#passwordError {
				position: absolute;
				width: 75px;
				height: 20px;
				left: 400px;
				top: 176px;
			}
			
			#passwordError.error-tip {
				background: url("resources/images/login/error-password.png") no-repeat;
			}
			
			#authCodeError {
				position: absolute;
				width: 75px;
				height: 20px;
				left: 400px;
				top: 226px;
			}
			
			#authCodeError.error-tip {
				background: url("resources/images/login/error-authcode.png") no-repeat;
			}
		</style>
		<script type="text/javascript">
			if (top.location != window.location) {
				top.location.href = window.location.href;
			}
			
			function refreshAuthCode() {
				document.getElementById("authCodeImage").src = "servlet/VerifyCodeServlet?t=" + Math.random();
			}
			
			function doLogin() {
				var loginName = $("#loginName").val();
				var password = $("#password").val();
				var authCode = $("#authCode").val();
				
				$('#loginNameError').removeClass("required-tip").removeClass("error-tip");
				$('#passwordError').removeClass("required-tip").removeClass("error-tip");
				$('#authCodeError').removeClass("required-tip").removeClass("error-tip");
				
				if(loginName == '') {
					$('#loginNameError').addClass("required-tip");
					return;
				}
				
				if(password == '') {
					$('#passwordError').addClass("required-tip");
					return;
				}
				
				if(authCode == '') {
					$('#authCodeError').addClass("required-tip");
					return;
				}
				
				$.ajax({
					url : '/walker/user/login',
					method : 'post',
					dataType : 'json',
					data : {
						loginName: loginName,
						password: $.md5(password),
						verifyCode: authCode
					},
					success : function(result){
						if(result.success)
						{
							window.location.href = "index.jsp";
						} else{
							var errorCode = result.err_msg;
							if(errorCode == 'LGN') {
								$('#loginNameError').addClass("error-tip");
								$("#loginName").focus();
							}
							else if(errorCode == 'PWD') {
								$('#passwordError').addClass("error-tip");
								refreshAuthCode();
								$("#password").val('').focus();
							}
							else if(errorCode == 'AUC') {
								$('#authCodeError').addClass("error-tip");
								refreshAuthCode();
								$("#authCode").val('').focus();
							}
							else {
								alert("登录发生异常。");
							}
						}
					}
				});
			}
			
			function resetForm() {
				$("#loginName").val('');
				$("#password").val('');
				$("#authCode").val('');
				refreshAuthCode();
			}
			
			$(document).ready(function() {
				$("input").blur(function() {
					$(this).removeClass("x-form-focus");
					var val = $(this).val();
					if(val != '') {
						$(this).next('div').removeClass('required-tip');
					}
					else {
						$(this).next('div').addClass('required-tip');
					}
				});
				
				$("input").focus(function() {
					 $(this).addClass("x-form-focus");
				});
				
				var formCenter = function () {
					$('#login_form').css({
						left: ($(document).width() - $('#login_form').outerWidth())/2
					});
				};
				
				$(window).resize(function() {
					formCenter();
				});
				
				$(window).keydown(function(e) {
					if(e.keyCode == 13) {
						doLogin();
					}
				});
				
				//初始化函数
				formCenter();
				
				$("#loginName").focus();
			});
		</script>
	</head>

	<body>
		<div id="login_form">
			<div id="logo"></div>
			<input type="text" id="loginName" />
			<div id="loginNameError"></div>
			<input type="password" id="password" />
			<div id="passwordError"></div>
			<input type="text" id="authCode" maxlength="4" autocomplete="off" />
			<div id="authCodeError"></div>
			<img id="authCodeImage" align="top" src="servlet/VerifyCodeServlet" title="点击刷新"
				onclick="refreshAuthCode(); return false;" />
			<div id="btn_login" onclick="doLogin(); return false;"></div>
			<div id="btn_reset" onclick="resetForm(); return false;"></div>
		</div>
	</body>
</html>