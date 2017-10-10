<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath();
%>


<%@page import="com.springmvc.walker.util.RequestUtils"%><meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />

<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/ext3.3.1/resources/css/ext-all.css" />
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/adapter/ext-base.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/locale/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/jquery/jquery.min.js"></script>

<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/css/main.css" />
<script type="text/javascript" src="<%=basePath%>/resources/js/common.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ux/Ext.ux.IFrameComponent.js"></script>

<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/ext3.3.1/ux/TimePicker/css/Ext.ux.form.SpinnerField.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/ext3.3.1/ux/TimePicker/css/Ext.ux.form.TimePickerField.css" />
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ux/TimePicker/Ext.ux.Spinner.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ux/TimePicker/Ext.ux.form.SpinnerField.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/ext3.3.1/ux/TimePicker/Ext.ux.form.TimePickerField.js"></script>
<style  type="text/css">
<!--
.x-selectable,.x-selectable * {
	-moz-user-select: text !important;
	-khtml-user-select: text !important;
	-webkit-user-select: text !important;
}

-->
</style>

<script type="text/javascript">
if (!Ext.grid.GridView.prototype.templates) {
	Ext.grid.GridView.prototype.templates = {};
}
Ext.grid.GridView.prototype.templates.cell = new Ext.Template(
		'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} x-selectable {css}" style="{style}" tabIndex="0" {cellAttr}>',
		'<div class="x-grid3-cell-inner x-grid3-col-{id}" {attr}>{value}</div>',
		'</td>');
	BASE_PATH = '<%=basePath%>/';

	Ext.namespace("CYCMS");

	Ext.grid.ColumnModel.override( {
		getTotalWidth : function(includeHidden) {
			var off = 0;
			if (Ext.isChrome) {
				off = 2;
			};
			if (!this.totalWidth) {
				this.totalWidth = 0;
				for ( var i = 0, len = this.config.length; i < len; i++) {
					if (includeHidden || !this.isHidden(i)) {
						this.totalWidth += this.getColumnWidth(i) + off;
					};
				};
			};
			return this.totalWidth;
		}
	});

	document.onkeydown = function(e) {
		if (!e) {
			e = window.event;
		}

		var target = e.target || e.srcElement;
		var tagName = target.tagName.toUpperCase();

		if (e.keyCode == 8) {
			var r = target.readOnly;
			if (r == undefined) {
				r = false;
			}
			if (tagName != 'INPUT' && tagName != 'TEXTAREA'
					&& tagName != 'PASSWORD' && tagName != 'TEXT') {
				return false;
			} else if (r) {
				return false;
			}
		} else if (e.keyCode == 13) {
			if (tagName != 'TEXTAREA' && ENTER_ACTION) {
				ENTER_ACTION();
				return false;
			}
		}
	};

	Ext.BLANK_IMAGE_URL = "<%=basePath%>/resources/ext3.3.1/resources/images/default/s.gif";
	Ext.onReady(function(){
		Ext.QuickTips.init();
	});

	//查找父页面打开窗口的ID，使用本id找到对应的CYCMS.popWinId
	CYCMS.findPopWinId = 'nothing';

	//打开新窗口
	CYCMS.popWindow = function(param) {
		var pWin = param.openWindow;
		if (!pWin) {
			pWin = top;
		}
		CYCMS.popWinId = pWin.Ext.id();
		var initialConfig = param.win.initialConfig;
		var fileUrl = param.url;
		//处理跨节点调用问题
		if(fileUrl.indexOf('http') != 0){
			if(fileUrl.indexOf('/')!=0){
				fileUrl = '/'+fileUrl;
			}
			fileUrl = '<%=basePath%>'+fileUrl;
		}
		if(fileUrl.indexOf('?')>0)
			fileUrl += "&POP_WIN_ID="+CYCMS.popWinId;
		else
			fileUrl += "?POP_WIN_ID="+CYCMS.popWinId;
		
		var initConfig = {
			id : CYCMS.popWinId,
			layout: 'border',
			items: [
				new top.Ext.Panel({
					closable : true,
					autoScroll : false,
					layout:'fit',
					region: 'center',
					items: [
						new top.Ext.ux.IFrameComponent({
							url: fileUrl
						})
					]
				})
			]
		};
		Ext.applyIf(initConfig, initialConfig);
		new top.Ext.Window(initConfig).show();
	};

	//在子页面中关闭自己
	CYCMS.closePopWindow = function() {
		var popWin = top.Ext.getCmp(CYCMS.findPopWinId);
		if (popWin) {
			popWin.close();
		}
	};

	Ext.override(Ext.data.Connection,{
	    serail : 0,
	    noLogin : false,
	    request : Ext.data.Connection.prototype.request.createSequence(function(){ 
	        this.serail++;
	    }),
	    handleResponse : Ext.data.Connection.prototype.handleResponse.createSequence(function(response){
	    	var contentType = response.getResponseHeader('Content-Type');
	    	if(contentType && contentType.startsWith("text/html")) {
		    	this.noLogin = true;
		    } 
	        if(this.serail == 1 && this.noLogin) {
	        	top.Ext.Msg.alert("提示", "登录超时,请重新登录!", function(a, b, c){
	        		top.location.reload(true);
				});
		    }
	        this.serail--;
	    })
	});

	<%
		if(RequestUtils.getStringParameter(request, "POP_WIN_ID",RequestUtils.STRING_REGEX_CODE)!=null){
			out.write("CYCMS.findPopWinId='" + RequestUtils.getStringParameter(request, "POP_WIN_ID", RequestUtils.STRING_REGEX_CODE) + "';");
		}
	%>
</script>