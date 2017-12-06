<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>媒资合集</title>
	<%@include file="../../../loader/loader.jsp"%>
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/ext3.3.1/ux/fileuploadfield/css/Ext.ux.form.FileUploadField.css" />
	<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ux/fileuploadfield/Ext.ux.form.FileUploadField.js"></script>
	
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/ext3.3.1/ux/imageupload/css/Ext.ux.form.ImageUpload.css" />
	<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ux/imageupload/Ext.ux.form.Image.js"></script>
	<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ux/imageupload/Ext.ux.form.ImageUpload.js"></script>
	<script type="text/javascript" src="<%=basePath%>/jsp/xml/js/modifyMediaSeries.js"></script>
	
	<style type="text/css">
		div.moviestatusok {
			width: 16px;
			height: 16px;
			background: url("../../resources/images/okvod.png") no-repeat;
			margin: 0px auto;
		}

		div.moviestatuserror {
			width: 16px;
			height: 16px;
			background: url("../../resources/images/lackmovie.png") no-repeat;
			margin: 0px auto;
		}

		div.lostpicture {
			width: 16px;
			height: 16px;
			background: url("../../resources/images/lostpicture.png") no-repeat;
			margin: 0px auto;
		}
		
		div.lackpicture {
			width: 16px;
			height: 16px;
			background: url("../../resources/images/lackpicture.png") no-repeat;
			margin: 0px auto;
		}

		div.okpicture {
			width: 16px;
			height: 16px;
			background: url("../../resources/images/okvod.png") no-repeat;
			margin: 0px auto;
		}
	</style>
</head>
<body>
</body>
</html>