Ext.namespace("com.walker.customer.photo");
com.walker.customer.photo.queryFormPnl = null;
com.walker.customer.photo.photoGridPnl = null;
com.walker.customer.photo.photoFormPnl = null;
var DOWNLOAD_PROCESS_TASKS = [];

com.walker.customer.photo.recData=null;

com.walker.customer.photo.mainPanel=function(obj){
	var tools = [];
	var item = [com.walker.customer.photo.getGridPnl()];
	//显示工具栏
	if(obj.flag != 1){
		tools = [{
			text : '上传',
			iconCls : 'btnIconImport',
			id : "3000101",
			hidden : true,
			handler : function() {
				com.walker.customer.photo.openInput();
			}
		},'-',{
			text : '下载',
			iconCls : 'btnIconDownload',
			id : "3000102",
			hidden : true,
			handler : function() {
				com.walker.customer.photo.fileOutput();
			}
		},'-',{
			text : '删除',
			iconCls : 'btnIconDel',
			id : "3000103",
			hidden : true,
			handler : function() {
				com.walker.customer.photo.deletePhoto();
			}
		},'->',{
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.customer.photo.queryView();
			}
		},'-',{
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("name_input").setValue();
				Ext.getCmp("people_input").setValue();
				Ext.getCmp("uploader_input").setValue();
			}
		}];
		item = [ com.walker.customer.photo.getQueryForm(),com.walker.customer.photo.getGridPnl()];
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

com.walker.customer.photo.getQueryForm = function(){
	
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
		},{
			layout : 'form',
			autoHeight : true,
			border : false,
			width : 250,
			labelWidth : 80,
			labelAlign : 'right',
			hidden : false,
			items : [{
				xtype : 'textfield',
				fieldLabel : '关系人',
				name : 'people',
				id:'people_input',
				anchor : '100%'
			}]
		},{
			layout : 'form',
			autoHeight : true,
			border : false,
			width : 250,
			labelWidth : 80,
			labelAlign : 'right',
			hidden : false,
			items : [{
				xtype : 'textfield',
				fieldLabel : '负责人',
				name : 'uploader',
				id:'uploader_input',
				anchor : '100%'
			}]
		}]
	});
	
	com.walker.customer.photo.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.customer.photo.getGridPnl = function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["id","name","type","people","uploader","size","url","create_time","update_time","status"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/walker/photo/getPhotoListPage',
		reader : reader,
		baseParams : {
			limit : 15,
			start: 0
		},
		remoteSort : true
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
				header : "名称",
				dataIndex : "name",
				sortable : true,
				width : 120,
				align : 'left',
				editor:new Ext.form.TextField()
			}, {
				header : "时长",
				dataIndex : "type",
				sortable : true,
				width : 80,
				align : 'center'
			},{
				header : "关系人",
				dataIndex : "people",
				sortable : true,
				width : 80,
				align : 'center'
			},{
				header : "负责人",
				dataIndex : "uploader",
				sortable : true,
				width : 80,
				align : 'center'
			},{
				header : "地址路径",
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
						url: '/walker/photo/savePhoto',
						params: {
							id : val.record.get("id"),
							name : val.record.get("name"),
							url : val.record.get("url")
						},
						success: function(response) {
							var result = Ext.decode(response.responseText);
							if(result.success){
								com.walker.customer.photo.queryView();
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
	com.walker.customer.photo.videoStore = store;
	com.walker.customer.photo.photoGridPnl = grid;
	store.load();
	return grid;
};

com.walker.customer.photo.queryView = function() {
	com.walker.customer.photo.photoGridPnl.store.baseParams.name = Ext.getCmp("name_input").getValue();
	com.walker.customer.photo.photoGridPnl.store.baseParams.people = Ext.getCmp("people_input").getValue();
	com.walker.customer.photo.photoGridPnl.store.baseParams.uploader = Ext.getCmp("uploader_input").getValue();
	com.walker.customer.photo.photoGridPnl.store.load();
};

com.walker.customer.photo.deletePhoto=function(){
	var selRecords= com.walker.common.getSelectRecord(com.walker.customer.photo.photoGridPnl,false);
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
			url : '/walker/photo/deletePhoto',
			success : function(response) {
				var result = Ext.decode(response.responseText);
				if (result.success) {
					Ext.Msg.alert("提示", "删除成功!");
					com.walker.customer.photo.queryView();
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

com.walker.customer.photo.win=null;

com.walker.customer.photo.openInput=function(){
	com.walker.customer.photo.win=com.walker.customer.photo.createInputWin();
	com.walker.customer.photo.win.show();
};

com.walker.customer.photo.createInputWin = function(){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 450,
		height : 440,
		modal : true,
		constrain : true,
		closeAction:'close',
		title : '上传视频',
		tbar : [ {
			text : '上传',
			iconCls : 'btnIconSave',
			name : 'save',
			handler : function() {
				
			}
		},'-',{
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.customer.photo.win.close();
			}
		} ],
		items : [com.walker.customer.photo.inputWinForm()]
	});
	return panelTmp;
};

com.walker.customer.photo.formPanel=null;
com.walker.customer.photo.inputWinForm=function(){
	var formPnl = new Ext.form.FormPanel({
	    width : 450,
	    height : 440,
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
            	items:[{
            		xtype : "textfield",
	         		fieldLabel : '关系人',
	         		name : 'people',
	         		maxLength:'32',
	         		allowBlank : false
            	}]
	    	},{
            	layout : "form",
            	items:[{
            		xtype : "textfield",
	         		fieldLabel : '监护人',
	         		name : 'uploader',
	         		maxLength:'32',
	         		allowBlank : false
            	}]
	    	},{
	          layout : "form",
	          html : '<iframe src="../webuploader/upPhoto.jsp"  width="420"  height="270"></iframe>'
	    }]
	});
	com.walker.customer.photo.formPanel= formPnl;
	return formPnl;
};

com.walker.customer.photo.fileOutput=function(){
	
	var selRecords= com.walker.common.getSelectRecord(com.walker.customer.photo.photoGridPnl,false);
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
			url : "/walker/video/downloadVideo",
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
		    		com.walker.customer.photo.queryView();
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
			items : [com.walker.customer.photo.mainPanel(obj)]
		}]
	});
	com.walker.permission.userpermission("30001");
});

