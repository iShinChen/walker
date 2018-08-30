<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
		<link rel="shortcut icon" href="favicon.ico" />
		<link rel="icon" href="favicon.ico" type="image/ico" />
		<title>漫步君-小试身手</title>
		<%@include file="loader/loader.jsp"%>

		<style type="text/css">
			a:link {
				text-decoration: none;
			}
			
			a:active {
				text-decoration: none
			}
			
			a:hover {
				text-decoration: none
			}
			
			a:visited {
				text-decoration: none;
			}
			
			#logininfo .x-form-display-field {
				font-size: 12px;
				font-family: '宋体';
				margin-top: 40px;
				padding-right: 5px;
			}
			
			#logininfo #username {
				cursor: pointer;
				text-decoration: underline;
				overflow: hidden;
				padding-right: 5px;
				word-break: break-all;
				white-space: nowrap;
			}
			
			#logininfo #loginout {
				padding-left: 5px;
				cursor: pointer;
				text-decoration: underline;
			}
			
			.x-panel-copyright .x-panel-body {
				text-align: center;
				font-size: 12px;
				padding-top: 3px;
				background-color: #D9E7F8;
			}
			
			#item_cy_logo .x-panel-body {
				padding-left: 150px;
				background: url('resources/images/logo/logo2.png') no-repeat;
				_background:none; 
				_filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src="resources/images/logo/logo2.png");
			}
			
/* 			body
				{
				background: url('resources/images/background.jpg') no-repeat;
				_background:none; 
				_filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src="resources/images/background.jpg");
				}  */
		</style>

		<script type="text/javascript" src="resources/js/index.js"></script>
	</head>

	<body>
		<script type="text/javascript">
			Ext.onReady(function() {
				com.walker.system.viewport();
			});
		</script>
		<div id="banner"></div>
	</body>
</html>
