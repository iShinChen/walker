Ext.namespace("com.walker.xml.xmlConfig");

com.walker.xml.xmlConfig.getQueryForm = function() {
	com.walker.xml.xmlConfig.queryFormPnl = new Ext.FormPanel( {
		layout : 'form',
		region : 'north',
		labelWidth : 120,
		labelAlign : 'right',
		autoHeight : false,
		height : 70,
		bodyStyle : 'padding-top: 10px;',
		margins : '0 0 4 0',
//		border: false,
		style: 'border-bottom-width: 1px;',
		tbar : [{
			text : '编辑',
			iconCls : 'btnIconUpdate',
			handler : function() {
				com.walker.xml.xmlConfig.update();
			}
		}, '->', {
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.xml.xmlField.query();
			}
		}, {
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("nameInput").setValue('');
				Ext.getCmp("codeInput").setValue('');
			}
		} ],
		items : [ {
			layout : "column",
			border : false,
			items : [ {
				width : 300,
				layout : "form",
				border : false,
				items : [ {
					xtype : 'textfield',
					fieldLabel : '对象名称',
					id : 'nameInput',
					anchor : '100%'
				} ]
			}]
		} ]
	});

	return com.walker.xml.xmlConfig.queryFormPnl;
};

com.walker.xml.xmlConfig.getGridPnl = function() {

	var reader = new Ext.data.JsonReader( {
		fields : [ "ID", "CODE","NAME", "XML_TYPE","STATUS"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store( {
		url : '/walker/xmlConfig/configList',
		reader : reader
	});
	
	store.on('beforeload', function() {
		Ext.getBody().mask('数据加载中...', 'x-mask-loading');
	});
	store.on('load', function() {
		Ext.getBody().unmask();
	});

	// 创建列模型的多选框模型.
	var sm = new Ext.grid.CheckboxSelectionModel({singleSelect: true});
	var model = new Ext.grid.ColumnModel( [ sm,

	new Ext.grid.RowNumberer( {
		header : "序号",
		align : 'left',
		width : 50,
		renderer : function(value, metadata, record, rowIndex) {
			var startRow = '';
			if (store.lastOptions.params) {
				startRow = store.lastOptions.params.start;
				if (isNaN(startRow)) {
					startRow = 0;
				}
			} else {
				startRow = 0;
			}
			return startRow + rowIndex + 1;
		}
	}), {
		header : "ID",
		dataIndex : "ID",
		sortable : true,
		hidden : true,
		width : 100,
		align : 'center'
	}, {
		header : "对象名称",
		dataIndex : "NAME",
		sortable : true,
		width : 200,
		align : 'center'
	}, {
		header : "对象编码",
		dataIndex : "CODE",
		sortable : true,
		width : 150,
		align : 'center'
	}, {
		header : "工单类型",
		dataIndex : "XML_TYPE",
		sortable : true,
		width : 200,
		align : 'center',
		renderer : function(value) {
			if (value == 'SERIES') {
				return '合集工单';
			} else if (value == 'PROGRAM') {
				return '分集工单';
			} else if (value == 'MOVIE') {
				return '片源工单';
			}
			return "";
		}
	}, {
		header : "启用状态",
		dataIndex : "STATUS",
		sortable : true,
		width : 200,
		align : 'center',
		renderer : function(value) {
			if (value == '01') {
				return '<font color="green">已启用</font>';
			} else if (value == '00') {
				return '未启用';
			}
			return "";
		}
	}]);

	var grid = new Ext.grid.GridPanel( {
		layout : 'fit',
		title : '字段列表',
		autoWidth : true,
		autoScroll : true,
		cm : model,
		sm : sm,
		store : store,
		stripeRows : true,
		region : 'center',
//		border: false,
		style: 'border-top-width: 1px;',
		bbar : new Ext.PagingToolbar( {
			pageSize : 15,
			store : store,
			displayInfo : true
		})
	});

	com.walker.xml.xmlConfig.gridPnl = grid;
	grid.store.load();
	return grid;
};

com.walker.xml.xmlConfig.query = function() {
	com.walker.xml.xmlConfig.gridPnl.store.baseParams.name = Ext.getCmp(
			"nameInput").getValue();
	com.walker.xml.xmlConfig.gridPnl.store.baseParams.code = Ext.getCmp(
			"codeInput").getValue();
	com.walker.xml.xmlConfig.gridPnl.store.load();
};

com.walker.xml.xmlConfig.editWin = null;
com.walker.xml.xmlConfig.createType = null;

com.walker.xml.xmlConfig.update = function() {
	var rec = com.walker.xml.xmlConfig.gridPnl.getSelectionModel().getSelected();
	if (rec) {
		new Ext.Window( {
			layout : 'border',
			width : 400,
			height : 250,
			modal : true,
			constrain : true,
			closeAction : 'close',
			title : "修改对象",
			items : [ com.walker.xml.xmlConfig.editWinForm() ],
			listeners : {
				show : function() {
					com.walker.xml.xmlConfig.formPanel.getEl().mask('数据加载中...', 'x-mask-loading');
					
					Ext.Ajax.request( {
						url : '/walker/xmlConfig/getConfigById',
						params : {
							ID : rec.get('ID')
						},
						success : function(response) {
							com.walker.xml.xmlConfig.formPanel.getEl().unmask();
							try {
								var result = Ext.util.JSON.decode(response.responseText);
								if (result.success) {
									com.walker.xml.xmlConfig.formPanel.find("name", "ID")[0].setValue(result.data.ID);
									com.walker.xml.xmlConfig.formPanel.find("name", "NAME")[0].setValue(result.data.NAME);
									com.walker.xml.xmlConfig.formPanel.find("name", "CODE")[0].setValue(result.data.CODE);
									com.walker.xml.xmlConfig.formPanel.find("name", "XML_TYPE")[0].setValue(result.data.XML_TYPE);
									com.walker.xml.xmlConfig.formPanel.find("name", "STATUS")[0].setValue(result.data.STATUS);
								} else {
									Ext.Msg.alert("提示", "获取字段信息失败!");
								}
							} catch (e) {
							}
						},
						failure : function(response) {
							com.walker.xml.xmlConfig.formPanel.getEl().unmask();
							Ext.Msg.alert("提示", "获取字段信息失败!");
						}
					});
				}
			}
		}).show();
	}
	else {
		Ext.Msg.alert("提示", "请选择一条记录!");
	}
};

com.walker.xml.xmlConfig.editWinForm = function() {
	com.walker.xml.xmlConfig.formPanel = new Ext.FormPanel( {
		layout : 'form',
		region : 'center',
		labelWidth : 100,
		labelAlign : "right",
		bodyStyle : 'padding-top: 10px;',
		border: false,
		tbar : [ {
			text : '保存',
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.xml.xmlConfig.save();
			}
		}, '-', {
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				Ext.WindowMgr.getActive().close();
			}
		} ],
		items : [ {
			xtype : "textfield",
			fieldLabel : "字段ID",
			anchor : '90%',
			name : 'ID',
			hidden : true
		}, {
			xtype : "textfield",
			fieldLabel : "字段名称",
			anchor : '90%',
			name : 'NAME',
			allowBlank : false
		}, {
			xtype : "textfield",
			fieldLabel : "字段编码",
			anchor : '90%',
			name : 'CODE',
			allowBlank : false,
			regex : /^[a-zA-Z0-9_]{1,50}$/,
			regexText : '只能输入数字、字母、下划线且长度不超过50'
		}, {
			xtype : 'combo',
			fieldLabel : '工单类型',
			name : 'XML_TYPE',
            anchor : '90%',
            triggerAction: 'all',
            displayField : 'text', 
            valueField : 'value', 
            store: new Ext.data.SimpleStore({
                fields :['text','value'],
                data : [['请选择...',''],['合集工单','SERIES'],['分集工单','PROGRAM'],['片源工单','MOVIE']]
            }),
            mode: 'local',
            emptyText:'请选择...',
            blankText : '请选择',
            editable:false
		},{
			xtype : "combo",
			fieldLabel : "使用状态",
			name : "STATUS",
			anchor : '90%',
			triggerAction: 'all',
			displayField : 'text', 
			valueField : 'value', 
			store: new Ext.data.SimpleStore({
			fields :['text','value'],
				data : [['请选择...',''],['启用','01'],['停用','00']]
			}),
			mode: 'local',
			editable:false,
			allowBlank : false
		} ]
	});
	return com.walker.xml.xmlConfig.formPanel;
};

com.walker.xml.xmlConfig.save = function() {
	if (com.walker.xml.xmlConfig.formPanel.getForm().isValid()) {
		var sumBtn = function(btn) {
			if (btn != 'yes') {
				return;
			}
			com.walker.xml.xmlConfig.formPanel.getForm().submit( {
				method : "POST",
				waitMsg : "数据提交中...",
				url : "/walker/xmlConfig/saveXmlConfig",
				success : function(form, action) {
					if (action.result.success) {
						Ext.Msg.alert("提示", "保存成功", function() {
							Ext.WindowMgr.getActive().close();
						});
						com.walker.xml.xmlConfig.query();
					} else {
						Ext.Msg.alert("提示", "字段" + action.result.desc);
					}
				},
				failure : function(form, action) {
					Ext.Msg.alert("提示", "字段操作失败。");
				}
			});

		};
		Ext.Msg.confirm('提示', "确定保存吗？", sumBtn);
	}
};

Ext.onReady(function() {
	new Ext.Viewport( {
		layout : 'border',
		items : [ com.walker.xml.xmlConfig.getQueryForm(),
				com.walker.xml.xmlConfig.getGridPnl() ]
	});
});
