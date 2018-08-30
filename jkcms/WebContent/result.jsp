<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>信息管理</title>

</head>
<body>
	<script language="JavaScript">
function sizeChangeForLogin(heigetValue) 
{ 
    if((typeof parent.document.all.fclogin) != "undefined"&&parent.document.all.fclogin!=null&&document!=null) {
		if(arguments.length==1){
			parent.document.all.fclogin.height=heigetValue;
		}else{
			parent.document.all.fclogin.height=document.body.scrollHeight; 
		}
	}
}

sizeChangeForLogin();
</script>
</body>
</html>