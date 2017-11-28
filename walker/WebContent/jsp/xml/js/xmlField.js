Ext.namespace("com.walker.xml.xmlField");

com.walker.xml.xmlField.getQueryForm = function() {
	com.walker.xml.xmlField.queryFormPnl = new Ext.FormPanel( {
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
		tbar : [ {
			text : '新增',
			iconCls : 'btnIconAddIn',
			handler : function() {
				com.walker.xml.xmlField.add();
			}
		}, {
			text : '编辑',
			iconCls : 'btnIconUpdate',
			handler : function() {
				com.walker.xml.xmlField.update();
			}
		}, {
			// 删除
			text : '删除',
			iconCls : 'btnIconDel',
			handler : function() {
				com.walker.xml.xmlField.remove();
			}
		}, '-', {
			text : '字段项维护',
			iconCls : 'btnIconAddIn',
			handler : function() {
				com.walker.xml.xmlField.manageContent();
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
					fieldLabel : '字段项名称',
					id : 'nameInput',
					anchor : '100%'
				} ]
			}, {
				width : 300,
				layout : "form",
				border : false,
				items : [ {
					xtype : "textfield",
					fieldLabel : "字段项编码",
					anchor : '100%',
					id : 'codeInput'
				} ]
			} ]
		} ]
	});

	return com.walker.xml.xmlField.queryFormPnl;
};

com.walker.xml.xmlField.getGridPnl = function() {

	var reader = new Ext.data.JsonReader( {
		fields : [ "ID", "NAME", "PARENT_ID", "CODE", "VALUE", "ORDERINDEX",
				"STATUS", "DESCRIPTION" ],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store( {
		url : '/walker/xmlField/listPage',
		reader : reader,
		baseParams : {
			limit : 15,
			start : 0,
			parentId : '0'
		}
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
		width : 250,
		align : 'center'
	}, {
		header : "字段名称",
		dataIndex : "NAME",
		sortable : true,
		width : 250,
		align : 'left'
	}, {
		header : "字段编码",
		dataIndex : "CODE",
		sortable : true,
		width : 200,
		align : 'left'
	}, {
		header : "描述",
		dataIndex : "DESCRIPTION",
		sortable : true,
		width : 300,
		align : 'left'
	} ]);

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

	com.walker.xml.xmlField.gridPnl = grid;
	grid.store.load();
	return grid;
};

com.walker.xml.xmlField.query = function() {
	com.walker.xml.xmlField.gridPnl.store.baseParams.name = Ext.getCmp(
			"nameInput").getValue();
	com.walker.xml.xmlField.gridPnl.store.baseParams.code = Ext.getCmp(
			"codeInput").getValue();
	com.walker.xml.xmlField.gridPnl.store.load();
};

com.walker.xml.xmlField.editWin = null;
com.walker.xml.xmlField.createType = null;
com.walker.xml.xmlField.add = function() {
	new Ext.Window( {
		layout : 'border',
		width : 400,
		height : 250,
		modal : true,
		constrain : true,
		closeAction : 'close',
		title : "新增字段",
		items : [ com.walker.xml.xmlField.editWinForm() ]
	}).show();
};

com.walker.xml.xmlField.update = function() {
	var rec = com.walker.xml.xmlField.gridPnl.getSelectionModel().getSelected();
	if (rec) {
		new Ext.Window( {
			layout : 'border',
			width : 400,
			height : 250,
			modal : true,
			constrain : true,
			closeAction : 'close',
			title : "新增字段",
			items : [ com.walker.xml.xmlField.editWinForm() ],
			listeners : {
				show : function() {
					com.walker.xml.xmlField.formPanel.getEl().mask('数据加载中...', 'x-mask-loading');
					
					Ext.Ajax.request( {
						url : '/walker/xmlField/getXmlFieldById',
						params : {
							ID : rec.get('ID')
						},
						success : function(response) {
							com.walker.xml.xmlField.formPanel.getEl().unmask();
							try {
								var result = Ext.util.JSON.decode(response.responseText);
								if (result.success) {
									com.walker.xml.xmlField.formPanel.find("name", "ID")[0].setValue(result.data.ID);
									com.walker.xml.xmlField.formPanel.find("name", "NAME")[0].setValue(result.data.NAME);
									com.walker.xml.xmlField.formPanel.find("name", "CODE")[0].setValue(result.data.CODE);
									com.walker.xml.xmlField.formPanel.find("name", "VALUE")[0].setValue(result.data.VALUE);
									com.walker.xml.xmlField.formPanel.find("name", "DESCRIPTION")[0].setValue(result.data.DESCRIPTION);
								} else {
									Ext.Msg.alert("提示", "获取字段信息失败!");
								}
							} catch (e) {
							}
						},
						failure : function(response) {
							com.walker.xml.xmlField.formPanel.getEl().unmask();
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

com.walker.xml.xmlField.editWinForm = function() {
	com.walker.xml.xmlField.formPanel = new Ext.FormPanel( {
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
				com.walker.xml.xmlField.save();
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
			value : "0",
			fieldLabel : "父级ID",
			anchor : '90%',
			name : 'PARENT_ID',
			hidden : true
		}, {
			xtype : "textfield",
			value : "01",
			fieldLabel : "状态",
			anchor : '90%',
			name : 'STATUS',
			hidden : true
		}, {
			xtype : "textfield",
			fieldLabel : "字段名称",
			anchor : '90%',
			name : 'NAME',
			allowBlank : false,
			regex : /^[\u4e00-\u9fa5a-zA-Z0-9_]{1,100}$/,
			regexText : '只能输入中文、数字、字母、下划线且长度不超过100!'
		}, {
			xtype : "textfield",
			fieldLabel : "字段编码",
			anchor : '90%',
			name : 'CODE',
			allowBlank : false,
			regex : /^[a-zA-Z0-9_]{1,50}$/,
			regexText : '只能输入数字、字母、下划线且长度不超过50'
		}, {
			xtype : "textarea",
			fieldLabel : "描述",
			anchor : '90%',
			name : 'DESCRIPTION',
			allowBlank : true
		} ]
	});
	return com.walker.xml.xmlField.formPanel;
};

com.walker.xml.xmlField.save = function() {
	if (com.walker.xml.xmlField.formPanel.getForm().isValid()) {
		var sumBtn = function(btn) {
			if (btn != 'yes') {
				return;
			}
			com.walker.xml.xmlField.formPanel.getForm().submit( {
				method : "POST",
				waitMsg : "数据提交中...",
				url : "/walker/xmlField/saveXmlField",
				success : function(form, action) {
					if (action.result.success) {
						Ext.Msg.alert("提示", "保存成功", function() {
							Ext.WindowMgr.getActive().close();
						});
						com.walker.xml.xmlField.query();
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

com.walker.xml.xmlField.remove = function() {
	var selReds = com.walker.xml.xmlField.gridPnl.getSelectionModel().getSelected();
	if (selReds) {
		var sumBtn = function(btn) {
			if (btn != 'yes') {
				return;
			}
			Ext.Ajax.request( {
				url : '/walker/xmlField/deleteXmlField',
				params : {
					ID : selReds.get('ID')
				},
				success : function(response) {
					try {
						var result = Ext.util.JSON.decode(response.responseText);
						if (result.success) {
							Ext.Msg.alert("提示", "删除成功!");
							com.walker.xml.xmlField.query();
						} else {
							if(result.errorMsg) {
								Ext.Msg.alert("提示", result.errorMsg);
							}
							else {
								Ext.Msg.alert("提示", "删除失败!");
							}
						}
					} catch (e) {
					}
				},
				failure : function(response) {
					Ext.Msg.alert("提示", result.errorMsg);
				}
			});
		};
		Ext.Msg.confirm('提示', "删除后可能对业务数据产生严重影响,确定继续?", sumBtn);
	}
};

com.walker.xml.xmlField.manageContent = function() {
	var rec = com.walker.xml.xmlField.gridPnl.getSelectionModel().getSelected();
	if (rec) {
		new Ext.Window( {
			layout : 'border',
			width : 750,
			height : 450,
			modal : true,
			constrain : true,
			closeAction : 'close',
			title : ("字段项维护【" + rec.get("NAME") + "】"),
			items : [ 
			    com.walker.xml.xmlField.getContentQueryForm(),
				com.walker.xml.xmlField.getContentGridPnl()
			],
			listeners : {
				show : function() {
					com.walker.xml.xmlField.contentGridPnl.store.baseParams.parentId = rec.get("ID");
					com.walker.xml.xmlField.contentGridPnl.store.load();
				}
			}
		}).show();
	}
	else {
		Ext.Msg.alert("提示", "请选择一条记录!");
	}
};

com.walker.xml.xmlField.getContentQueryForm = function() {
	com.walker.xml.xmlField.contentQueryFormPnl = new Ext.FormPanel( {
		layout : 'column',
		region : 'north',
		labelWidth : 120,
		labelAlign : 'right',
		height : 70,
		bodyStyle : 'padding-top: 10px;',
		margins : '0 0 4 0',
		border : false,
		style : 'border-bottom-width: 1px;',
		items : [ {
			width : 300,
			layout : "form",
			border: false,
			items : [ {
				xtype : 'textfield',
				fieldLabel : '字段项名称',
				id : 'cNameInput',
				anchor : '100%'
			} ]
		}, {
			width : 300,
			layout : "form",
			border: false,
			items : [ {
				xtype : "textfield",
				fieldLabel : "字段项编码",
				anchor : '100%',
				id : 'cCodeInput'
			} ]
		} ],
		tbar : [ {
			text : '新增',
			iconCls : 'btnIconAddIn',
			handler : function() {
				com.walker.xml.xmlField.addContent();
			}
		}, {
			text : '编辑',
			iconCls : 'btnIconUpdate',
			handler : function() {
				com.walker.xml.xmlField.updateContent();
			}
		}, {
			// 删除
			text : '删除',
			iconCls : 'btnIconDel',
			handler : function() {
				com.walker.xml.xmlField.deleteContent();
			}
		}, '->', {
			xtype : 'button',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.xml.xmlField.queryContent();
			}
		}, {
			xtype : 'button',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("cNameInput").setValue('');
				Ext.getCmp("cCodeInput").setValue('');
			}
		} ]
	});
	return com.walker.xml.xmlField.contentQueryFormPnl;
};

com.walker.xml.xmlField.getContentGridPnl = function() {
	var reader = new Ext.data.JsonReader( {
		fields : [ "ID", "NAME", "PARENT_ID", "CODE", "VALUE", "ORDERINDEX",
				"STATUS", "DESCRIPTION" ],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store( {
		url : '/walker/xmlField/listPage',
		reader : reader,
		baseParams : {
			limit : 15,
			start : 0
		}
	});
	
	store.on('beforeload', function() {
		Ext.WindowMgr.getActive().getEl().mask('数据加载中...', 'x-mask-loading');
	});
	store.on('load', function() {
		Ext.WindowMgr.getActive().getEl().unmask();
	});

	// 创建列模型的多选框模型.
	var sm = new Ext.grid.CheckboxSelectionModel({singleSelect : true});
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
		width : 250,
		align : 'center'
	}, {
		header : "字段项名称",
		dataIndex : "NAME",
		sortable : true,
		width : 150,
		align : 'left'
	}, {
		header : "字段项编码",
		dataIndex : "CODE",
		sortable : true,
		width : 100,
		align : 'left'
	}, {
		header : "工单字段",
		dataIndex : "VALUE",
		sortable : true,
		width : 100,
		align : 'left'
	}, {
		header : "排序号",
		dataIndex : "ORDERINDEX",
		sortable : true,
		width : 80,
		align : 'right'
	}, {
		header : "状态",
		dataIndex : "STATUS",
		sortable : true,
		width : 100,
		align : 'center',
		renderer : function(value) {
			if (value == '01') {
				return '<font color="green">启用</font>';
			} else if (value == '00') {
				return '停用';
			}
			return "";
		}
	}, {
		header : "描述",
		dataIndex : "DESCRIPTION",
		sortable : true,
		width : 200,
		align : 'left'
	} ]);

	com.walker.xml.xmlField.contentGridPnl = new Ext.grid.GridPanel( {
		layout : 'fit',
		title : '字段项列表',
		autoScroll : true,
		cm : model,
		sm : sm,
		store : store,
		stripeRows : true,
		region : 'center',
		border : false,
		style : 'border-top-width: 1px;',
		bbar : new Ext.PagingToolbar( {
			pageSize : 15,
			store : store,
			displayInfo : true
		})
	});

	return com.walker.xml.xmlField.contentGridPnl;
};

com.walker.xml.xmlField.queryContent = function() {
	com.walker.xml.xmlField.contentGridPnl.store.baseParams.code = Ext.getCmp("cCodeInput").getValue();
	com.walker.xml.xmlField.contentGridPnl.store.baseParams.name = Ext.getCmp("cNameInput").getValue();
	com.walker.xml.xmlField.contentGridPnl.store.load();
};

com.walker.xml.xmlField.addContent = function() {
	new Ext.Window( {
		layout : 'fit',
		width : 500,
		height : 320,
		modal : true,
		constrain : true,
		closeAction : 'close',
		title : "新增字段项",
		tbar : [ {
			text : '保存',
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.xml.xmlField.saveContent();
			}
		}, '-', {
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				Ext.WindowMgr.getActive().close();
			}
		} ],
		items : [ com.walker.xml.xmlField.contenEditWinForm() ],
		listeners : {
			show: function() {
				var rec = com.walker.xml.xmlField.gridPnl.getSelectionModel().getSelected();
				if(rec) {
					com.walker.xml.xmlField.contentFormPanel.find("name", "PARENT_ID")[0].setValue(rec.get("ID"));
				}
			}
		}
	}).show();
};

com.walker.xml.xmlField.updateContent = function() {
	var rec = com.walker.xml.xmlField.contentGridPnl.getSelectionModel().getSelected();
	if (rec) {
		new Ext.Window( {
			layout : 'fit',
			width : 500,
			height : 320,
			modal : true,
			constrain : true,
			closeAction : 'close',
			title : "修改字段项",
			tbar : [ {
				text : '保存',
				iconCls : 'btnIconSave',
				handler : function() {
					com.walker.xml.xmlField.saveContent();
				}
			}, '-', {
				text : '取消',
				iconCls : 'btnIconReset',
				handler : function() {
					Ext.WindowMgr.getActive().close();
				}
			} ],
			items : [ com.walker.xml.xmlField.contenEditWinForm() ],
			listeners : {
				show: function() {
					com.walker.xml.xmlField.contentFormPanel.getEl().mask('数据加载中...', 'x-mask-loading');
					
					Ext.Ajax.request( {
						url : '/walker/xmlField/getXmlFieldById',
						params : {
							ID : rec.get('ID')
						},
						success : function(response) {
							com.walker.xml.xmlField.contentFormPanel.getEl().unmask();
							try {
								var result = Ext.util.JSON.decode(response.responseText);
								if (result.success) {
									com.walker.xml.xmlField.contentFormPanel.find("name", "ID")[0].setValue(result.data.ID);
									com.walker.xml.xmlField.contentFormPanel.find("name", "PARENT_ID")[0].setValue(result.data.PARENT_ID);
									com.walker.xml.xmlField.contentFormPanel.find("name", "NAME")[0].setValue(result.data.NAME);
									com.walker.xml.xmlField.contentFormPanel.find("name", "CODE")[0].setValue(result.data.CODE);
									com.walker.xml.xmlField.contentFormPanel.find("name", "VALUE")[0].setValue(result.data.VALUE);
									com.walker.xml.xmlField.contentFormPanel.find("name", "ORDERINDEX")[0].setValue(result.data.ORDERINDEX);
									com.walker.xml.xmlField.contentFormPanel.find("name", "STATUS")[0].setValue(result.data.STATUS);
									com.walker.xml.xmlField.contentFormPanel.find("name", "DESCRIPTION")[0].setValue(result.data.DESCRIPTION);
								} else {
									Ext.Msg.alert("提示", "获取字段项信息失败!");
								}
							} catch (e) {
							}
						},
						failure : function(response) {
							com.walker.xml.xmlField.contentFormPanel.getEl().unmask();
							Ext.Msg.alert("提示", "获取字段项信息失败!");
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

com.walker.xml.xmlField.contenEditWinForm = function() {
	com.walker.xml.xmlField.contentFormPanel = new Ext.FormPanel( {
		layout : 'form',
		region : 'north',
		labelWidth : 120,
		labelAlign : "right",
		bodyStyle : 'padding-top: 10px;',
		border : false,
		items : [ {
			xtype : "textfield",
			fieldLabel : "ID",
			anchor : '85%',
			name : 'ID',
			hidden : true
		}, {
			xtype : "textfield",
			fieldLabel : "父级ID",
			anchor : '85%',
			name : 'PARENT_ID',
			hidden : true
		}, {
			xtype : "textfield",
			fieldLabel : "字段项名称",
			anchor : '85%',
			name : 'NAME',
			allowBlank : false,
			regex : /^[\u4e00-\u9fa5a-zA-Z0-9_]{1,50}$/,
			regexText : '只能输入中文、数字、字母、下划线且长度不超过50'
		}, {
			xtype : "textfield",
			fieldLabel : "字段项编码",
			anchor : '85%',
			name : 'CODE',
			allowBlank : false,
			regex : /^[a-zA-Z0-9_]{1,50}$/,
			regexText : '只能输入数字、字母、下划线且长度不超过50'
		}, {
			xtype : "textfield",
			fieldLabel : "工单字段",
			anchor : '85%',
			name : 'VALUE',
			allowBlank : true,
			regex : /^[a-zA-Z0-9_]{1,50}$/,
			regexText : '只能输入数字、字母、下划线且长度不超过50'
		}, {
			xtype : "textfield",
			fieldLabel : "排列序号",
			anchor : '85%',
			name : 'ORDERINDEX',
			allowBlank : false,
			regex : /^\d{1,11}$/,
			regexText : '排列序号只能输入数字且长度不超过11'
		}, {
			xtype : "combo",
			fieldLabel : "状态",
			name : "STATUS",
			anchor : '85%',
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
		}, {
			xtype : "textarea",
			fieldLabel : "描述",
			anchor : '85%',
			name : 'DESCRIPTION',
			allowBlank : true
		} ]
	});
	return com.walker.xml.xmlField.contentFormPanel;
};

com.walker.xml.xmlField.saveContent = function() {
	if (com.walker.xml.xmlField.contentFormPanel.getForm().isValid()) {
		var sumBtn = function(btn) {
			if (btn != 'yes') {
				return;
			}
			com.walker.xml.xmlField.contentFormPanel.getForm().submit( {
				method : "POST",
				waitMsg : "保存中,请稍后...",
				url : "/walker/xmlField/saveXmlField",
				params : {STATUS : com.walker.xml.xmlField.contentFormPanel.find("name", "STATUS")[0].getValue()},
				success : function(form, action) {
					if (action.result.success) {
						Ext.Msg.alert("提示", "保存成功!", function() {
							Ext.WindowMgr.getActive().close();
						});
						com.walker.xml.xmlField.queryContent();
					} else {
						Ext.Msg.alert("提示", "字段" + action.result.desc);
					}
				},
				failure : function(form, action) {
					Ext.Msg.alert("提示", "字段" + action.result.desc);
				}
			});

		};
		Ext.Msg.confirm('提示', "确定保存吗？", sumBtn);
	}
};

com.walker.xml.xmlField.deleteContent = function() {
	var selRec = com.walker.common.getSelectRecord(com.walker.xml.xmlField.contentGridPnl, true);
	if (selRec) {
		var sumBtn = function(btn) {
			if (btn != 'yes') {
				return;
			}
			Ext.Ajax.request( {
				url : '/walker/xmlField/deleteXmlField',
				params : {
					ID : selRec.get('ID')
				},
				success : function(response) {
					try {
						var result = Ext.util.JSON.decode(response.responseText);
						if (result.success) {
							Ext.Msg.alert("提示", "删除成功!");
							com.walker.xml.xmlField.queryContent();
						} else {
							Ext.Msg.alert("提示", result.errorMsg);
						}
					} catch (e) {
					}
				},
				failure : function(response) {
					Ext.Msg.alert("提示", result.errorMsg);
				}
			});
		};
		Ext.Msg.confirm('提示', "删除后可能导致部分业务数据显示异常,确定继续?", sumBtn);
	}
};

Ext.onReady(function() {

	new Ext.Viewport( {
		layout : 'border',
		items : [ com.walker.xml.xmlField.getQueryForm(),
				com.walker.xml.xmlField.getGridPnl() ]
	});
});
