Ext.namespace("com.walker.file.picture");
com.walker.file.picture.queryFormPnl = null;
com.walker.file.picture.pictureGridPnl = null;
com.walker.file.picture.photoFormPnl = null;
var DOWNLOAD_PROCESS_TASKS = [];

com.walker.file.picture.recData=null;

com.walker.file.picture.mainPanel=function(obj){
	var tools = [];
	var item = [com.walker.file.picture.getGridPnl()];
	//显示工具栏
	if(obj.flag != 1){
		tools = [{
			text : '上传',
			iconCls : 'btnIconImport',
			id : "2000601",
			hidden : true,
			handler : function() {
				com.walker.file.picture.editPicture();
			}
		},{
			text : '下载',
			iconCls : 'btnIconDownload',
			id : "2000602",
			hidden : true,
			handler : function() {
				com.walker.file.picture.fileOutput();
			}
		},{
			text : '删除',
			iconCls : 'btnIconDel',
			id : "2000603",
			hidden : true,
			handler : function() {
				com.walker.file.picture.deletePhoto();
			}
		},'->',{
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.file.picture.queryView();
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
		item = [ com.walker.file.picture.getQueryForm(),com.walker.file.picture.getGridPnl()];
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

com.walker.file.picture.getQueryForm = function(){
	
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
	
	com.walker.file.picture.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.file.picture.getGridPnl = function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["id","typeName","type","fileUrl","resolution","status","parentId","parentName","viewUrl"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/jkcms/picture/getPictureListPage',
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
				header : "图片类别",
				dataIndex : "typeName",
				sortable : true,
				width : 120,
				align : 'center'
			},{
				header : "关联内容名称",
				dataIndex : "parentName",
				sortable : true,
				width : 100,
				align : 'center'
			}, {
				header : "类别",
				dataIndex : "type",
				sortable : true,
				hidden :true,
				width : 80,
				align : 'center'
			},{
				header : "存储路径",
				dataIndex : "fileUrl",
				sortable : true,
				hidden :true,
				width : 250,
				align : 'left'
			
			},{
				header : "图片路径",
				align : 'center',
				sortable : false,
				dataIndex : 'fileUrl',
				align : 'left',
				width : 250,
				renderer : function(value, metadata, record, rowIndex, columnIndex, store){
					if(value) {
						var resloution = record.get("resolution");
						var width = 200;
						var height = 200;
						
						var resloutions = resloution.split("*");
						if(resloutions.length == 2) {
							var rwidth = parseInt(resloutions[0]);
							var rheight = parseInt(resloutions[1]);
							if (rwidth <= 200) {
								width = rwidth;
								height = rheight;
							}
							else {
								height = parseInt(width / rwidth * rheight);
							}
						}
						
						var html = "<div style='padding-top: 3px;padding-bottom: 3px;'>" 
							+ "<img src=" + value +" width='" + width + "' height='" + height + "' /></div>";
						metadata.attr = ' ext:qtip="' + html + '"';
					}
					return value;
				}
			},{
				header : "分辨率",
				dataIndex : "resolution",
				sortable : true,
				width : 100,
				align : 'center'
			},{
				header : "关联内容ID",
				dataIndex : "parentId",
				sortable : true,
				width : 80,
				hidden : true,
				align : 'center'
			},{
				header : "数据状态",
				dataIndex : "status",
				sortable : true,
				width : 80,
				align : 'center',
				renderer : function(value) {
					if (value == '00') {
						return '<font color="black">未上线</font>';
					}else if (value == '01') {
						return '<font color="green">已上线</font>';
					}else if (value == '02') {
						return '<font color="red">已下线</font>';
					}
					return "";
				}
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
						url: '/jkcms/picture/savePicture',
						params: {
							id : val.record.get("id"),
							name : val.record.get("name"),
							url : val.record.get("url")
						},
						success: function(response) {
							var result = Ext.decode(response.responseText);
							if(result.success){
								com.walker.file.picture.queryView();
							}else{
								Ext.Msg.alert("提示",result.err_msg);
							}
						},
						failure: function(response) {
							Ext.Msg.alert("提示","操作失败！");
						}
					});
				}
			}
	});
	com.walker.file.picture.videoStore = store;
	com.walker.file.picture.pictureGridPnl = grid;
	store.load();
	return grid;
};

com.walker.file.picture.queryView = function() {
	com.walker.file.picture.pictureGridPnl.store.baseParams.name = Ext.getCmp("name_input").getValue();
	com.walker.file.picture.pictureGridPnl.store.load();
};

com.walker.file.picture.deletePhoto=function(){
	var selRecords= com.walker.common.getSelectRecord(com.walker.file.picture.pictureGridPnl,false);
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
			url : '/jkcms/picture/deletePicture',
			success : function(response) {
				var result = Ext.decode(response.responseText);
				if (result.success) {
					Ext.Msg.alert("提示", "删除成功!");
					com.walker.file.picture.queryView();
				}else{
					Ext.Msg.alert("提示", result.err_msg);
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

com.walker.file.picture.win=null;

com.walker.file.picture.openInput=function(){
	com.walker.file.picture.win=com.walker.file.picture.createInputWin();
	com.walker.file.picture.win.show();
};

com.walker.file.picture.editPicture = function() {
	var record = com.walker.file.picture.pictureGridPnl.getSelectionModel().getSelected();
	if(record) {
		new Ext.Window( {
			title : "编辑海报",
			layout : 'border',
			width : 450,
			height : 430,
			modal : true,
			border : false,
			constrain : true,
			autoScroll : true,
			maximizable : false,
			minimizable : false,
			plain: true,
			resizable : false,
			closeAction : 'close',
			items : [ com.walker.file.picture.createPosterEditForm() ],
			listeners : {
				show : function() {
					com.walker.file.picture.editPosterForm.find("name", "TYPE_NAME")[0].setValue(record.get("typeName"));
					
					var desc = "";
					if(record.data.resolution) {
						desc += "分辨率要求：" + record.data.resolution;
					}
//					if(record.get("FILE_MAX_SIZE")) {
//						desc += "大小限制：" + record.get("FILE_MAX_SIZE") + "KB" + "\n";
//					}
//					if(record.get("FILE_SUFFIX")) {
//						desc += "文件类型：." + record.get("FILE_SUFFIX");
//					}
					com.walker.file.picture.editPosterForm.find("name", "DESCRIPTION")[0].setValue(desc);

					var pictureId = record.data.id;
					if(pictureId) {
						Ext.getCmp("imgUrl").setValue({
							viewUrl : record.data.fileUrl, 
							resloution : record.data.resloution, 
							fileUrl : record.data.fileUrl, 
							fileSize : record.get("FILE_SIZE")
						});
					}
				}
			}
		}).show();
	}
};

com.walker.file.picture.createPosterEditForm = function() {
	com.walker.file.picture.editPosterForm = new Ext.FormPanel( {
		labelWidth : 100,
		labelAlign : 'right',
		bodyStyle : 'padding-top: 10px;',
		region : 'center',
		autoHeight: false,
		autoScroll : true,
		fileUpload : true,
		items : [ {
			xtype : 'textfield',
			fieldLabel : '海报类型',
			name : 'TYPE_NAME',
			width : 220,
			readOnly : true,
			cls : 'x-form-field-readonly'
		}, {
	        xtype : "textarea",
	        fieldLabel: '类型说明',
	        name: 'DESCRIPTION',
			height : 60,
			width : 220,
			readOnly : true,
			cls : 'x-form-field-readonly'
		}, new Ext.ux.form.ImageUploadField({
			fieldLabel : "图片预览",
			width : 220,
			height: 220,
			id : 'imgUrl',
			uploadAction : '/jkcms/picture/upload',
			uploadParams : {ftpRoot: 'picture'},
			override : false,
			listeners : {
				uploaded : function(o, result) {
					Ext.Msg.alert("提示", "图片上传成功,点击保存生效!");
					Ext.getCmp('btnSavePicture').setDisabled(false);
				},
				unuploaded : function(o, m) {
					Ext.Msg.alert("提示", m.errorMsg);
	    		},
		    	cleared : function(o) {
		    		Ext.getCmp('btnSavePicture').setDisabled(false);
		    	}
	    	}
		}) ],
		tbar : [ {
			text : '保存',
			iconCls : 'btnIconSave',
			id : 'btnSavePicture',
			disabled : true,
			handler : function() {
				com.walker.file.picture.savePicture();
			}
		}, '-', {
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				Ext.WindowMgr.getActive().close();
			}
		} ]
	});
	return com.walker.file.picture.editPosterForm;
};

com.walker.file.picture.savePicture = function() {
	var record = com.walker.file.picture.pictureGridPnl.getSelectionModel().getSelected();
	if(com.walker.file.picture.editPosterForm.getForm().isValid()) {
		Ext.Msg.confirm('提示',"确定保存吗?", function(btn) {
			if(btn!='yes') {return;}
			
			var imageValue = Ext.getCmp("imgUrl").getValue();
			console.info('url1:' + imageValue.fileUrl);
			com.walker.file.picture.editPosterForm.getEl().mask("数据提交中...", "x-mask-loading");
			Ext.Ajax.request( {
				url : '/jkcms/picture/savePicture.do',
				method : 'post',
				success : function(response) {
					com.walker.file.picture.editPosterForm.getEl().unmask();
					var result = Ext.decode(response.responseText);
					if (result.success) {
						Ext.Msg.alert("提示", "更新海报成功!");
						Ext.WindowMgr.getActive().close();
						com.walker.file.picture.pictureGridPnl.store.reload();
						com.walker.file.picture.refreshSeriesGrid();
					}
				},
				failure : function(response) {
					com.walker.file.picture.editPosterForm.getEl().unmask();
					Ext.Msg.alert("提示", "更新海报失败!");
				},
				params : {
					FILE_URL : imageValue.fileUrl,
					RESLOUTION : imageValue.resloution,
					FILE_SIZE : imageValue.fileSize,
					TYPE : record.get("TYPE_CODE"),
					PICTURE_ID : record.get("id"),
					SERIES_ID : com.walker.file.picture.SERIES_ID
				}
			});
		});
	}
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
			items : [com.walker.file.picture.mainPanel(obj)]
		}]
	});
	com.walker.permission.userpermission("20006");
});

