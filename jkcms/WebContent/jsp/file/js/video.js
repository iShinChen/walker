Ext.namespace("com.walker.file.video");
com.walker.file.video.queryFormPnl = null;
com.walker.file.video.videoGridPnl = null;
com.walker.file.video.videoFormPnl = null;
var DOWNLOAD_PROCESS_TASKS = [];

com.walker.file.video.recData=null;

com.walker.file.video.mainPanel=function(obj){
	var tools = [];
	var item = [com.walker.file.video.getGridPnl()];
	//显示工具栏
	if(obj.flag != 1){
		tools = [{
			text : '上传',
			iconCls : 'btnIconImport',
			id : "2000301",
			hidden : true,
			handler : function() {
				com.walker.file.video.openInput();
			}
		},'-',{
			text : '下载',
			iconCls : 'btnIconDownload',
			id : "2000302",
			hidden : true,
			handler : function() {
				com.walker.file.video.fileOutput();
			}
		},'-',{
			text : '删除',
			iconCls : 'btnIconDel',
			id : "2000303",
			hidden : true,
			handler : function() {
				com.walker.file.video.deleteVideo();
			}
		},'-',{
			text : '播放',
			iconCls : 'btnIconCDNIn',
			id : "2000304",
			hidden : true,
			handler : function() {
				com.walker.file.video.play();
			}
		},'->',{
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.file.video.queryView();
			}
		},'-',{
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("name_input").setValue();
			}
		}];
		item = [ com.walker.file.video.getQueryForm(),com.walker.file.video.getGridPnl()];
	}
	var mainPanel = new Ext.Panel( {
		layout : 'border',
		border : false,
		region : 'center',
		tbar: tools,
		items : item
	});
	return mainPanel;
};

com.walker.file.video.getQueryForm = function(){
	
	var queryForm = new Ext.Panel({
		id : 'fileSearchPanel',
		layout : 'column',
		region : 'north',
		rowHeight : 0,
		frame:true,
		buttonAlign : 'left',
		autoHeight:false,
		height:40,
		items : [{
			layout : 'form',
			autoHeight : true,
			border : false,
			width : 250,
			labelWidth : 80,
			labelAlign : 'right',
			items : [{
				xtype : 'textfield',
				fieldLabel : '名称',
				name : 'name',
				id:'name_input',
				anchor : '100%'
			}]
		}]
	});
	
	com.walker.file.video.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.file.video.getGridPnl = function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["id","name","video_size","duration","url","create_time","update_time","flag","status"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/jkcms/video/getVideoListPage',
		reader : reader,
		baseParams : {
			limit : 15,
			start: 0
		},
		remoteSort : true
	});
	
	var listLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:"数据加载中..."});
	store.on("beforeload", function() {
		for(var i = 0;i < DOWNLOAD_PROCESS_TASKS.length; i++){
			Ext.TaskMgr.stop(DOWNLOAD_PROCESS_TASKS[i]);
		}
		DOWNLOAD_PROCESS_TASKS = [];
		
		if(listLoadMask) {
			listLoadMask.show();
		}
	});
	
	store.on("load", function() {
		if(listLoadMask) {
			listLoadMask.hide();
		}
	});
	
	// 创建列模型的多选框模型.
    var sm = new Ext.grid.CheckboxSelectionModel();	
	var model = new Ext.grid.ColumnModel([sm,
              new Ext.grid.RowNumberer({
		  	  	header : "序号",
					align : 'left',
					width : 40,
					renderer : function(value, metadata, record,rowIndex) {
						var startRow = '';
						if(store.lastOptions.params){
							startRow =store.lastOptions.params.start;
							if (isNaN(startRow)) {
								startRow = 0;
							}
						}else{
							startRow = 0;
						}
						return startRow + rowIndex + 1;
					}
			}),{
				header : "ID",
				dataIndex : "id",
				sortable : true,
				hidden : true,
				width : 100,
				align : 'center'
			},{
				header : "视频名称",
				dataIndex : "name",
				sortable : true,
				width : 120,
				align : 'left',
				editor:new Ext.form.TextField()
			}, {
				header : "时长",
				dataIndex : "duration",
				sortable : true,
				width : 80,
				align : 'center'
			}, {
				header : "视频大小(MB)",
				dataIndex : "video_size",
				sortable : true,
				width : 80,
				align : 'center'
			}, {
				header : "视频地址",
				dataIndex : "url",
				sortable : true,
				width : 200,
				align : 'center'
			},{
				header : "创建时间",
				dataIndex : "create_time",
				sortable : true,
				width : 150,
				align : 'center'
			},{
				header : "更新时间",
				dataIndex : "update_time",
				sortable : true,
				width : 150,
				align : 'center'
			},{
				header : "上传状态",
				dataIndex : "status",
				sortable : true,
				width : 100,
				align : 'center',
				renderer : function(value, metadata, record){
					if (value == '01') {
						com.walker.file.video.getUploadProgress(record.data.id);
						return '<font color="blue">正在上传</font>(<font class="uploadprocess" id="' + record.data.id + '"></font>)';
					}else if (value == '10') {
						return '<font color="green">上传成功</font>';
					}else if (value == '02') {
						return '<font color="red">上传失败</font>';
					}else if (value == '00') {
						return '<font color="black">未上传</font>';
					}else if (value == '03') {
						return '<font color="#7A8B8B">远程文件不存在</font>';
					}else if (value == '04') {
						return '<font color="#CDAD00">暂停下载</font>';
					}else if (value == '05') {
						return '<font color="#8E388E">排队中</font>';
					}
				}
			},{
				header : "下载状态",
				dataIndex : "flag",
				sortable : true,
				width : 100,
				align : 'center',
				renderer : function(value, metadata, record){
					if (value == '01') {
						com.walker.file.video.getDownloadProgress(record.data.id);
						return '<font color="blue">正在下载</font>(<font class="downloadprocess" id="' + record.data.id + '"></font>)';
					}else if (value == '10') {
						return '<font color="green">下载成功</font>';
					}else if (value == '02') {
						return '<font color="red">下载失败</font>';
					}else if (value == '00') {
						return '<font color="black">未下载</font>';
					}else if (value == '03') {
						return '<font color="#7A8B8B">远程文件不存在</font>';
					}else if (value == '04') {
						return '<font color="#CDAD00">暂停下载</font>';
					}else if (value == '05') {
						return '<font color="#8E388E">排队中</font>';
					}
				}
			}, {
				header : "下载进度",
				dataIndex : "",
				width : 100,
				hidden : true,
		        renderer: function (value, meta, record) {
		        	var findBtn = "<image title='查看下载进度' class='imgBtn' src='" + BASE_PATH + "resources/images/button/find.png' style='cursor:pointer' onclick='javascript:return false;'></image>";
		            return "<div id='"+record.data.id+"' class='controlBtn'>"+findBtn+"</div>";
		        }.createDelegate(this),
		        align : 'center'
			}
	]);
	
	var grid = new Ext.grid.EditorGridPanel({
			layout : 'fit',
			title : '视频列表',
			autoWidth : true,
			autoScroll : true,
			cm : model,
			sm : sm,
			store : store,
			stripeRows : true,
			region : 'center',
			bodyStyle : 'padding:5px 5px',
			bbar : new Ext.PagingToolbar({
				pageSize : 15,
				store : store,
				displayInfo : true,
				region : 'center'
			}),
			listeners:{
				afteredit : function(val){
					Ext.Ajax.request({
						url: '/jkcms/video/saveVideo',
						params: {
							id : val.record.get("id"),
							name : val.record.get("name"),
							url : val.record.get("url")
						},
						success: function(response) {
							var result = Ext.decode(response.responseText);
							if(result.success){
								com.walker.file.video.queryView();
							}else{
								Ext.Msg.alert("提示","操作失败！");
							}
						},
						failure: function(response) {
							Ext.Msg.alert("提示","操作失败！");
						}
					});
				}
			}
	});
	com.walker.file.video.videoStore = store;
	com.walker.file.video.videoGridPnl = grid;
	store.load();
	return grid;
};

com.walker.file.video.queryView = function() {
	com.walker.file.video.videoGridPnl.store.baseParams.name = Ext.getCmp("name_input").getValue();
	com.walker.file.video.videoGridPnl.store.load();
};

com.walker.file.video.deleteVideo=function(){
	var selRecords= com.walker.common.getSelectRecord(com.walker.file.video.videoGridPnl,false);
	if(!selRecords){
		return;
	}
	
	var ids="";
	for ( var i = 0; i < selRecords.length; i++) {
		
		if(selRecords[i].data.code!=null){
			
		}
		ids += ","+selRecords[i].data.id;
	}
	var sumBtn = function(btn){
		if(btn != 'yes'){
			return;
		}
		Ext.Ajax.request( {
			url : '/jkcms/video/deleteVideo',
			success : function(response) {
				var result = Ext.decode(response.responseText);
				if (result.success) {
					Ext.Msg.alert("提示", "删除成功!");
					com.walker.file.video.queryView();
				}
			},
			failure : function(response) {
				Ext.Msg.alert("提示", "删除失败!");
			},
			params : {
				ids : ids.substring(1)
			}
		});
	};
	Ext.Msg.confirm('系统提示',"确定删除吗?",sumBtn);
};

com.walker.file.video.win=null;

com.walker.file.video.openInput=function(){
	com.walker.file.video.win=com.walker.file.video.createInputWin();
	com.walker.file.video.win.show();
};


com.walker.file.video.winPlay=null;
com.walker.file.video.play=function(){
	com.walker.file.video.winPlay = new Ext.Window({
		layout : 'fit',
		width : 871,
		height : 520,
		modal : true,
		constrain : true,
		closeAction:'close',
		title : '视频播放',
//		frame : false,
		border: 0,
		items : [new Ext.form.FormPanel({
				    width : 832,
				    height : 468,
				    frame : false,
				    layout : "fit",
//				    labelWidth : 80,
//				    html : '<iframe src="../video-js/demo.html"  width="100%"  height="100%"></iframe>'
				    html : '<iframe src="../video-play/jwplayer.html"  width="100%"  height="100%"></iframe>'	
				})]
		});
	
	com.walker.file.video.winPlay.show();
}

com.walker.file.video.createInputWin = function(){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 450,
		height : 120,
		modal : true,
		constrain : true,
		closeAction:'close',
		title : '上传视频',
		tbar : [ {
			text : '上传',
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.file.video.fileInput();
			}
		},'-',{
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.file.video.win.close();
			}
		} ],
		items : [com.walker.file.video.inputWinForm()]
	});
	return panelTmp;
};

com.walker.file.video.formPanel=null;
com.walker.file.video.inputWinForm=function(){
	var formPnl = new Ext.form.FormPanel({
	    width : 450,
	    height : 120,
	    frame : true,
	    layout : "form",
	    labelWidth : 80,
	    fileUpload: true,
	    enctype:"multipart/form-data",
	    items : [{
	    		layout : "form",
	    		items : [{
	    			xtype : "textfield",
	    			fieldLabel : 'ID',
	    			name : 'id',
	    			hidden: true
	    		}]
	    	},{
            	layout : "form",
            	items:[{
            		xtype : "textfield",
	         		fieldLabel : '名称',
	         		name : 'name',
	         		maxLength:'32',
	         		allowBlank : false
            	}]
	    	},{
	          layout : "form",
	          items : [{
			      xtype : 'textfield',
		    	  allowBlank:false,
	              fieldLabel:'选择文件',
	              inputType:'file',
	              name:'filePath',
	              id:'filePath',
	              anchor : '80%'
	          }]
	    }]
	});
	com.walker.file.video.formPanel= formPnl;
	return formPnl;
};

com.walker.file.video.fileInput=function(){

	com.walker.file.video.formPanel.getForm().submit({
		url:'/jkcms/video/uploadVideo',
		method :'POST',
		waitMsg:"上传中,请稍后...",
		success: function(form ,action) {		
			if(action.result.success){
				Ext.Msg.alert("提示","文件上传成功" ,function() {
					com.walker.file.video.win.close();
				});
				com.walker.file.video.queryView();
			}else{
				Ext.Msg.alert("提示","文件上传失败!");
			}
	    },
		failure: function(response) {
			Ext.Msg.alert("提示","上传失败!");
		}	
	});
};

com.walker.file.video.fileOutput=function(){
	
	var selRecords= com.walker.common.getSelectRecord(com.walker.file.video.videoGridPnl,false);
	if(!selRecords){
		return;
	}
	
	var ids="";
	for ( var i = 0; i < selRecords.length; i++) {
		ids += ","+selRecords[i].data.id;
	}
	
	var sumBtn = function(btn){
		if(btn != 'yes'){
			return;
		}
	
		if (!Ext.fly('downloadAttachFileForm')) {// 生成一个弹出对话框,以提供下载  
	            var frm = document.createElement('form');  
	            frm.id = 'downloadAttachFileForm';  
	            frm.style.display = 'none';  
	            document.body.appendChild(frm);  
	    }  
		// 请求附件下载  
		Ext.Ajax.request({
			url : "/jkcms/video/downloadVideo",
		    isUpload : true,
		    form : Ext.fly('downloadAttachFileForm'),
		    method : 'POST',
		    params : {  
		    	ids : ids.substring(1)// 附件的路径  	
		    },
		    success : function(response) {
		    	var result = Ext.decode(response.responseText);
		    	Ext.Msg.alert("提示",response.responseText);
		    	if(result.success){
		    		Ext.Msg.alert("提示","视频下载成功!");
		    		com.walker.file.video.queryView();
		    	}else{
					Ext.Msg.alert("提示","视频下载失败!");
				}
		    },
		    failure : function(response){
		    	Ext.Msg.alert("提示","下载失败!");
		    }
		});
	};
	Ext.Msg.confirm('系统提示',"确定下载吗?",sumBtn);
};

com.walker.file.video.getDownloadProgress = function(videoId) {
	var task = {
		run: function () {
			Ext.Ajax.request({
				url: '/jkcms/video/findDownloadProgress',
				params: {
					videoId : videoId
				},
				success: function(response) {
					var result = Ext.decode(response.responseText);
					if(result.success) {
						var progress = result.downloadprogress;
						var downloading = result.downloading;
						if(downloading) {
							if(progress < 100)
	                		{
								document.getElementById(videoId).innerText = progress + "%";
	                		}else{
	                			Ext.TaskMgr.stop(task);
	                			com.walker.file.video.videoStore.reload();
	                		}
						}else{
							Ext.TaskMgr.stop(task);
                			com.walker.file.video.videoStore.reload();
						}
					}
				}, 
				failure : function(response) {
					Ext.TaskMgr.stop(task);
					com.walker.file.video.videoStore.reload();
				}
			});
		},  
		interval: 3000
    };
	Ext.TaskMgr.start(task);
	DOWNLOAD_PROCESS_TASKS.push(task);
};

com.walker.file.video.getUploadProgress = function(videoId) {
	var task = {
		run: function () {
			Ext.Ajax.request({
				url: '/jkcms/video/findUploadProgress',
				params: {
					videoId : videoId
				},
				success: function(response) {
					var result = Ext.decode(response.responseText);
					if(result.success) {
						var progress = result.uploadprogress;
						var uploading = result.uploading;
						if(uploading) {
							if(progress < 100)
	                		{
								document.getElementById(videoId).innerText = progress + "%";
	                		}else{
	                			Ext.TaskMgr.stop(task);
	                			com.walker.file.video.videoStore.reload();
	                		}
						}else{
							Ext.TaskMgr.stop(task);
                			com.walker.file.video.videoStore.reload();
						}
					}
				}, 
				failure : function(response) {
					Ext.TaskMgr.stop(task);
					com.walker.file.video.videoStore.reload();
				}
			});
		},  
		interval: 3000
    };
	Ext.TaskMgr.start(task);
	DOWNLOAD_PROCESS_TASKS.push(task);
};



Ext.onReady(function() {
	var obj = {};
	obj.flag = 0;
	Ext.QuickTips.init();
	new Ext.Viewport({
		id : 'row-panel',
		bodyStyle : 'padding:5px',
		layout : 'fit',
		title : 'Row Layout',
		items : [{
			rowHeight : 0,
			xtype : 'panel',
			layout : 'border',
			items : [com.walker.file.video.mainPanel(obj)]
		}]
	});
	com.walker.permission.userpermission("20003");
});

