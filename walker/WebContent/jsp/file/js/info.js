Ext.namespace("com.walker.file.info");
com.walker.file.info.queryFormPnl = null;
com.walker.file.info.infoGridPnl = null;
com.walker.file.info.infoFormPnl = null;

com.walker.file.info.recData=null;

com.walker.file.info.mainPanel=function(obj){
	var tools = [];
	var item = [com.walker.file.info.getGridPnl()];
	//显示工具栏
	if(obj.flag != 1){
		tools = [{
			text : '新增',
			iconCls : 'btnIconAddIn',
			id : "2000401",
			hidden : true,
			handler : function() {
				com.walker.file.info.editInfo("add");
			}
		},'-',{
			text : '编辑',
			iconCls : 'btnIconUpdate',
			id : "2000402",
			hidden : true,
			handler : function() {
				com.walker.file.info.editInfo("edit");
			}
		},'-',{
			text : '删除',
			iconCls : 'btnIconDel',
			id : "2000403",
			hidden : true,
			handler : function() {
				com.walker.file.info.deleteInfo();
			}
		},'-',{
			text : '内容预览',
			iconCls : 'btnIconFind',
			id : "2000404",
			hidden : true,
			handler : function() {
				com.walker.preview.previewInfo(com.walker.file.info.infoGridPnl);
			}
		},'->',{
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.file.info.queryInfo();
			}
		},'-',{
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("name_input").setValue();
			}
		}];
		item = [ com.walker.file.info.getQueryForm(),com.walker.file.info.getGridPnl()];
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

com.walker.file.info.getQueryForm = function(){
	
	var queryForm = new Ext.Panel({
		id : 'infoSearchPanel',
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
	
	com.walker.file.info.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.file.info.getGridPnl = function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["id","title","summary","content","type","create_time","update_time","status"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/walker/info/getInfoListPage',
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
				header : "标题",
				dataIndex : "title",
				sortable : true,
				width : 120,
				align : 'left'
			}, {
				header : "类型",
				dataIndex : "type",
				sortable : true,
				width : 80,
				align : 'center'
			}, {
				header : "概述",
				dataIndex : "summary",
				sortable : true,
				width : 150,
				align : 'center'
			}, {
				header : "内容",
				dataIndex : "content",
				sortable : true,
				width : 200,
				align : 'center',
				hidden : true
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
				header : "数据状态",
				dataIndex : "status",
				sortable : true,
				width : 80,
				align : 'center',
				renderer : function(value) {
					if (value == 1) {
						return '<font color="black">未发布</font>';
					}else if (value == 2) {
						return '<font color="green">已发布</font>';
					}
					return "";
				}
			}
	]);
	
	var grid = new Ext.grid.GridPanel({
			layout : 'fit',
			title : '项目列表',
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
	
	com.walker.file.info.infoGridPnl = grid;
	grid.store.load();
	return grid;
};

com.walker.file.info.queryInfo = function() {
	com.walker.file.info.infoGridPnl.store.baseParams.name = Ext.getCmp("name_input").getValue();
	com.walker.file.info.infoGridPnl.store.load();
};

com.walker.file.info.title=null;
com.walker.file.info.creayteType=null;


com.walker.file.info.editInfo = function(types) {
	com.walker.file.info.creayteType=types;
	if(types == "edit")
	{
		var rec = com.walker.common.getSelectRecord(com.walker.file.info.infoGridPnl,true);
		if(rec){
			Ext.Ajax.request({
				url: '/walker/info/getInfoById',
				params : {
					id:rec.get('id')
				},
				success: function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.success){
						com.walker.file.info.recData=result.data;
						com.walker.file.info.title="修改资讯";
						com.walker.file.info.showRecordWin();
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
		com.walker.file.info.title="新增资讯";
		com.walker.file.info.showRecordWin();
	}
};
com.walker.file.info.editWin=null;
com.walker.file.info.showRecordWin=function(){
	com.walker.file.info.editWin=com.walker.file.info.createEditWin();
	if(com.walker.file.info.creayteType=="edit"){
		com.walker.file.info.infoFormPnl.find("name", "id")[0].setValue(com.walker.file.info.recData.id);
		com.walker.file.info.infoFormPnl.find("name", "title")[0].setValue(com.walker.file.info.recData.title);
		com.walker.file.info.infoFormPnl.find("hiddenName", "type")[0].setValue(com.walker.file.info.recData.type);
		com.walker.file.info.infoFormPnl.find("name", "summary")[0].setValue(com.walker.file.info.recData.summary);
		com.walker.file.info.infoFormPnl.findByType("radiogroup")[0].setValue(com.walker.file.info.recData.status);
		com.walker.file.info.infoFormPnl.find("name", "content")[0].setValue(com.walker.file.info.recData.content);
	}
	com.walker.file.info.editWin.show();
};


com.walker.file.info.createEditWin=function(){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 400,
		height : 250,
		modal : true,
		constrain : true,	//防止窗口超出浏览器  
		maximizable: true,  //最大化
		maximized :true,
		closeAction:'close',
		title : com.walker.file.info.title,
		tbar : [ {
			text : '保存',
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.file.info.saveInfo();
			}
		},'-',{
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.file.info.editWin.close();
			}
		} ],
		items : [com.walker.file.info.editWinForm()]
	});
	return panelTmp;
	
};

com.walker.file.info.editWinForm=function(){
	
	var messageFormPnl = new Ext.form.FormPanel({
	    width : 500,
	    height : 300,
	    frame : true,
	    labelWidth : 120,
	    labelAlign : "right",
	    autoScroll:true, 
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
            		width: 400,
            		xtype : "textfield",
		         	fieldLabel : '标题',
		         	name : 'title'
            	}]
            },{	
            	layout : "form",
            	items:[{
            	   width: 400,
             	   xtype : "combo",
            	   fieldLabel : "资讯类型",
            	   hiddenName : "type",
//            	   anchor : '80%',
 			       triggerAction: 'all',
		           displayField : 'VALUE', 
		           valueField : 'CODE', 
		           store:com.walker.file.info.info_type,
			       mode: 'local',
			       emptyText:'请选择...',
			       blankText : '请选择',
			       allowBlank : false,
			       editable:false
               }]
            },{	
            	layout : "form",
            	items:[{
            		width: 400,
            		xtype : "textfield",
		     		fieldLabel : '简介',
		     		name : 'summary',
		     		allowBlank : true
            	}]
            },{
    		    layout : "form",
    		    items : new Ext.form.RadioGroup({
    		    	fieldLabel: '状态',
    		    	width: 200,
    		    	items: [{
    					    name: 'status',
    					    inputValue: '1',
    					    boxLabel: '未发布',
    					    checked: true
    				   },{
    				    	name: 'status',
    				    	inputValue: '2',
    				    	boxLabel: '发布'
    				   }]
    		    	})
    	    },{
    	    	xtype :"panel",
	        	title :"内容",
	        	anchor : '95%',
	        	items :
	        	new Ueditor({
	                allowBlank : true,
	                name : 'content',
	                id :"content",
	                autoHeight : true,
	        	})}
   ]
	});
	com.walker.file.info.infoFormPnl = messageFormPnl;
	return messageFormPnl;
};

com.walker.file.info.saveInfo=function(){
	if(com.walker.file.info.infoFormPnl.getForm().isValid()){
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			com.walker.file.info.infoFormPnl.getForm().submit({
				method:"POST",
				waitMsg:"保存中,请稍后...",
				url : "/walker/info/saveInfo",
				success: function(form, action) {
						if(action.result.success){
							Ext.Msg.alert("提示","保存成功" ,function() {
								com.walker.file.info.editWin.close();
							});
							com.walker.file.info.queryInfo();
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

com.walker.file.info.deleteInfo=function(){
	var selRecords= com.walker.common.getSelectRecord(com.walker.file.info.infoGridPnl,false);
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
			url : '/walker/info/deleteInfo',
			success : function(response) {
				var result = Ext.decode(response.responseText);
				if (result.success) {
					Ext.Msg.alert("提示", "删除成功!");
					com.walker.file.info.queryInfo();
				}else{
					Ext.Msg.alert("提示", result.success);
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
			items : [com.walker.file.info.mainPanel(obj)]
		}]
	});
	com.walker.permission.userpermission("20004");
});

//资讯类型
com.walker.file.info.info_type = new Ext.data.Store({
	url : '/walker/user/getPubCode?PUB_CODE_NAME=INFO_TYPE',
	reader : new Ext.data.JsonReader({
		fields : ["VALUE","CODE"],
		root : 'data'
	})
});
com.walker.file.info.info_type.load();