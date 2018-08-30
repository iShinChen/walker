Ext.namespace("com.walker.file.video_online");
com.walker.file.video_online.queryFormPnl = null;
com.walker.file.video_online.videoOnlineGridPnl = null;
com.walker.file.video_online.videoOnlineFormPnl = null;

com.walker.file.video_online.recData=null;

com.walker.file.video_online.mainPanel=function(obj){
	var tools = [];
	var item = [com.walker.file.video_online.getGridPnl()];
	//显示工具栏
	if(obj.flag != 1){
		tools = [{
			text : '新增',
			iconCls : 'btnIconAddIn',
			id : "2000501",
			hidden : true,
			handler : function() {
				com.walker.file.video_online.editVideoOnline("add");
			}
		},'-',{
			text : '编辑',
			iconCls : 'btnIconUpdate',
			id : "2000502",
			hidden : true,
			handler : function() {
				com.walker.file.video_online.editVideoOnline("edit");
			}
		},'-',{
			text : '删除',
			iconCls : 'btnIconDel',
			id : "2000503",
			hidden : true,
			handler : function() {
				com.walker.file.video_online.deleteVideo();
			}
		},'->',{
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.file.video_online.queryView();
			}
		},'-',{
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("name_input").setValue();
			}
		}];
		item = [ com.walker.file.video_online.getQueryForm(),com.walker.file.video_online.getGridPnl()];
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

com.walker.file.video_online.getQueryForm = function(){
	
	var queryForm = new Ext.Panel({
		id : 'videoSearchPanel',
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
	
	com.walker.file.video_online.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.file.video_online.getGridPnl = function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["id","name","tags","video_url","flash_url","embed_url","create_time","update_time","status"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/jkcms/videoOnline/getVideoOnlineListPage',
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
				header : "视频名称",
				dataIndex : "name",
				sortable : true,
				width : 120,
				align : 'left'
			}, {
				header : "标签",
				dataIndex : "tags",
				sortable : true,
				width : 120,
				align : 'center'
			}, {
				header : "网页地址",
				dataIndex : "video_url",
				sortable : true,
				width : 200,
				align : 'center'
			},{
				header : "Flash地址",
				dataIndex : "flash_url",
				sortable : true,
				width : 200,
				align : 'center'
			},{
				header : "嵌入地址",
				dataIndex : "embed_url",
				sortable : true,
				hidden : true,
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
				header : "嵌入播放",
				dataIndex : "",
		        renderer: function (value, meta, record) {
		             var formatStr = "<image class='imgBtn' src='" + BASE_PATH + "resources/images/button/film_save.png' style='cursor:pointer' onclick='javascript:return false;'></image>"; 
		             var resultStr = String.format(formatStr);
		             return "<div class='controlBtn'>" + resultStr + "</div>";
		        }.createDelegate(this),
		        align : 'center'
			},{
				header : "播放器播放",
				dataIndex : "",
		        renderer: function (value, meta, record) {
		             var formatStr = "<image class='imgBtn2' src='" + BASE_PATH + "resources/images/button/film_save.png' style='cursor:pointer' onclick='javascript:return false;'></image>"; 
		             var resultStr = String.format(formatStr);
		             return "<div class='controlBtn'>" + resultStr + "</div>";
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
			})
	});
	
	grid.on('cellclick', function (grid, rowIndex, columnIndex, e) {
		var btn = e.getTarget('.imgBtn');
		if (btn) {
            record = grid.getStore().getAt(rowIndex);
            var play_name = record.data.name;
            var	play_url = record.data.embed_url;
            com.walker.file.video_online.showPlayWin(play_name,play_url);
		}
	});
	
	com.walker.file.video_online.videoStore = store;
	com.walker.file.video_online.videoOnlineGridPnl = grid;
	store.load();
	return grid;
};


com.walker.file.video_online.queryView = function() {
	com.walker.file.video_online.videoOnlineGridPnl.store.baseParams.name = Ext.getCmp("name_input").getValue();
	com.walker.file.video_online.videoOnlineGridPnl.store.load();
};

com.walker.file.video_online.title=null;
com.walker.file.video_online.creayteType=null;


com.walker.file.video_online.editVideoOnline = function(types) {
	com.walker.file.video_online.creayteType=types;
	if(types == "edit")
	{
		var rec = com.walker.common.getSelectRecord(com.walker.file.video_online.videoOnlineGridPnl,true);
		if(rec){
			Ext.Ajax.request({
				url: '/jkcms/videoOnline/getVideoOnlineById',
				params : {
					id:rec.get('id')
				},
				success: function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.success){
						com.walker.file.video_online.recData=result.data;
						com.walker.file.video_online.title="修改视频";
						com.walker.file.video_online.showRecordWin();
					}else{
						Ext.Msg.alert("提示",result.err_msg);
					}
			     },
				failure: function(response) {		
					Ext.Msg.alert("提示","获取信息失败");
				}
			});
		}
	}else{
		com.walker.file.video_online.title="新增信息";
		com.walker.file.video_online.showRecordWin();
	}
};

com.walker.file.video_online.editWin=null;
com.walker.file.video_online.showRecordWin=function(){
	com.walker.file.video_online.editWin=com.walker.file.video_online.createEditWin();
	if(com.walker.file.video_online.creayteType=="edit"){
		com.walker.file.video_online.messageFormPnl.find("name", "id")[0].setValue(com.walker.file.video_online.recData.id);
		com.walker.file.video_online.messageFormPnl.find("name", "name")[0].setValue(com.walker.file.video_online.recData.name);
		com.walker.file.video_online.messageFormPnl.find("name", "tags")[0].setValue(com.walker.file.video_online.recData.tags);
		com.walker.file.video_online.messageFormPnl.find("name", "video_url")[0].setValue(com.walker.file.video_online.recData.video_url);
		com.walker.file.video_online.messageFormPnl.find("name", "flash_url")[0].setValue(com.walker.file.video_online.recData.flash_url);
		com.walker.file.video_online.messageFormPnl.find("name", "embed_url")[0].setValue(com.walker.file.video_online.recData.embed_url);
	}
	com.walker.file.video_online.editWin.show();
};


com.walker.file.video_online.playWin=null;
com.walker.file.video_online.showPlayWin=function(play_name,play_url){
	com.walker.file.video_online.playWin = new Ext.Window({
		layout : 'fit',
		width : 840,
		height : 630,
		modal : true,
		constrain : true,
		closeAction:'close',
		title : play_name,
		items : [new Ext.form.FormPanel({
				    width : 960,
				    height : 540,
				    frame : true,
				    layout : "form",
				    labelWidth : 80,
				    html : play_url
				})]
		});
	com.walker.file.video_online.playWin.show();
};

com.walker.file.video_online.createEditWin=function(){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 666,
		height : 250,
		modal : true,
		constrain : true,
//		maximizable: true,  
//		maximized :true,
		closeAction:'close',
		title : com.walker.file.video_online.title,
		tbar : [ {
			text : '保存',
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.file.video_online.saveVdieoOnline();
			}
		},'-',{
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.file.video_online.editWin.close();
			}
		} ],
		items : [com.walker.file.video_online.editWinForm()]
	});
	return panelTmp;
	
};

com.walker.file.video_online.editWinForm=function(){
	
	var messageFormPnl = new Ext.form.FormPanel({
	    width : 777,
	    height : 333,
	    frame : true,
	    labelWidth : 120,
	    labelAlign : "right",
	    autoScroll:true, 
//	    fileUpload : true,
        border: false,  
	    items : [
            {	layout : "form",
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
		         	anchor : '80%',
	         		allowBlank : false
            	}]
            },{	
            	layout : "form",
            	items:[{
            		xtype : "textfield",
	         		fieldLabel : '标签',
	         		name : 'tags',
	         		anchor : '80%'
            	}]
            },{	
            	layout : "form",
            	items:[{
            		xtype : "textfield",
		     		fieldLabel : '网页地址',
		     		name : 'video_url',
		     		allowBlank : true,
		     		anchor : '80%'
            	}]
            },{
   				layout : "form",
            	items:[{
            		xtype : "textfield",
			        fieldLabel : "Flash地址",
			        name : "flash_url",
			        allowBlank : true,
			        anchor : '80%'
            	}]
			},{
   				layout : "form",
            	items:[{
            		xtype : "textarea",
			        fieldLabel : "嵌入地址",
			        name : "embed_url",
			        allowBlank : true,
			        anchor : '80%'
            	}]
			}
   ]
	});
	com.walker.file.video_online.messageFormPnl = messageFormPnl;
	return messageFormPnl;
};

com.walker.file.video_online.saveVdieoOnline=function(){
	if(com.walker.file.video_online.messageFormPnl.getForm().isValid()){
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			com.walker.file.video_online.messageFormPnl.getForm().submit({
				method:"POST",
				waitMsg:"保存中,请稍后...",
				url : "/jkcms/videoOnline/saveVideoOnline",
				success: function(form, action) {
						if(action.result.success){
							Ext.Msg.alert("提示","保存成功" ,function() {
								com.walker.file.video_online.editWin.close();
							});
							com.walker.file.video_online.queryView();
						} else {
							Ext.Msg.alert("提示", action.result.err_msg);
						}
				     },
				 failure: function(form, action) {
			    	 Ext.Msg.alert("提示","保存失败");
				 }
			});
		};
		Ext.Msg.confirm('提示',"确定保存吗？",sumBtn);
	}
};

com.walker.file.video_online.deleteVideoOnline=function(){
	var selRecords= com.walker.common.getSelectRecord(com.walker.file.video_online.videoOnlineGridPnl,false);
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
			url : '/jkcms/videoOnline/deleteVideoOnline',
			success : function(response) {
				var result = Ext.decode(response.responseText);
				if (result.success) {
					Ext.Msg.alert("提示", "删除成功!");
					com.walker.file.video_online.queryView();
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
			items : [com.walker.file.video_online.mainPanel(obj)]
		}]
	});
	com.walker.permission.userpermission("20005");
});

