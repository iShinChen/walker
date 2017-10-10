Ext.namespace("com.walker.publicCode");

com.walker.publicCode.queryFormPnl = null;
com.walker.publicCode.publicCodeGridPnl = null;
com.walker.publicCode.formPanel = null;

com.walker.publicCode.parentId = null;

com.walker.publicCode.mainPanel=function(){
	var mainPanel = new Ext.Panel( {
		layout : 'border',
		border : false,
		region : 'center',
		tbar:[{
			text : '添加字典',
			iconCls : 'btnIconAddIn',
			id : "c54c685a1e6f4ec98a9cf4cb46b202a5",
//			hidden : true,
			handler : function() {
				com.walker.publicCode.add();
			}
		},'-',{
			text : '维护字典内容',
			iconCls : 'btnIconAddIn',
			id : "a1db306051f64768a0d7122edf357c8a",
//			hidden : true,
			handler : function() {
				com.walker.publicCode.createContent();
			}
		},'-',{
			text : '编辑',
			iconCls : 'btnIconUpdate',		
			id : "7d635c8b911d42ff9dbe3a5cac6c8f04",
//			hidden : true,
			handler : function() {
				com.walker.publicCode.update();
			}
		},'-',{
			// 删除
			text : '删除',
			iconCls : 'btnIconDel',
			id : "de5bf83258af45c0a7145201d68e29ec",
//			hidden : true,
			handler : function() {
				com.walker.publicCode.deletePublicCode();
			}
		},'-',{
			// 刷新
			text : '刷新',
			iconCls : 'btnIconRecover',
			id : "cd1d0c15bbf94fb789906195498944f8",
//			hidden : true,
			handler : function() {
				com.walker.publicCode.flushBookConfig();
			}
		},'->',{
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.publicCode.queryPublicCode();
			}
		},'-',{
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("nameInput").setValue();
			}
		}],
		items : [ com.walker.publicCode.getQueryForm(),com.walker.publicCode.getGridPnl()]
	});
	return mainPanel;
};

com.walker.publicCode.getQueryForm = function(){
	
	var queryForm = new Ext.Panel({
		frame : true,
		layout : 'form',
		region : 'north',
		labelWidth : 80,
		border : false,
		height : 40,
		items : [{
			layout : 'form',
			autoHeight : true,
			border : false,
			width : 250,
			labelWidth : 80,
			labelAlign : 'right',
			items : [{
				xtype : 'textfield',
				fieldLabel : '字典名称',
				name : 'name',
				id:'nameInput',
				anchor : '100%'
			}]
		}]
	});
	
	com.walker.publicCode.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.publicCode.getGridPnl = function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["ID","NAME","PARENT_ID","CODE","VALUE","ORDERINDEX","STATUS","DESC"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/walker/user/getPublicCodeListPage',
		reader : reader,
		baseParams : {
			limit : 15,
			start: 0,
			parentId : '0'
		}
	});
	
	// 创建列模型的多选框模型.
    var sm = new Ext.grid.CheckboxSelectionModel();	
	var model = new Ext.grid.ColumnModel([sm,
	                                      
      new Ext.grid.RowNumberer({
    	  	header : "序号",
			align : 'left',
			width : 50,
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
				dataIndex : "ID",
				sortable : true,
				hidden : true,
				width : 250,
				align : 'center'
			},{
				header : "字典名称",
				dataIndex : "NAME",
				sortable : true,
				width : 180,
				align : 'left'
			},{
				header : "字典编码",
				dataIndex : "CODE",
				sortable : true,
				width : 200,
				align : 'left'
			},{
				header : "排序号",
				dataIndex : "ORDERINDEX",
				sortable : true,
				width : 100,
				align : 'right'
			},{
				header : "状态",
				dataIndex : "STATUS",
				sortable : true,
				width : 100,
				align : 'center',
				renderer : function(value) {
					if (value == '0') {
						return '有效';
					}else if (value == '1') {
						return '无效';
					}
					return "";
				}
			},{
				header : "描述",
				dataIndex : "DESC",
				sortable : true,
				width : 200,
				align : 'left'
			}
	]);
	
	var grid = new Ext.grid.GridPanel({
			layout : 'fit',
			title : '代码列表',
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
	
	com.walker.publicCode.publicCodeGridPnl = grid;
	grid.store.load();
	return grid;
};

com.walker.publicCode.queryPublicCode = function() {
	com.walker.publicCode.publicCodeGridPnl.store.baseParams.name = Ext.getCmp("nameInput").getValue();
	com.walker.publicCode.publicCodeGridPnl.store.load();
};

com.walker.publicCode.editWin=null;
com.walker.publicCode.recData=null;
com.walker.publicCode.createType=null;
com.walker.publicCode.add=function(type){
	com.walker.publicCode.createType="1";
	com.walker.publicCode.showRecordWin("字典-新增");
};

com.walker.publicCode.update=function(){
	var rec = com.walker.common.getSelectRecord(com.walker.publicCode.publicCodeGridPnl,true);
	if(rec){
		
		if(rec.get('STATUS')=="1"){
			Ext.Msg.alert("提示","该字典已删除！");
			return false;
		}
		
		com.walker.publicCode.createType="2";
		Ext.Ajax.request({
			url: '/walker/user/selectPublicCodeInfoById',
			params : {
				ID:rec.get('ID')
			},
			success: function(response) {
				var result = Ext.decode(response.responseText);
				if(result.success){
					com.walker.publicCode.recData=result.data;
					com.walker.publicCode.showRecordWin("字典-编辑");
				}else{
					Ext.Msg.alert("提示","获取字典信息失败!");
				}
		     },
			failure: function(response) {
				Ext.Msg.alert("提示","获取字典信息失败!");
			}
		});
	}
};


com.walker.publicCode.showRecordWin=function(titleName){
	com.walker.publicCode.editWin=com.walker.publicCode.createEditWin(titleName);
	if(com.walker.publicCode.createType=="2"){
		com.walker.publicCode.formPanel.find("name", "ID")[0].setValue(com.walker.publicCode.recData.ID);
		com.walker.publicCode.formPanel.find("name", "NAME")[0].setValue(com.walker.publicCode.recData.NAME);
		com.walker.publicCode.formPanel.find("name", "CODE")[0].setValue(com.walker.publicCode.recData.CODE);
		com.walker.publicCode.formPanel.find("name", "ORDERINDEX")[0].setValue(com.walker.publicCode.recData.ORDERINDEX);
		com.walker.publicCode.formPanel.find("name", "DESC")[0].setValue(com.walker.publicCode.recData.DESC);
	}
	com.walker.publicCode.editWin.show();
};

com.walker.publicCode.createEditWin=function(titleName){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 500,
		height : 320,
		modal : true,
		constrain : true,
		closeAction:'close',
		title : titleName,
		tbar : [ {
			text : '保存',
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.publicCode.savePublicCodeInfo();
			}
		},'-',{
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.publicCode.editWin.close();
			}
		} ],
		items : [com.walker.publicCode.editWinForm()]
	});
	return panelTmp;
};

com.walker.publicCode.formPanel=null;
com.walker.publicCode.editWinForm=function(){
	var formPanel = new Ext.FormPanel({
	    frame : true,
	    layout : 'form',
	    region : 'north',
	    labelWidth : 100,
		labelAlign : "right",
		bodyStyle: 'padding-top: 15px;',
	    items: [
            {
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "代码ID",
				       width : 300,
				       name : 'ID',
				       hidden : true
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "字典名称",
				       width : 300,
				       name : 'NAME',
				       allowBlank : false,
				       regex:/^[\u4e00-\u9fa5a-zA-Z0-9_]{1,100}$/,
				       regexText : '字典名称只能输入中文、数字、字母、下划线且长度不超过100'
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "字典编码",
				       width : 300,
				       name : 'CODE',
				       allowBlank : false,
				       regex:/^[a-zA-Z0-9_]{1,50}$/,
				       regexText : '字典编码只能输入数字、字母、下划线且长度不超过50'
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       value : "0",
				       fieldLabel : "PARENT_ID",
				       width : 300,
				       name : 'PARENT_ID',
				       hidden : true
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "排列序号",
				       width : 300,
				       name : 'ORDERINDEX',
				       allowBlank : false,
				       regex:/^\d{1,11}$/,
				       regexText : '排列序号只能输入数字且长度不超过11'
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textarea",
				       fieldLabel : "描述",
				       width : 300,
				       name : 'DESC',
				       allowBlank : true,
				       regex:/^[\u4e00-\u9fa5a-zA-Z0-9_]{1,200}$/,
				       regexText : '描述只能输入中文、数字、字母、下划线且长度不超过200'
				      }]
				 }]
            }
        ]
	});
	com.walker.publicCode.formPanel=formPanel;
	return formPanel;
};

com.walker.publicCode.savePublicCodeInfo=function(){
	
	if(com.walker.publicCode.formPanel.getForm().isValid()){
		
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			com.walker.publicCode.formPanel.getForm().submit({
				method:"POST",
				waitMsg:"保存中,请稍后...",
				url : "/walker/user/savePublicCodeInfo",		
				success: function(form, action) {
					if(action.result.success){
						Ext.Msg.alert("提示","保存成功" ,function() {
							com.walker.publicCode.editWin.close();
						});
						com.walker.publicCode.queryPublicCode();
					} else {
						Ext.Msg.alert("提示", "字典"+action.result.desc);
					}
			     },
				 failure: function(form, action) {
			    	 Ext.Msg.alert("提示","字典"+action.result.desc);
				 }
			});
			
		};
		Ext.Msg.confirm('提示',"确定保存吗？",sumBtn);
	}
};

com.walker.publicCode.deletePublicCode=function(){
	var selReds = com.walker.common.getSelectRecord(com.walker.publicCode.publicCodeGridPnl,true);
	if(selReds){
		
		if(selReds.get('STATUS')=="1"){
			Ext.Msg.alert("提示","该字典已删除！");
			return false;
		}
		
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			Ext.Ajax.request({
				url: '/walker/user/deletePublicCodeInfo',
				params: {
				   codeId:selReds.get('ID')
				},
				success: function(response) {
					var result = Ext.decode(response.responseText);
					if(result.success){
						Ext.Msg.alert("提示","删除成功!");
						com.walker.publicCode.queryPublicCode();
					}else
					{
						Ext.Msg.alert("提示",result.errorMsg);
					}
				},
				failure: function(response) {
					Ext.Msg.alert("提示",result.errorMsg);
				}
			});
		};
		Ext.Msg.confirm('提示',"删除后将永久无效，会对系统公共数据产生影响，确定继续删除吗?",sumBtn);
	}
};	


com.walker.publicCode.flushBookConfig=function(){
	var sumBtn = function(btn){
		if(btn!='yes'){return;}
		Ext.Ajax.request({
			url: '/walker/user/flushBookConfig',
			success: function(response) {
				var result = Ext.decode(response.responseText);
				if(result.success){
					Ext.Msg.alert("提示","刷新成功!");
				}else{
					Ext.Msg.alert("提示","刷新失败!");
				}
		     },
			failure: function(response) {
				Ext.Msg.alert("提示","刷新失败!");
			}
		});
	};
	Ext.Msg.confirm('提示',"确定刷新缓存吗?",sumBtn);
};

com.walker.publicCode.createContent=function(){
	var rec = com.walker.common.getSelectRecord(com.walker.publicCode.publicCodeGridPnl,true);
	if(rec){
		Ext.Ajax.request({
			url: '/walker/user/selectPublicCodeInfoById',
			params : {
				ID:rec.get('ID')
			},
			success: function(response) {
				var result = Ext.decode(response.responseText);
				if(result.success){
					com.walker.publicCode.recData=result.data;
					com.walker.publicCode.showContentRecordWin(com.walker.publicCode.recData.NAME+"—字典内容维护");
				}else{
					Ext.Msg.alert("提示","获取字典信息失败!");
				}
		     },
			failure: function(response) {
				Ext.Msg.alert("提示","获取字典信息失败!");
			}
		});
	}
};

com.walker.publicCode.showContentRecordWin=function(titleName){
	com.walker.publicCode.editContentWin=com.walker.publicCode.createContentEditWin(titleName);
	com.walker.publicCode.editContentWin.show();
};

com.walker.publicCode.createContentEditWin=function(titleName){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 750,
		height : 450,
		modal : true,
		constrain : true,
		closeAction:'close',
		title : titleName,
		items : [com.walker.publicCode.contentMainPanel()]
	});
	return panelTmp;
};

com.walker.publicCode.contentMainPanel=function(){
	var mainPanel = new Ext.Panel( {
		layout : 'border',
		border : false,
		region : 'center',
		tbar:[{
			text : '新增',
			iconCls : 'btnIconAddIn',
			handler : function() {
				com.walker.publicCode.addContent();
			}
		},{
			text : '编辑',
			iconCls : 'btnIconUpdate',		
			handler : function() {
				com.walker.publicCode.updateContent();
			}
		},{
			// 删除
			text : '删除',
			iconCls : 'btnIconDel',
			handler : function() {
				com.walker.publicCode.deleteContent();
			}
		},'->',{
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.publicCode.queryPublicCodeContent();
			}
		},{
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("codeInput").setValue();
				Ext.getCmp("valueInput").setValue();
			}
		}],
		items : [ com.walker.publicCode.getContentQueryForm(),com.walker.publicCode.getContentGridPnl()]
	});
	return mainPanel;
};

com.walker.publicCode.queryCntentFormPnl=null;

com.walker.publicCode.getContentQueryForm = function(){
	var queryForm = new Ext.Panel({
		frame : true,
		layout : 'form',
		region : 'north',
		labelWidth : 80,
		border : false,
		height : 40,
		items : [ {
			layout : "column",
			items : [ {
				columnWidth : .45,
				layout : "form",
				labelAlign : 'right',
				items : [ {
					xtype : "textfield",
					fieldLabel : "字典项编码",
					anchor : '90%',
					name : 'code',
					id : 'codeInput'
				} ]
			}, {
				columnWidth : .45,
				layout : "form",
				labelAlign : 'right',
				items : [ {
					xtype : 'textfield',
					fieldLabel : '字典项值',
					name : 'value',
					id : 'valueInput',
					anchor : '90%'
				} ]
			}]
		}]
	});
	com.walker.publicCode.queryCntentFormPnl = queryForm;
	return queryForm;
};

com.walker.publicCode.publicCodeContentGridPnl=null;

com.walker.publicCode.getContentGridPnl=function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["ID","NAME","PARENT_ID","CODE","VALUE","ORDERINDEX","STATUS","DESC"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/walker/user/getPublicCodeListPage',
		reader : reader,
		baseParams : {
			limit : 15,
			start: 0,
			parentId : com.walker.publicCode.recData.CODE
		}
	});
	
	// 创建列模型的多选框模型.
    var sm = new Ext.grid.CheckboxSelectionModel();	
	var model = new Ext.grid.ColumnModel([sm,
	                                      
      new Ext.grid.RowNumberer({
    	  	header : "序号",
			align : 'left',
			width : 50,
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
				dataIndex : "ID",
				sortable : true,
				hidden : true,
				width : 250,
				align : 'center'
			}, {
				header : "字典项编码",
				dataIndex : "CODE",
				sortable : true,
				width : 100,
				align : 'left'
			}, {
				header : "字典项名称",
				dataIndex : "VALUE",
				sortable : true,
				width : 200,
				align : 'left'
			},{
				header : "排序号",
				dataIndex : "ORDERINDEX",
				sortable : true,
				width : 80,
				align : 'right'
			},{
				header : "状态",
				dataIndex : "STATUS",
				sortable : true,
				width : 100,
				align : 'center',
				renderer : function(value) {
					if (value == '0') {
						return '有效';
					}else if (value == '1') {
						return '无效';
					}
					return "";
				}
			},{
				header : "描述",
				dataIndex : "DESC",
				sortable : true,
				width : 200,
				align : 'left'
			}
	]);
	
	var grid = new Ext.grid.GridPanel({
		layout : 'fit',
		title : '代码列表',
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
	
	com.walker.publicCode.publicCodeContentGridPnl = grid;
	grid.store.load();
	return grid;
	
};

com.walker.publicCode.queryPublicCodeContent=function(){
	com.walker.publicCode.publicCodeContentGridPnl.store.baseParams.code = Ext.getCmp("codeInput").getValue();
	com.walker.publicCode.publicCodeContentGridPnl.store.baseParams.value = Ext.getCmp("valueInput").getValue();
	com.walker.publicCode.publicCodeContentGridPnl.store.load();
};


com.walker.publicCode.ceditWin=null;
com.walker.publicCode.cType=null;
com.walker.publicCode.addContent=function(){
	com.walker.publicCode.cType="1";
	com.walker.publicCode.cshowRecordWin("字典项-新增");
};

com.walker.publicCode.updateContent=function(){
	var rec = com.walker.common.getSelectRecord(com.walker.publicCode.publicCodeContentGridPnl,true);
	if(rec){
		com.walker.publicCode.cType="2";
		Ext.Ajax.request({
			url: '/walker/user/selectPublicCodeInfoById',
			params : {
				ID:rec.get('ID')
			},
			success: function(response) {
				var result = Ext.decode(response.responseText);
				if(result.success){
					com.walker.publicCode.recData=result.data;
					com.walker.publicCode.cshowRecordWin("字典项-编辑");
				}else{
					Ext.Msg.alert("提示","获取字典项信息失败!");
				}
		     },
			failure: function(response) {
				Ext.Msg.alert("提示","获取字典项信息失败!");
			}
		});
	}	
};

com.walker.publicCode.cshowRecordWin=function(titleName){
	com.walker.publicCode.ceditWin=com.walker.publicCode.cncreateEditWin(titleName);
	if(com.walker.publicCode.cType=="1"){
		com.walker.publicCode.nformPanel.find("name", "PARENT_ID")[0].setValue(com.walker.publicCode.recData.CODE);
		com.walker.publicCode.nformPanel.find("name", "NAME")[0].setValue(com.walker.publicCode.recData.NAME);
	}else{
		com.walker.publicCode.nformPanel.find("name", "ID")[0].setValue(com.walker.publicCode.recData.ID);
		com.walker.publicCode.nformPanel.find("name", "PARENT_ID")[0].setValue(com.walker.publicCode.recData.PARENT_ID);
		com.walker.publicCode.nformPanel.find("name", "NAME")[0].setValue(com.walker.publicCode.recData.NAME);
		com.walker.publicCode.nformPanel.find("name", "CODE")[0].setValue(com.walker.publicCode.recData.CODE);
		com.walker.publicCode.nformPanel.find("name", "VALUE")[0].setValue(com.walker.publicCode.recData.VALUE);
		com.walker.publicCode.nformPanel.find("name", "ORDERINDEX")[0].setValue(com.walker.publicCode.recData.ORDERINDEX);
		com.walker.publicCode.nformPanel.find("name", "DESC")[0].setValue(com.walker.publicCode.recData.DESC);
	}
	com.walker.publicCode.ceditWin.show();
};

com.walker.publicCode.cncreateEditWin=function(titleName){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 500,
		height : 320,
		modal : true,
		constrain : true,
		closeAction:'close',
		title : titleName,
		tbar : [ {
			text : '保存',
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.publicCode.savePublicCodeContent();
			}
		},'-',{
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.publicCode.ceditWin.close();
			}
		} ],
		items : [com.walker.publicCode.neditWinForm()]
	});
	return panelTmp;
};

com.walker.publicCode.nformPanel=null;
com.walker.publicCode.neditWinForm=function(){
	var formPanel = new Ext.FormPanel({
	    frame : true,
	    layout : 'form',
	    region : 'north',
	    labelWidth : 100,
		labelAlign : "right",
		bodyStyle: 'padding-top: 15px;',
	    items: [
            {
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "代码ID",
				       width : 300,
				       name : 'ID',
				       hidden : true
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "字典编码",
				       width : 300,
				       name : 'PARENT_ID',
				       hidden : true
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "字典名称",
				       width : 300,
				       name : 'NAME',
				       hidden : true
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "字典项编码",
				       width : 300,
				       name : 'CODE',
				       allowBlank : false,
				       regex:/^[a-zA-Z0-9_]{1,50}$/,
				       regexText : '字典编码只能输入数字、字母、下划线且长度不超过50'
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "字典项名称",
				       width : 300,
				       name : 'VALUE',
				       allowBlank : false,
				       regex:/^[\u4e00-\u9fa5a-zA-Z0-9\/_、-]{1,50}$/,
				       regexText : '字典名称只能输入中文、数字、字母、下划线且长度不超过50'
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       fieldLabel : "排列序号",
				       width : 300,
				       name : 'ORDERINDEX',
				       allowBlank : false,
				       regex:/^\d{1,11}$/,
				       regexText : '排列序号只能输入数字且长度不超过11'
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textarea",
				       fieldLabel : "描述",
				       width : 300,
				       name : 'DESC',
				       allowBlank : true,
				       regex:/^[\u4e00-\u9fa5a-zA-Z0-9_]{1,200}$/,
				       regexText : '描述只能输入中文、数字、字母、下划线且长度不超过200'
				      }]
				 }]
            }
        ]
	});
	com.walker.publicCode.nformPanel=formPanel;
	return formPanel;
};

com.walker.publicCode.savePublicCodeContent=function(){
	
	if(com.walker.publicCode.nformPanel.getForm().isValid()){
		
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			com.walker.publicCode.nformPanel.getForm().submit({
				method:"POST",
				waitMsg:"保存中,请稍后...",
				url : "/walker/user/savePublicCodeInfo",		
				success: function(form, action) {
					if(action.result.success){
						Ext.Msg.alert("提示","保存成功" ,function() {
							com.walker.publicCode.ceditWin.close();
						});
						com.walker.publicCode.queryPublicCodeContent();
					} else {
						Ext.Msg.alert("提示", "字典项"+action.result.desc);
					}
			     },
				 failure: function(form, action) {
			    	 Ext.Msg.alert("提示","字典项"+action.result.desc);
				 }
			});
			
		};
		Ext.Msg.confirm('提示',"确定保存吗？",sumBtn);
	}
};

com.walker.publicCode.deleteContent=function(){
	var selReds = com.walker.common.getSelectRecord(com.walker.publicCode.publicCodeContentGridPnl,true);
	if(selReds){
		
		if(selReds.get('STATUS')=="1"){
			Ext.Msg.alert("提示","该字典项已删除！");
			return false;
		}
		
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			Ext.Ajax.request({
				url: '/walker/user/deletePublicCodeInfo',
				params: {
				   codeId:selReds.get('ID')
				},
				success: function(response) {
					var result = Ext.decode(response.responseText);
					if(result.success){
						Ext.Msg.alert("提示","删除成功!");
						com.walker.publicCode.queryPublicCodeContent();
					}else
					{
						Ext.Msg.alert("提示",result.errorMsg);
					}
				},
				failure: function(response) {
					Ext.Msg.alert("提示",result.errorMsg);
				}
			});
		};
		Ext.Msg.confirm('提示',"删除后将永久无效，会对系统公共数据产生影响，确定继续删除吗?",sumBtn);
	}
};

Ext.onReady(function() {
	
	new Ext.Viewport({
		id : 'row-panel',
		bodyStyle : 'padding:5px',
		layout : 'fit',
		title : 'Row Layout',
		items : [{
			rowHeight : 0,
			xtype : 'panel',
			layout : 'border',
			items : [com.walker.publicCode.mainPanel()]
		}]
	});
	com.walker.permission.userpermission("32fabb14643e47b1a3e95e5804c9058b");
});
