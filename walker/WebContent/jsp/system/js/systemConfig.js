Ext.namespace("com.walker.systemConfig");

com.walker.systemConfig.queryFormPnl = null;
com.walker.systemConfig.systemConfigGridPnl = null;

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
			items : [com.walker.systemConfig.getQueryForm(),com.walker.systemConfig.getGridPnl()]
		}]
	});
});

com.walker.systemConfig.getQueryForm = function(){
	
	var queryForm = new Ext.Panel({
		id : 'systemConfigPanel',
		layout : 'column',
		region : 'north',
		rowHeight : 0,
		frame:true,
		buttonAlign : 'left',
		autoHeight:false,
		height:80,
		bodyStyle : 'padding:10px 0px 0px 0px',
		tbar:[{
			text : '编辑',
			iconCls : 'btnIconUpdate',
			handler : function() {
				com.walker.systemConfig.updateSysConfig();
			}
		},'-',{
			text : '刷新缓存',
			iconCls : 'btnIconRecover',
			id : "1000103",
			handler : function() {
				com.walker.systemConfig.flushSysConfig();
			}
		},'->',{
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.systemConfig.querySystemConfig();
			}
		},'-',{
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("codeInput").setValue();
				Ext.getCmp("nameInput").setValue();
			}
		}],
		items : [{
			layout : 'form',
			autoHeight : true,
			border : false,
			width : 300,
			labelWidth : 80,
			labelAlign : 'right',
			items : [{
				xtype : 'textfield',
				fieldLabel : '编码',
				name : 'code',
				id:'codeInput',
				anchor : '100%'
			}]
		},{
			layout : 'form',
			autoHeight : true,
			border : false,
			width : 300,
			labelWidth : 80,
			labelAlign : 'right',
			items : [{
				xtype : 'textfield',
				fieldLabel : '名称',
				name : 'name',
				id:'nameInput',
				anchor : '100%'
			}]
		}]
	});
	
	com.walker.systemConfig.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.systemConfig.getGridPnl = function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["ID","CODE","NAME","VALUE"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/walker/user/getSysConfigListPage',
		reader : reader,
		baseParams : {
			limit : 15,
			start: 0
		}
	});
	
	// 创建列模型的多选框模型.
    var sm = new Ext.grid.CheckboxSelectionModel({ singleSelect: true });	
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
				header : "编码",
				dataIndex : "CODE",
				sortable : true,
				width : 250,
				align : 'left'
			}, {
				header : "名称",
				dataIndex : "NAME",
				sortable : true,
				width : 250,
				align : 'left'
			}, {
				header : "值",
				dataIndex : "VALUE",
				sortable : true,
				width : 400,
				align : 'left'
			}
	]);
	
	var grid = new Ext.grid.GridPanel({
			layout : 'fit',
			title : '系统配置列表',
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
	
	com.walker.systemConfig.systemConfigGridPnl = grid;
	grid.store.load();
	return grid;
};

com.walker.systemConfig.querySystemConfig=function(){
	com.walker.systemConfig.systemConfigGridPnl.store.baseParams.code = Ext.getCmp("codeInput").getValue();
	com.walker.systemConfig.systemConfigGridPnl.store.baseParams.name = Ext.getCmp("nameInput").getValue();
	com.walker.systemConfig.systemConfigGridPnl.store.load();
};

com.walker.systemConfig.record=null;
com.walker.systemConfig.updateSysConfig=function(){
	var rec = com.walker.common.getSelectRecord(com.walker.systemConfig.systemConfigGridPnl,true);
	if(rec){
		com.walker.systemConfig.record=rec;
		com.walker.systemConfig.showRecordWin("编辑");
	}
};

com.walker.systemConfig.showRecordWin=function(titleName){
	com.walker.systemConfig.editWin = com.walker.systemConfig.createEditWin(titleName);
	com.walker.systemConfig.formPanel.find("name", "id")[0].setValue(com.walker.systemConfig.record.get('ID'));
	com.walker.systemConfig.formPanel.find("name", "code")[0].setValue(com.walker.systemConfig.record.get('CODE'));
	com.walker.systemConfig.formPanel.find("name", "name")[0].setValue(com.walker.systemConfig.record.get('NAME'));
	com.walker.systemConfig.formPanel.find("name", "value")[0].setValue(com.walker.systemConfig.record.get('VALUE'));
	com.walker.systemConfig.editWin.show();
};


com.walker.systemConfig.createEditWin=function(titleName){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 400,
		height : 250,
		modal : true,
		closeAction:'close',
		constrain : true,
		title : titleName,
		tbar : [ {
			text : '确定',
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.systemConfig.saveSysConfigInfo();
			}
		}, '-', {
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.systemConfig.editWin.close();
			}
		} ],
		items : [
		    com.walker.systemConfig.editWinForm()
		]
	});
	return panelTmp;
};

com.walker.systemConfig.formPanel=null;
com.walker.systemConfig.editWinForm=function(){
	var formPanel = new Ext.FormPanel({
	    frame : true,
	    layout : 'form',
	    region : 'north',
	    labelWidth : 60,
		labelAlign : "right",
		bodyStyle : 'padding-top: 10px;',
	    items: [{
        	layout : "column",
        	items : [{
        		columnWidth : .95,
			    layout : "form",
			    items : [{
			       xtype : "textfield",
			       fieldLabel : "ID",
			       width : 250,
			       name : 'id',
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
			       fieldLabel : "CODE",
			       width : 250,
			       name : 'code',
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
			       fieldLabel : "名称",
			       width : 250,
			       name : 'name',
			       allowBlank: false
			      }]
			 }]
        },{
        	layout : "column",
        	items : [{
        		columnWidth : .95,
			    layout : "form",
			    items : [{
			       xtype : "textfield",
			       fieldLabel : "值",
			       width : 250,
			       name : 'value',
			       allowBlank: false
			      }]
			 }]
        }]
	});
	com.walker.systemConfig.formPanel=formPanel;
	return formPanel;
};

com.walker.systemConfig.saveSysConfigInfo=function(){
	if(com.walker.systemConfig.formPanel.getForm().isValid()){
		
		com.walker.systemConfig.formPanel.getForm().submit({
			method:"POST",
			waitMsg:"保存中,请稍后...",
			url : "/walker/user/saveSysConfigInfo",
			params : {
				ID : com.walker.systemConfig.formPanel.find("name", "id")[0].getValue(),
				CODE : com.walker.systemConfig.formPanel.find("name", "code")[0].getValue(),
				NAME : com.walker.systemConfig.formPanel.find("name", "name")[0].getValue(),
				VALUE : com.walker.systemConfig.formPanel.find("name", "value")[0].getValue()
			},
			success: function(response,action) {
				if(action.result.success){
					Ext.Msg.alert("提示","保存成功");
					com.walker.systemConfig.systemConfigGridPnl.store.reload();
				}else{
					Ext.Msg.alert("提示",action.result.err_msg);
				}
				com.walker.systemConfig.editWin.close();
		     },
			 failure: function(response) {
				Ext.Msg.alert("提示","保存失败");
			 }
		});
	}
};

com.walker.systemConfig.flushSysConfig=function(){
	var sumBtn = function(btn){
		if(btn!='yes'){return;}
		Ext.Ajax.request({
			url: '/walker/user/flushSysConfig',
			success: function(response) {
				var result = Ext.decode(response.responseText);
				if(result.success){
					Ext.Msg.alert("提示","刷新成功!");
					com.walker.systemConfig.systemConfigGridPnl.store.reload();
				}else{
					Ext.Msg.alert("提示",result.err_msg);
				}
		     },
			failure: function(response) {
				Ext.Msg.alert("提示","刷新失败!");
			}
		});
	};
	Ext.Msg.confirm('提示',"确定刷新缓存吗?",sumBtn);
};