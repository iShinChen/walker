Ext.namespace("com.walker.file.files");
com.walker.file.files.queryFormPnl = null;
com.walker.file.files.fileGridPnl = null;
com.walker.file.files.fileFormPnl = null;

com.walker.file.files.recData=null;

com.walker.file.files.mainPanel=function(obj){
	var tools = [];
	var item = [com.walker.file.files.getGridPnl()];
	//显示工具栏
	if(obj.flag != 1){
		tools = [{
			text : '上传',
			iconCls : 'btnIconImport',
			id : "2000201",
			hidden : true,
			handler : function() {
				com.walker.file.files.openInput();
			}
		},'-',{
			text : '下载',
			iconCls : 'btnIconDownload',
			id : "2000202",
			hidden : true,
			handler : function() {
				com.walker.file.files.fileOutput();
			}
		},'-',{
			text : '删除',
			iconCls : 'btnIconDel',
			id : "2000203",
			hidden : true,
			handler : function() {
				com.walker.file.files.deleteFile();
			}
		},'->',{
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.file.files.queryMessage();
			}
		},'-',{
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("name_input").setValue();
			}
		}];
		item = [ com.walker.file.files.getQueryForm(),com.walker.file.files.getGridPnl()];
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

com.walker.file.files.getQueryForm = function(){
	
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
	
	com.walker.file.files.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.file.files.getGridPnl = function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["id","name","type","file_size","url","create_time","update_time","status"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/walker/files/getFileListPage',
		reader : reader,
		baseParams : {
			limit : 15,
			start: 0
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
				header : "名称",
				dataIndex : "name",
				sortable : true,
				width : 120,
				align : 'left',
				editor:new Ext.form.TextField()
			}, {
				header : "文件类型",
				dataIndex : "type",
				sortable : true,
				width : 80,
				align : 'center'
			}, {
				header : "文件大小",
				dataIndex : "file_size",
				sortable : true,
				width : 80,
				align : 'center'
			}, {
				header : "文件地址",
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
				header : "文件状态",
				dataIndex : "status",
				sortable : true,
				width : 80,
				align : 'center',
				renderer : function(value) {
					if (value == 0) {
						return '<font color="red">无效</font>';
					}else if (value == 1) {
						return '<font color="black">有效</font>';
					}
					return "";
				}
			}
	]);
	
	var grid = new Ext.grid.EditorGridPanel({
			layout : 'fit',
			title : '文件列表',
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
						url: '/walker/files/saveFile',
						params: {
							id : val.record.get("id"),
							name : val.record.get("name"),
							url : val.record.get("url")
						},
						success: function(response) {
							var result = Ext.decode(response.responseText);
							if(result.success){
								com.walker.file.files.queryMessage();
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
	
	com.walker.file.files.fileGridPnl = grid;
	grid.store.load();
	return grid;
};

com.walker.file.files.queryMessage = function() {
	com.walker.file.files.fileGridPnl.store.baseParams.name = Ext.getCmp("name_input").getValue();
	com.walker.file.files.fileGridPnl.store.load();
};

com.walker.file.files.deleteFile=function(){
	var selRecords= com.walker.common.getSelectRecord(com.walker.file.files.fileGridPnl,false);
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
			url : '/walker/files/deleteFile',
			success : function(response) {
				var result = Ext.decode(response.responseText);
				if (result.success) {
					Ext.Msg.alert("提示", "删除成功!");
					com.walker.file.files.queryMessage();
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

com.walker.file.files.win=null;

com.walker.file.files.openInput=function(){
	com.walker.file.files.win=com.walker.file.files.createInputWin();
	com.walker.file.files.win.show();
};

com.walker.file.files.createInputWin = function(){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 450,
		height : 120,
		modal : true,
		constrain : true,
		closeAction:'close',
		title : '上传文件',
		tbar : [ {
			text : '上传',
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.file.files.fileInput();
			}
		},'-',{
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.file.files.win.close();
			}
		} ],
		items : [com.walker.file.files.inputWinForm()]
	});
	return panelTmp;
};

com.walker.file.files.formPanel=null;
com.walker.file.files.inputWinForm=function(){
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
	com.walker.file.files.formPanel= formPnl;
	return formPnl;
};

com.walker.file.files.fileInput=function(){

	com.walker.file.files.formPanel.getForm().submit({
		url:'/walker/files/uploadFile',
		method :'POST',
		waitMsg:"上传中,请稍后...",
		success: function(form ,action) {		
			if(action.result.success){
				Ext.Msg.alert("提示","文件上传成功" ,function() {
					com.walker.file.files.win.close();
				});
				com.walker.file.files.queryMessage();
			}else{
				Ext.Msg.alert("提示","文件上传失败!");
			}
	    },
		failure: function(response) {
			Ext.Msg.alert("提示","上传失败!");
		}	
	});
};

com.walker.file.files.fileOutput=function(){
	
	var rec = com.walker.common.getSelectRecord(com.walker.file.files.fileGridPnl,true);
	if(rec){
		
		if (!Ext.fly('downloadAttachFileForm')) {// 生成一个弹出对话框,以提供下载  
	            var frm = document.createElement('form');  
	            frm.id = 'downloadAttachFileForm';  
	            frm.style.display = 'none';  
	            document.body.appendChild(frm);  
	    }  
		// 请求附件下载  
		Ext.Ajax.request({
			url : "/walker/files/downLoadFile",
		    isUpload : true,
		    form : Ext.fly('downloadAttachFileForm'),
		    method : 'POST',
		    params : {  
		    	fileUrl : rec.get('url')// 附件的路径  	
		    },
		    success : function(response) {
		    	var result = Ext.decode(response.responseText);
		    	if(result.success){
		    		Ext.Msg.alert("提示","文件下载成功!");
		    		com.walker.file.files.queryMessage();
		    	}else{
					Ext.Msg.alert("提示","文件下载失败!");
				}
		    },
		    failure : function(response){
		    	Ext.Msg.alert("提示","下载失败!");
		    }
		});
	}
}

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
			items : [com.walker.file.files.mainPanel(obj)]
		}]
	});
	com.walker.permission.userpermission("20002");
});

