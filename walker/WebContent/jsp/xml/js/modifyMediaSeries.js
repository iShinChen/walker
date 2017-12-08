Ext.namespace("com.walker.xml.modifyMediaSeries");

Ext.onReady(function() {
	var rec = com.walker.common.getSelectRecord(WALKER.parentPage().com.walker.xml.mediaSeries.seriesGridPnl, true);
	com.walker.xml.modifyMediaSeries.SERIES_ID = rec.get("SERIES_ID");
	
	new Ext.Viewport( {
		layout : 'border',
		items : [ 
		    new Ext.TabPanel( {
				region : "center",
				border : false,
				enableTabScroll : false,
				deferredRender : false,
				activeTab : 0,
				minTabWidth : 159,
				resizeTabs : true,
				autoScroll : false,
				items : [ {
					title : "合集内容",
					layout: 'border',
					border : false,
 					autoScroll : false,
					items : [ com.walker.xml.modifyMediaSeries.seriesEditForm ]
				}, {
					title : "海报列表",
					layout: 'border',
					border : false,
 					autoScroll : false,
					items : [ com.walker.xml.modifyMediaSeries.createPosterPanel() ]
				}, {
					title : "分集列表",
					border : false,
					layout: 'border',
					autoScroll : false,
					items : [com.walker.xml.modifyMediaSeries.createProgramPnl()]
				} ]
			} )
		]
	});
});

/**
 * 关闭窗口
 */
com.walker.xml.modifyMediaSeries.closeEditWin = function() {
	Ext.Msg.confirm('提示',"确定放弃更改,关闭窗口?", function(btn) {
		if(btn == "yes") {
			WALKER.closePopWindow();
		}
	});
};

com.walker.xml.modifyMediaSeries.refreshSeriesGrid = function () {
	WALKER.parentPage().com.walker.xml.mediaSeries.seriesGridPnl.store.reload({
		callback : function(records){
			for ( var i = 0; i < records.length; i++) {
				if (com.walker.xml.modifyMediaSeries.SERIES_ID == records[i].get("SERIES_ID")) {
					WALKER.parentPage().com.walker.xml.mediaSeries.seriesGridPnl.getSelectionModel().selectRow(i);
				}
			}
		}
	});
};

//内容分类
com.walker.xml.modifyMediaSeries.contentType = com.walker.common.getPubCodeStore('MEDIA_TYPE', '1');

com.walker.xml.modifyMediaSeries.statusStore = com.walker.common.getPubCodeStore('LINE_STATUS', '1');

com.walker.xml.modifyMediaSeries.clarityStore = com.walker.common.getPubCodeStore('MEIDA_CLARITY', '1');

//剧集类型
com.walker.xml.modifyMediaSeries.episodeType = com.walker.common.getPubCodeStore('MEDIA_EPISODE_TYPE', '1');

/**
 * 合集编辑面板
 */
com.walker.xml.modifyMediaSeries.seriesEditForm = new Ext.FormPanel( {
	region : 'center',
	layout : 'form',
	labelWidth : 100,
	width : 1280,
	labelAlign : 'right',
	autoHeight: false,
	autoScroll: true,
	bodyStyle: 'padding-top: 10px;',
	border: false,
	tbar : [ {
		text : '保存',
		iconCls : 'btnIconSave',
		handler : function() {
			com.walker.xml.modifyMediaSeries.saveSeries();
		}
	}, '-', {
		text : '取消',
		iconCls : 'btnIconReset',
		handler : function() {
			com.walker.xml.modifyMediaSeries.closeEditWin();
		}
	} ],
	items : [ {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "名称",
				anchor: '95%',
				name : 'NAME',
				allowBlank : false
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "英文名称",
				anchor: '95%',
				name : 'EN_NAME'
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "别名",
				anchor: '95%',
				name : 'ALIAS_NAME'
			} ]
		} ]
	}, {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : 'combo',
				fieldLabel : '剧集类型',
				id : 'EPISODE_TYPE',
				name : 'EPISODE_TYPE',
				anchor: '95%',
				triggerAction : 'all',
				displayField : 'VALUE',
				valueField : 'CODE',
				store : com.walker.xml.modifyMediaSeries.episodeType,
				mode : 'local',
				emptyText : '请选择...',
				allowBlank : false,
				editable : false
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "简拼索引",
				anchor: '95%',
				name : 'SIMPLESPELL',
				allowBlank : false
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "全拼索引",
				anchor: '95%',
				name : 'FULLSPELL'
			} ]
		} ]
	}, {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : 'combo',
				fieldLabel : '内容分类',
				id : 'TYPE',
				name : 'TYPE',
				anchor: '95%',
				triggerAction : 'all',
				displayField : 'NAME',
				valueField : 'CODE',
				store : com.walker.xml.modifyMediaSeries.contentType,
				mode : 'local',
				emptyText : '请选择...',
				allowBlank : false,
				editable : false
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "上映地区",
				anchor: '95%',
				name : 'AREA'
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "datefield",
				fieldLabel : "上映日期",
				anchor: '95%',
				name : 'RELEASE_TIME',
				format : 'Y-m-d'
			} ]
		} ]
	}, {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "影片语种",
				anchor: '95%',
				name : 'LANGUAGE'
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "字幕",
				anchor: '95%',
				name : 'CAPTION'
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "出品人",
				anchor: '95%',
				name : 'PRODUCER'
			} ]
		} ]
	}, {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "演员",
				anchor: '95%',
				name : 'ACTOR'
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "导演",
				anchor: '95%',
				name : 'DIRECTOR'
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "编剧",
				anchor: '95%',
				name : 'ADAPTOR'
			} ]
		} ]
	}, {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "numberfield",
				fieldLabel : "总集数",
				anchor: '95%',
				name : 'PROGRAM_COUNT',
				allowBlank : false,
			    minValue : 1,
			    maxLength : 3,
			    allowNegative: false,
			    allowDecimals: false,
				blankText : '总集数',
				value : 0
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "最新分集",
				anchor: '95%',
				name : 'NEW_PROGRAM'
			} ]
		} ]
	}, {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "textfield",
				fieldLabel : "版权商",
				anchor: '95%',
				name : 'COPYRIGHT'
			} ]
		}, {
			layout : "form",
			border : false,
			width : 320,
			items : [ {
				xtype : "datefield",
				fieldLabel : "版权过期时间",
				anchor: '95%',
				name : 'COPYRIGHT_DATE',
				format : 'Y-m-d'
			} ]
		} ]
	}, {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			columnWidth : 1,
			items : [ {
				xtype : "textfield",
				fieldLabel : "关键字",
				width : 839,
				name : 'KEYWORDS'
			} ]
		} ]
	}, {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			columnWidth : 1,
			items : [ {
				xtype : "textfield",
				fieldLabel : "看点",
				width : 839,
				name : 'AWARDS'
			} ]
		} ]
	}, {
		layout : "column",
		border : false,
		items : [ {
			layout : "form",
			border : false,
			columnWidth : 1,
			items : [ {
				xtype : "textarea",
				fieldLabel : "描述",
				width : 839,
				height : 70,
				name : 'DESCRIPTION'
			} ]
		} ]
	} ],
	listeners : {
		afterrender: function() {
			com.walker.xml.modifyMediaSeries.initSeriesEditForm();
		}
	}
});

/**
 * 初始化合集FORM数据
 */
com.walker.xml.modifyMediaSeries.initSeriesEditForm = function() {
	if (com.walker.xml.modifyMediaSeries.SERIES_ID) {
		com.walker.xml.modifyMediaSeries.seriesEditForm.getEl().mask("数据加载中...","x-mask-loading");
		com.walker.xml.modifyMediaSeries.seriesEditForm.getForm().load({
			url : "/walker/series/getSeriesById",
			method : "post",
			params : {
				seriesId : com.walker.xml.modifyMediaSeries.SERIES_ID
			},
			success : function(form, action) {
				com.walker.xml.modifyMediaSeries.seriesEditForm.getEl().unmask();
				com.walker.xml.modifyMediaSeries.seriesEditForm.findById("TYPE").setValue(action.result.data.TYPE);
				com.walker.xml.modifyMediaSeries.seriesEditForm.findById("TYPE").setRawValue(action.result.data.TYPE_NAME);
				com.walker.xml.modifyMediaSeries.status = action.result.data.STATUS;
			},
			failure : function(response) {
				com.walker.xml.modifyMediaSeries.seriesEditForm.getEl().unmask();
			}
		});
	}
};

/**
 * 更新合集数据
 */
com.walker.xml.modifyMediaSeries.saveSeries = function() {
	if (com.walker.xml.modifyMediaSeries.seriesEditForm.getForm().isValid()) {
		var params = {};
		params.TYPE = com.walker.xml.modifyMediaSeries.seriesEditForm.findById("TYPE").getValue();
		params.EPISODE_TYPE = com.walker.xml.modifyMediaSeries.seriesEditForm.findById("EPISODE_TYPE").getValue();
		params.TYPE_NAME = com.walker.xml.modifyMediaSeries.seriesEditForm.findById("TYPE").getRawValue();
		params.SERIES_ID = com.walker.xml.modifyMediaSeries.SERIES_ID;
		
		var submitForm = function () {
			com.walker.xml.modifyMediaSeries.seriesEditForm.getEl().mask("数据提交中...","x-mask-loading");
			com.walker.xml.modifyMediaSeries.seriesEditForm.getForm().submit({
				method : "POST",
				url : "/walker/series/updateSeries",
				params : params,
				success : function(response, action) {
					com.walker.xml.modifyMediaSeries.seriesEditForm.getEl().unmask();
					if (action.result.success == 'true') {
						Ext.Msg.alert("提示", "保存成功!");
						com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
					}
				},
				failure : function(response) {
					com.walker.xml.modifyMediaSeries.seriesEditForm.getEl().unmask();
					Ext.Msg.alert("提示", "保存失败!");
				}
			});
		};

		var confirmTitle = '提示';
		var confirmContent = '确定保存修改?';
		var confirmIcon = Ext.MessageBox.QUESTION;
		if (com.walker.xml.modifyMediaSeries.status == '01') {
			confirmTitle = '警告';
			confirmContent = '该合集已上线,确定保存修改?';
			confirmIcon = Ext.MessageBox.WARNING;
		}
		
		Ext.Msg.show({
			title: confirmTitle,
			msg: confirmContent,
			buttons: Ext.Msg.YESNO,
			fn: function(btn) {
				if (btn != 'yes') {
					return;
				}
				submitForm();
			},
			icon: confirmIcon
		});
	}
};

/**
 * 海报管理
 */
com.walker.xml.modifyMediaSeries.createPosterPanel = function() {
	var reader = new Ext.data.JsonReader( {
		fields : [ "TYPE_CODE", "TYPE_NAME", "RESLOUTION_W", "RESLOUTION_H", "FILE_MAX_SIZE", "FILE_SUFFIX", "IS_REQUIRED"
		           , "PICTURE_ID", "TYPE", "FILE_URL", "FILE_VIEW_URL", "MD5", "FILE_SIZE", "RESLOUTION", "STATUS", "IS_LACKED"],
		root : 'rows',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store( {
		url : '/walker/series/getPicturesBySeriesId',
		reader : reader,
		baseParams : {
			seriesId : com.walker.xml.modifyMediaSeries.SERIES_ID
		}
	});
	if(com.walker.xml.modifyMediaSeries.SERIES_ID) {
		store.load();
	}

	var sm = new Ext.grid.CheckboxSelectionModel({singleSelect: true});
	var colModel = new Ext.grid.ColumnModel( {
		columns : [
		sm,
		new Ext.grid.RowNumberer( {
			header : "序号",
			width : 50
		}), {
			header : "类型编码",
			align : 'center',
			sortable : false,
			dataIndex : 'TYPE_CODE',
			width : 80
		}, {
			header : "类型名称",
			align : 'left',
			sortable : false,
			dataIndex : 'TYPE_NAME',
			width : 160,
			renderer : function(v, metadata, record, rowIndex, columnIndex, store){
				if(v) {
					var rw = record.get("RESLOUTION_W");
					var rh = record.get("RESLOUTION_H");
					if(rw && rh) {
						return v + "(" + rw+ "*" + rh +")";
					}
					return v;
				}
				return "";
			}
		}, {
			header : "是否必需",
			align : 'center',
			sortable : false,
			dataIndex : 'IS_REQUIRED',
			width : 80,
			renderer : function(v, metadata, record, rowIndex, columnIndex, store){
				if(v === '1') {
					return "<font color='blue'>是</font>";
				}
				return "<font color='black'>否</font>";
			}
		}, {
			header : "海报ID",
			align : 'center',
			hidden : true,
			sortable : false,
			dataIndex : 'PICTURE_ID',
			width : 250
		}, {
			header : "实际大小",
			align : 'center',
			sortable : false,
			dataIndex : 'FILE_SIZE',
			align : 'right',
			width : 100,
			renderer : function(v, metadata, record, rowIndex, columnIndex, store){
				if(v && !isNaN(v)) {
					return (v / 1024).toFixed(2) + "(KB)";
				}
				return "";
			}
		}, {
			header : "实际分辨率",
			align : 'center',
			sortable : false,
			dataIndex : 'RESLOUTION',
			width : 100
		}, {
			header : "海报地址",
			align : 'center',
			sortable : false,
			dataIndex : 'FILE_VIEW_URL',
			align : 'left',
			width : 500,
			renderer : function(value, metadata, record, rowIndex, columnIndex, store){
				if(value) {
					var resloution = record.get("RESLOUTION");
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
		}, {
			header : "海报状态",
			dataIndex : "IS_LACKED",
			sortable : true,
			width : 80,
			align : 'center',
			renderer : function(value, metadata, record, rowindex){
				metadata.css += " x-grid3-cell-icon";
				var playUrl = record.get("FILE_VIEW_URL");
				if(playUrl) {
					if (value === true) {
						return '<div title="海报丢失" class="lackpicture"></div>';
					}
					else {
						return '<div title="海报正常" class="okpicture"></div>';
					}
				}
			}
		}, {
			header : "上线状态",
			dataIndex : "STATUS",
			sortable : true,
			width : 100,
			align : 'center',
			renderer : function(value, metadata, record){
				return com.walker.common.renderer.getComboValue(com.walker.xml.modifyMediaSeries.statusStore, value, {
					"00" : "black",
					"01" : "green",
					"02" : "red"
				});
			}
		} ]
	});
	com.walker.xml.modifyMediaSeries.posterGrid = new Ext.grid.GridPanel( {
		region : 'center',
		store : store,
		autoHeight: false,
		loadMask : true,
		sm: sm,
		colModel : colModel,
		border: false,
		tbar : [ {
			text : '编辑',
			iconCls : 'btnIconUpdate',
			id : 'btnEditPicture',
			disabled : true,
			handler : function() {
				com.walker.xml.modifyMediaSeries.editPicture();
			}
		}, {
			text : '删除',
			iconCls : 'btnIconDel',
			id : 'btnDeletePicture',
			disabled : true,
			handler : function() {
				com.walker.xml.modifyMediaSeries.deletePicture();
			}
		}, '-', {
			text : '上线',
			iconCls : 'btnIconOnLine',
			id : 'btnOnLinePicture',
			disabled : true,
			handler : function() {
				com.walker.xml.modifyMediaSeries.onlinePicture();
			}
		}, {
			text : '下线',
			iconCls : 'btnIconOffLine',
			id : 'btnOffLinePicture',
			disabled : true,
			handler : function() {
				com.walker.xml.modifyMediaSeries.offlinePicture();
			}
		}, '-', {
			text : '刷新',
			iconCls : 'btnIconRefresh',
			handler : function() {
				com.walker.xml.modifyMediaSeries.posterGrid.store.reload();
			}
		}, '-', {
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.xml.modifyMediaSeries.closeEditWin();
			}
		} ]
	});
	
	com.walker.xml.modifyMediaSeries.posterGrid.getSelectionModel().on("selectionchange", function(sm) {
		if(sm.selections.length == 0) {
			Ext.getCmp('btnEditPicture').setDisabled(true);
			Ext.getCmp('btnDeletePicture').setDisabled(true);
			Ext.getCmp('btnOnLinePicture').setDisabled(true);
			Ext.getCmp('btnOffLinePicture').setDisabled(true);
		}
	});
	
	com.walker.xml.modifyMediaSeries.posterGrid.getSelectionModel().on("rowselect", function (sm, rowindex, record) {
		var pictureId = record.get("PICTURE_ID");
		Ext.getCmp('btnEditPicture').setDisabled(false);
		if(pictureId) {
			Ext.getCmp('btnDeletePicture').setDisabled(false);
			Ext.getCmp('btnOnLinePicture').setDisabled(false);
			Ext.getCmp('btnOffLinePicture').setDisabled(false);
		}
		else {
			Ext.getCmp('btnDeletePicture').setDisabled(true);
			Ext.getCmp('btnOnLinePicture').setDisabled(true);
			Ext.getCmp('btnOffLinePicture').setDisabled(true);
		}
	});
	
	return com.walker.xml.modifyMediaSeries.posterGrid;
};

com.walker.xml.modifyMediaSeries.createPosterEditForm = function() {
	com.walker.xml.modifyMediaSeries.editPosterForm = new Ext.FormPanel( {
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
			uploadAction : 'pictureUpload.do',
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
				beforeupload : function(o) {
					if(!com.walker.xml.modifyMediaSeries.SERIES_ID) {
	    				Ext.Msg.alert("提示", "请先保存合集内容!");
	    				return false;
	    			}
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
				com.walker.xml.modifyMediaSeries.savePicture();
			}
		}, '-', {
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				Ext.WindowMgr.getActive().close();
			}
		} ]
	});
	return com.walker.xml.modifyMediaSeries.editPosterForm;
};

com.walker.xml.modifyMediaSeries.editPicture = function() {
	var record = com.walker.xml.modifyMediaSeries.posterGrid.getSelectionModel().getSelected();
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
			items : [ com.walker.xml.modifyMediaSeries.createPosterEditForm() ],
			listeners : {
				show : function() {
					com.walker.xml.modifyMediaSeries.editPosterForm.find("name", "TYPE_NAME")[0].setValue(record.get("TYPE_NAME"));

					var desc = "";
					if(record.get("RESLOUTION_W") && record.get("RESLOUTION_H")) {
						desc += "分辨率要求：" + record.get("RESLOUTION_W") + "*" + record.get("RESLOUTION_H") + "\n";
					}
					if(record.get("FILE_MAX_SIZE")) {
						desc += "大小限制：" + record.get("FILE_MAX_SIZE") + "KB" + "\n";
					}
					if(record.get("FILE_SUFFIX")) {
						desc += "文件类型：." + record.get("FILE_SUFFIX");
					}
					com.walker.xml.modifyMediaSeries.editPosterForm.find("name", "DESCRIPTION")[0].setValue(desc);

					var pictureId = record.get("PICTURE_ID");
					if(pictureId) {
						Ext.getCmp("imgUrl").setValue({
							viewUrl : record.get("FILE_VIEW_URL"), 
							resloution : record.get("RESLOUTION"), 
							fileUrl : record.get("FILE_URL"), 
							fileSize : record.get("FILE_SIZE")
						});
					}
				}
			}
		}).show();
	}
};

com.walker.xml.modifyMediaSeries.deletePicture = function() {
	var record = com.walker.xml.modifyMediaSeries.posterGrid.getSelectionModel().getSelected();
	if(record) {
		var pictureId = record.get("PICTURE_ID");
		Ext.Msg.confirm('提示',"确定删除所选海报吗?", function(btn) {
			if(btn!='yes') {return;}
			com.walker.xml.modifyMediaSeries.posterGrid.getEl().mask("数据提交中...", "x-mask-loading");
			Ext.Ajax.request( {
				url : '/walker/picture/deletePicture',
				method : 'post',
				success : function(response) {
					com.walker.xml.modifyMediaSeries.posterGrid.getEl().unmask();
					var result = Ext.decode(response.responseText);
					if (result.success) {
						Ext.Msg.alert("提示", "删除海报成功!");
						com.walker.xml.modifyMediaSeries.posterGrid.store.reload();
						com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
					}
				},
				failure : function(response) {
					com.walker.xml.modifyMediaSeries.posterGrid.getEl().unmask();
					Ext.Msg.alert("提示", "删除海报失败!");
				},
				params : {
					pictureId : pictureId
				}
			});
		});
	}
	else {
		Ext.Msg.alert("提示", "未选择任何记录!");
	}
};

com.walker.xml.modifyMediaSeries.onlinePicture = function() {
	var record = com.walker.xml.modifyMediaSeries.posterGrid.getSelectionModel().getSelected();
	if(record) {
		var pictureId = record.get("PICTURE_ID");
		Ext.Msg.confirm('提示',"确定上线所选海报吗?", function(btn) {
			if(btn!='yes') {return;}
			com.walker.xml.modifyMediaSeries.posterGrid.getEl().mask("数据提交中...", "x-mask-loading");
			Ext.Ajax.request( {
				url : '/walker/picture/onlinePicture',
				method : 'post',
				success : function(response) {
					com.walker.xml.modifyMediaSeries.posterGrid.getEl().unmask();
					var result = Ext.decode(response.responseText);
					if (result.success) {
						Ext.Msg.alert("提示", "上线海报成功!");
						com.walker.xml.modifyMediaSeries.posterGrid.store.reload();
						com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
					}
				},
				failure : function(response) {
					com.walker.xml.modifyMediaSeries.posterGrid.getEl().unmask();
					Ext.Msg.alert("提示", "上线海报失败!");
				},
				params : {
					pictureId : pictureId
				}
			});
		});
	}
	else {
		Ext.Msg.alert("提示", "未选择任何记录!");
	}
};

com.walker.xml.modifyMediaSeries.offlinePicture = function() {
	var record = com.walker.xml.modifyMediaSeries.posterGrid.getSelectionModel().getSelected();
	if(record) {
		var pictureId = record.get("PICTURE_ID");
		Ext.Msg.confirm('提示',"确定下线所选海报吗?", function(btn) {
			if(btn!='yes') {return;}
			com.walker.xml.modifyMediaSeries.posterGrid.getEl().mask("数据提交中...", "x-mask-loading");
			Ext.Ajax.request( {
				url : '/walker/picture/offlinePicture',
				method : 'post',
				success : function(response) {
					com.walker.xml.modifyMediaSeries.posterGrid.getEl().unmask();
					var result = Ext.decode(response.responseText);
					if (result.success) {
						Ext.Msg.alert("提示", "下线海报成功!");
						com.walker.xml.modifyMediaSeries.posterGrid.store.reload();
						com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
					}
				},
				failure : function(response) {
					com.walker.xml.modifyMediaSeries.posterGrid.getEl().unmask();
					Ext.Msg.alert("提示", "下线海报失败!");
				},
				params : {
					pictureId : pictureId
				}
			});
		});
	}
	else {
		Ext.Msg.alert("提示", "未选择任何记录!");
	}
};

com.walker.xml.modifyMediaSeries.savePicture = function() {
	var record = com.walker.xml.modifyMediaSeries.posterGrid.getSelectionModel().getSelected();
	if(com.walker.xml.modifyMediaSeries.editPosterForm.getForm().isValid()) {
		Ext.Msg.confirm('提示',"确定保存吗?", function(btn) {
			if(btn!='yes') {return;}
			
			var imageValue = Ext.getCmp("imgUrl").getValue();
			
			com.walker.xml.modifyMediaSeries.editPosterForm.getEl().mask("数据提交中...", "x-mask-loading");
			Ext.Ajax.request( {
				url : '/walker/picture/savePicture',
				method : 'post',
				success : function(response) {
					com.walker.xml.modifyMediaSeries.editPosterForm.getEl().unmask();
					var result = Ext.decode(response.responseText);
					if (result.success) {
						Ext.Msg.alert("提示", "更新海报成功!");
						Ext.WindowMgr.getActive().close();
						com.walker.xml.modifyMediaSeries.posterGrid.store.reload();
						com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
					}
				},
				failure : function(response) {
					com.walker.xml.modifyMediaSeries.editPosterForm.getEl().unmask();
					Ext.Msg.alert("提示", "更新海报失败!");
				},
				params : {
					FILE_URL : imageValue.fileUrl,
					RESLOUTION : imageValue.resloution,
					FILE_SIZE : imageValue.fileSize,
					TYPE : record.get("TYPE_CODE"),
					PICTURE_ID : record.get("PICTURE_ID"),
					SERIES_ID : com.walker.xml.modifyMediaSeries.SERIES_ID
				}
			});
		});
	}
};

//分集管理列表
/**
 * 分集内容
 */
com.walker.xml.modifyMediaSeries.createProgramPnl = function() {
	var reader = new Ext.data.JsonReader( {
		fields : [ "PROGRAM_ID", "NAME", "SERIALNO", "DURATION", "KEYWORDS", "DIRECTOR", 
		           "ADAPTOR", "ACTOR", "AWARDS", "DESCRIPTION", "STATUS", "MOVIE_COUNT", "ONLINE_MOVIE_COUNT", "ONLINE_TIME", "UNLINE_TIME"],
		root : 'rows',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store( {
		url : '/walker/series/getProgramPageBySeriesId',
		reader : reader,
		baseParams : {
			limit : 20,
			start : 0,
			SERIES_ID : com.walker.xml.modifyMediaSeries.SERIES_ID
		}
	});
	store.on("load", function(s, records) {
		if (com.walker.xml.modifyMediaSeries.programId) {
			for ( var i = 0; i < records.length; i++) {
				if (records[i].get("PROGRAM_ID") == com.walker.xml.modifyMediaSeries.programId) {
					com.walker.xml.modifyMediaSeries.programGrid.getSelectionModel().selectRow(i);
				}
			}
		}
	});
	
	store.load();

	var colModel = new Ext.grid.ColumnModel( {
		columns : [ new Ext.grid.RowNumberer( {
			header : "序号",
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
			header : "分集ID",
			align : 'center',
			hidden : true,
			sortable : false,
			dataIndex : 'PROGRAM_ID'
		}, {
			header : "分集名称",
			align : 'center',
			hidden : false,
			sortable : false,
			dataIndex : 'NAME',
			align : 'left',
			width : 200
		}, {
			header : "集号",
			align : 'center',
			hidden : false,
			sortable : false,
			dataIndex : 'SERIALNO',
			align : 'right',
			width : 60
		}, {
			header : "时长(秒)",
			align : 'center',
			hidden : false,
			sortable : false,
			dataIndex : 'DURATION',
			align : 'right',
			width : 80
		}, {
			header : "导演",
			align : 'center',
			hidden : true,
			sortable : false,
			dataIndex : 'DIRECTOR',
			align : 'left',
			width : 120
		}, {
			header : "编剧",
			dataIndex : "ADAPTOR",
			hidden : true,
			sortable : true,
			width : 100,
			align : 'left'
		}, {
			header : "演员",
			align : 'center',
			hidden : true,
			sortable : false,
			dataIndex : 'ACTOR'
		}, {
			header : "上线状态",
			dataIndex : "STATUS",
			sortable : true,
			width : 100,
			align : 'center',
			renderer : function(value, metadata, record){
				if (value == '00') {
					return '<font color="black">未上线</font>';
				}else if (value == '01') {
					var onlineTime = "";
					if(record.data.ONLINE_TIME) {
						onlineTime = record.data.ONLINE_TIME;
					}
					return '<font color="green" title="上线时间:' + onlineTime + '">已上线</font>';
				}else if (value == '02') {
					var unlineTime = "";
					if(record.data.UNLINE_TIME) {
						unlineTime = record.data.UNLINE_TIME;
					}
					return '<font color="red" title="下线时间:' + unlineTime + '">已下线</font>';
				}
			}
		}, {
			header : "片源数",
			dataIndex : "ONLINE_MOVIE_COUNT",
			sortable : true,
			width : 70,
			align : 'center',
			renderer : function(value, metadata, record, rowindex){
				var programId = record.data.PROGRAM_ID;
				var movieCount = record.data.MOVIE_COUNT;
				if(value == 0) {
					return "<a href='javascript:void();' class='movielack' onclick='com.walker.xml.modifyMediaSeries.showMovieList(\"" + programId + "\");return false;'>0/" + movieCount + "</a>";
				}
				else {
					return "<a href='javascript:void();' class='movienormal' onclick='com.walker.xml.modifyMediaSeries.showMovieList(\"" + programId + "\");return false;'>" + value + "/" + movieCount + "</a>";
				}
			}
		}, {
			header : "片源状态",
			dataIndex : "ONLINE_MOVIE_COUNT",
			sortable : true,
			width : 80,
			align : 'center',
			renderer : function(value, metadata, record, rowindex){
				metadata.css += " x-grid3-cell-icon";
				var movieCount = record.data.MOVIE_COUNT;
				if (movieCount == 0 || value == 0) {
					return '<div title="缺少片源" class="moviestatuserror"></div>';
				}
				return '<div title="片源正常" class="moviestatusok"></div>';
			}
		} ]
	});
	var grid = new Ext.grid.GridPanel( {
		region : 'center',
		store : store,
		autoHeight: false,
		loadMask : true, // 加载数据时为元素做出类似于遮罩的效果
		sm: new Ext.grid.RowSelectionModel(),
		colModel : colModel,
		border: false,
		style: 'border-right-width: 1px;',
		bbar : new Ext.PagingToolbar( {
			store : store,
			displayInfo : true,
			pageSize : 20
		})
	});
	
	grid.getSelectionModel().on("rowselect", function (sm, rowindex, record) {
		com.walker.xml.modifyMediaSeries.editProgramForm.getEl().mask("数据加载中...","x-mask-loading");
		Ext.getCmp("programSave").setDisabled(true);
		Ext.getCmp("programDel").setDisabled(true);
		Ext.getCmp("programOnLine").setDisabled(true);
		Ext.getCmp("programOffLine").setDisabled(true);
		
		Ext.Ajax.request({
			url : '/walker/program/getProgramById',
			success : function(response) {
				com.walker.xml.modifyMediaSeries.editProgramForm.getEl().unmask();
				Ext.getCmp("programSave").setDisabled(false);
				Ext.getCmp("programDel").setDisabled(false);
				
				var result = Ext.util.JSON.decode(response.responseText);
				result = result.data;
				com.walker.xml.modifyMediaSeries.programId = record.get('PROGRAM_ID');
				com.walker.xml.modifyMediaSeries.editProgramForm.status = result.STATUS;
				
				if (result.STATUS == '01') {
					Ext.getCmp("programOffLine").setDisabled(false);
				}
				else {
					Ext.getCmp("programOnLine").setDisabled(false);
				}
				
				var form = com.walker.xml.modifyMediaSeries.editProgramForm.getForm();
				
				for ( var i = 0; i < form.items.length; i++) {
					var field = form.items.get(i);
					var val = result[field.name];
					if (val && val != 'null') {
						field.setValue(val);
					}
					else {
						field.setValue('');
					}
				}
			},
			failure : function(response) {
				com.walker.xml.modifyMediaSeries.editProgramForm.getEl().unmask();
				com.walker.xml.modifyMediaSeries.programId = '';
				com.walker.xml.modifyMediaSeries.editProgramForm.status = '';

				var form = com.walker.xml.modifyMediaSeries.editProgramForm.getForm();
				form.reset();
				Ext.Msg.alert("提示","查询发生异常!");
			},
			params : {
				programId : record.get("PROGRAM_ID")
			}
		});
	});

	com.walker.xml.modifyMediaSeries.programGrid = grid;

	com.walker.xml.modifyMediaSeries.editProgramForm = new Ext.FormPanel( {
		region : 'east',
		width : 650,
		maxSize : 650,
		minSize : 650,
		labelWidth : 100,
		labelAlign : 'right',
		bodyStyle : 'padding:10px 0px 0px 0px',
		layout : 'form',
		autoHeight: false,
		autoScroll: true,
		border: false,
		split: true,
		style: 'border-left-width: 1px;',
		items : [ {
			layout : 'column',
			border : false,
			items : [ {
				width: 310,
				layout : 'form',
				border : false,
				items : [ {
					xtype : 'textfield',
					fieldLabel : '分集名称',
					id : 'NAME',
					name : 'NAME',
					allowBlank : false,
				    maxLength : 100,
					width : 190
				} ]
			}, {
				width: 310,
				layout : 'form',
				border : false,
				items : [ {
					xtype : 'numberfield',
		            fieldLabel: '集号',
		            id: 'SERIALNO',
		            name: 'SERIALNO',
		            allowBlank: false,
				    minValue : 1,
				    maxLength : 3,
				    allowNegative: false,
				    allowDecimals: false,
					width : 190
				} ]
			} ]
		}, {
			layout : 'column',
			border : false,
			items : [ {
				width: 310,
				layout : 'form',
				border : false,
				items : [ {
					fieldLabel : '时长(秒)',
					xtype : 'numberfield',
		            fieldLabel: '时长(秒)',
		            id: 'DURATION',
		            name: 'DURATION',
				    minValue : 1,
				    maxLength : 5,
		            allowBlank: false,
				    allowNegative: false,
				    allowDecimals: false,
					width : 190
				} ]
			}, {
				width: 310,
				layout : 'form',
				border : false,
				items : [ {
					fieldLabel : '更新时间',
					xtype : "datefield",
					name : 'UPDATE_TIME',
					format : 'Y-m-d',
					editable : true,
					width : 190
				} ]
			} ]
		}, {
			layout : 'column',
			border : false,
			items : [ {
				width: 310,
				layout : 'form',
				border : false,
				items : [ {
					xtype : 'textfield',
		            fieldLabel: '导演',
		            id: 'DIRECTOR',
		            name: 'DIRECTOR',
				    maxLength : 100,
					width : 190
				} ]
			}, {
				width: 310,
				layout : 'form',
				border : false,
				items : [ {
					xtype : 'textfield',
		            fieldLabel: '编剧',
		            id: 'ADAPTOR',
		            name: 'ADAPTOR',
				    maxLength : 100,
					width : 190
				} ]
			} ]
		}, {
			xtype : 'textfield',
            fieldLabel: '演员',
            id: 'ACTOR',
            name: 'ACTOR',
		    maxLength : 100,
			width : 500
		}, {
            xtype : "textfield",
            fieldLabel: '关键字',
	        id : 'KEYWORDS',
	        name : 'KEYWORDS',
		    maxLength : 200,
			width : 500
		}, {
            xtype : "textfield",
            fieldLabel: '看点',
            id: 'AWARDS',
            name: 'AWARDS',
		    maxLength : 200,
			width : 500
		}, {
            xtype : "textarea",
            fieldLabel: '描述',
            id: 'DESCRIPTION',
            name: 'DESCRIPTION',
		    maxLength : 500,
			height : 150,
			width : 500
		} ],
		tbar : [ {
			text : '保存',
			id : 'programSave',
			disabled : true,
			iconCls : 'btnIconSave',
			handler : function() {
				com.walker.xml.modifyMediaSeries.saveProgram();
			}
		}, '-', {
			text : '删除',
			iconCls : 'btnIconDel',
			id : 'programDel',
			disabled : true,
			handler : function() {
				com.walker.xml.modifyMediaSeries.deleteProgram();
			}
		}, '-', {
			text : '上线',
			id : 'programOnLine',
			disabled : true,
			iconCls : 'btnIconOnLine',
			handler : function() {
				com.walker.xml.modifyMediaSeries.onlineProgram();
			}
		}, {
			text : '下线',
			id : 'programOffLine',
			disabled : true,
			iconCls : 'btnIconOffLine',
			handler : function() {
				com.walker.xml.modifyMediaSeries.offlineProgram();
			}
		}, '-', {
			text : '取消',
			iconCls : 'btnIconReset',
			handler : function() {
				com.walker.xml.modifyMediaSeries.closeEditWin();
			}
		} ]
	});

	return new Ext.Panel( {
		region : 'center',
		layout : 'border',
		border : false,
		autoScroll : true,
		items : [ com.walker.xml.modifyMediaSeries.programGrid, com.walker.xml.modifyMediaSeries.editProgramForm ]
	});
};

//删除分集
com.walker.xml.modifyMediaSeries.deleteProgram = function(){
	if(com.walker.xml.modifyMediaSeries.programId){
		if(com.walker.xml.modifyMediaSeries.editProgramForm.status == '01') {
			Ext.Msg.alert("提示","分集已上线，无法删除!");
			return;
		}
		
		var sumBtn = function(btn){
			if(btn!='yes') {return;}
			var myLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:"数据处理中..."});
			myLoadMask.show();
			Ext.Ajax.request({
				url : '/walker/program/deleteProgram',
				success : function(response) {
					myLoadMask.hide();
					Ext.Msg.alert("提示","删除成功!", function() {
						Ext.getCmp("programSave").setDisabled(true);
						Ext.getCmp("programDel").setDisabled(true);
						Ext.getCmp("programOnLine").setDisabled(true);
						Ext.getCmp("programOffLine").setDisabled(true);
						
						com.walker.xml.modifyMediaSeries.programId = '';
						com.walker.xml.modifyMediaSeries.editProgramForm.status = '';

						com.walker.xml.modifyMediaSeries.editProgramForm.getForm().reset();
						
						com.walker.xml.modifyMediaSeries.programGrid.store.reload();
						com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
					});
				},
				failure : function(response) {
					myLoadMask.hide();
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.err_msg){
						Ext.Msg.alert("提示", result.err_msg);
					}else{
						Ext.Msg.alert("提示","删除失败!");
					}
				},
				params : {
					programId : com.walker.xml.modifyMediaSeries.programId,
					seriesId : com.walker.xml.modifyMediaSeries.SERIES_ID
				}
			});
		};
		Ext.Msg.confirm('提示',"确定<font color='red'>删除</font>所选分集吗?", sumBtn);
	}
};

com.walker.xml.modifyMediaSeries.showMovieList = function(programId) {
	if(programId) {
		var store = new Ext.data.Store({
			url : '/walker/movie/getMovieByProgramId',
			reader : new Ext.data.JsonReader({
				fields : ["MOVIE_ID", "ORIGINAL_ID", "NAME", "TYPE", "TAG", "PLAY_URL"
				          , "CLARITY", "RESOLUTION", "OVERALLBITRATEMODE", "VIDEOTYPE"
				          , "KBPS", "AUDIOTYPE", "AUDIOFORMAT", "CAPTIONED", "DIMENSIONS"
				          , "FILE_TYPE", "DURATION", "FILE_SIZE", "STATUS", "MD5", "STATUS", "CDN_ID"
				          , "CREATE_TIME", "CDN_TIME", "ONLINE_TIME", "UNLINE_TIME"],
				root : 'rows',
				totalProperty : 'total'
			})
		});
		
		// 创建列模型的多选框模型.
	    var sm = new Ext.grid.CheckboxSelectionModel();	
		var model = new Ext.grid.ColumnModel([
		    sm, {
				header : "片源ID",
				dataIndex : "MOVIE_ID",
				hidden : true,
				sortable : true,
				width : 250,
				align : 'center'
			}, {
				header : "原始片源ID",
				dataIndex : "ORIGINAL_ID",
				sortable : true,
				hidden : true,
				width : 150,
				align : 'left'
			}, {
				header : "清晰度",
				dataIndex : "CLARITY",
				sortable : true,
				width : 75,
				align : 'center',
				renderer : function(value,metadata){
					return com.walker.common.renderer.getComboValue(com.walker.xml.modifyMediaSeries.clarityStore, value, {
						"00" : "black",
						"01" : "green",
						"02" : "blue",
						"03" : "#7A378B"
					});
				}
			}, {
				header : "分辨率",
				dataIndex : "RESOLUTION",
				sortable : true,
				width : 80,
				align : 'center'
			}, {
				header : "KBPS",
				dataIndex : "KBPS",
				sortable : true,
				width : 80,
				align : 'right'
			}, {
				header : "文件大小(MB)",
				dataIndex : "FILE_SIZE",
				sortable : true,
				width : 100,
				align : 'right'
			}, {
				header : "注入时间",
				dataIndex : "CDN_TIME",
				sortable : true,
				width : 150,
				align : 'center'
			}, {
				header : "创建时间",
				dataIndex : "CREATE_TIME",
				sortable : true,
				width : 150,
				hidden : true,
				align : 'center'
			}, {
				header : "上线状态",
				dataIndex : "STATUS",
				sortable : true,
				width : 80,
				align : 'center',
				renderer : function(value, metadata, record){
					if (value == '00') {
						return '<font color="black">未上线</font>';
					}else if (value == '01') {
						var onlineTime = "";
						if(record.data.ONLINE_TIME) {
							onlineTime = record.data.ONLINE_TIME;
						}
						return '<font color="green" title="' + onlineTime + '">已上线</font>';
					}else if (value == '02') {
						var unlineTime = "";
						if(record.data.UNLINE_TIME) {
							unlineTime = record.data.UNLINE_TIME;
						}
						return '<font color="red" title="' + unlineTime + '">已下线</font>';
					}
				}
			}
		]);
		
		var grid = new Ext.grid.GridPanel({
			autoWidth : true,
			autoScroll : true,
			cm : model,
			sm : sm,
			store : store,
			border: false,
			stripeRows : true,
			region : 'center'
		});
		
		new Ext.Window( {
			title : "片源列表",
			layout : 'border',
			width : 700,
			height : 350,
			modal : true,
			constrain : true,
			autoScroll : true,
			maximizable : false,
			minimizable : false,
			plain: true,
			resizable : false,
			closeAction : 'close',
			items : [ grid ],
			tbar : [ {
				// 上线
				text : '上线',
				iconCls : 'btnIconOnLine',
				handler : function() {
					var records = grid.getSelectionModel().getSelections();
					if(records.length == 1) {
						var record = records[0];
						var status = record.data.STATUS;
						var movieId = record.data.MOVIE_ID;
						if (status != '01') {
							Ext.Msg.confirm('提示',"确定<font color='blue'>上线</font>所选片源吗?", function(btn) {
								if (btn != 'yes') {
									return;
								}
								Ext.getBody().mask("数据处理中...", "x-mask-loading");
								Ext.Ajax.request({
									url : '/walker/movie/onlineMovie',
									success : function(response) {
										Ext.getBody().unmask();
										Ext.Msg.alert("提示", "上线成功!", function() {
											store.reload();
											com.walker.xml.modifyMediaSeries.programGrid.store.reload();
											com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
										});
									},
									failure : function(response) {
										Ext.getBody().unmask();
										Ext.Msg.alert("提示", "上线失败!");
									},
									params : {
										movieId : movieId
									}
								});
							});
						}
						else {
							Ext.Msg.alert("提示", "该片源已上线!");
						}
					}
					else {
						Ext.Msg.alert("提示", "请选择一条记录!");
					}
				}
			},{
				// 下线
				text : '下线',
				iconCls : 'btnIconOffLine',
				handler : function() {
					var records = grid.getSelectionModel().getSelections();
					if(records.length == 1) {
						var record = records[0];
						var status = record.data.STATUS;
						var movieId = record.data.MOVIE_ID;
						if (status == '01') {
							Ext.Msg.confirm('提示',"确定<font color='red'>下线</font>所选片源吗?", function(btn) {
								if (btn != 'yes') {
									return;
								}
								Ext.getBody().mask("数据处理中...", "x-mask-loading");
								Ext.Ajax.request({
									url : '/walker/movie/offlineMovie',
									success : function(response) {
										Ext.getBody().unmask();
										Ext.Msg.alert("提示", "下线成功!", function() {
											store.reload();
											com.walker.xml.modifyMediaSeries.programGrid.store.reload();
											com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
										});
									},
									failure : function(response) {
										Ext.getBody().unmask();
										Ext.Msg.alert("提示", "下线失败!");
									},
									params : {
										movieId : movieId
									}
								});
							});
						}
						else {
							Ext.Msg.alert("提示", "该片源已下线!");
						}
					}
					else {
						Ext.Msg.alert("提示", "请选择一条记录!");
					}
				}
			}, '-', {
				text : '删除',
				iconCls : 'btnIconDel',
				handler : function() {
					var records = grid.getSelectionModel().getSelections();
					if(records.length == 1) {
						var record = records[0];
						var movieId = record.data.MOVIE_ID;
						Ext.Msg.confirm('提示',"确定<font color='red'>删除</font>所选片源吗?", function(btn) {
							if (btn != 'yes') {
								return;
							}
							Ext.getBody().mask("数据处理中...", "x-mask-loading");
							Ext.Ajax.request({
								url : '/walker/movie/deleteMovie',
								success : function(response) {
									Ext.getBody().unmask();
									Ext.Msg.alert("提示", "删除成功!", function() {
										store.reload();
										com.walker.xml.modifyMediaSeries.programGrid.store.reload();
										com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
									});
								},
								failure : function(response) {
									Ext.getBody().unmask();
									Ext.Msg.alert("提示", "删除失败!");
								},
								params : {
									movieId : movieId
								}
							});
						});
					}
					else {
						Ext.Msg.alert("提示", "请选择一条记录!");
					}
				}
			}, '-', {
				text : '刷新',
				iconCls : 'btnIconRefresh',
				handler : function() {
					store.reload();
				}
			}, '-', {
				text : '取消',
				iconCls : 'btnIconReset',
				handler : function() {
					Ext.WindowMgr.getActive().close();
				}
			} ]
		}).show();
		
		var loadingLoadMask = new Ext.LoadMask(grid.getEl(), {msg:"数据加载中..."});
		store.on("beforeload", function(){
			loadingLoadMask.show();
		});
		store.on("load", function(){
			loadingLoadMask.hide();
		});
		
		store.load({params:{ programId: programId}});
	}
};

com.walker.xml.modifyMediaSeries.onlineProgram = function() {
	if (com.walker.xml.modifyMediaSeries.programId) {
		var ids = [];
		ids.push(com.walker.xml.modifyMediaSeries.programId);
		
		var sumBtn = function(btn){
			if(btn!='yes') {
				return;
			}
			
			Ext.Ajax.request({
				url : '/walker/program/onlineProgram',
				success : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.success){
						Ext.Msg.alert("提示", "操作成功!", function() {
							com.walker.xml.modifyMediaSeries.programGrid.store.reload();
							com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
						});
					}
				},
				failure : function(response) {
					Ext.Msg.alert("提示", "操作失败!");
				},
				params : {
					programIds : ids
				}
			});
		};
		Ext.Msg.confirm('提示',"确定上线该分集吗?", sumBtn);
	}
};

com.walker.xml.modifyMediaSeries.offlineProgram = function() {
	if (com.walker.xml.modifyMediaSeries.programId) {
		var ids = [];
		ids.push(com.walker.xml.modifyMediaSeries.programId);
		
		var sumBtn = function(btn){
			if(btn!='yes') {
				return;
			}
			
			Ext.Ajax.request({
				url : '/walker/program/offlineProgram',
				success : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.success){
						Ext.Msg.alert("提示", "操作成功!", function() {
							com.walker.xml.modifyMediaSeries.programGrid.store.reload();
							com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
						});
					}
				},
				failure : function(response) {
					Ext.Msg.alert("提示", "操作失败!");
				},
				params : {
					programIds : ids
				}
			});
		};
		Ext.Msg.confirm('提示',"确定下线该分集吗?", sumBtn);
	}
};

/**
 * 保存分集内容
 */
com.walker.xml.modifyMediaSeries.saveProgram = function() {
	if (com.walker.xml.modifyMediaSeries.editProgramForm.getForm().isValid()) {
		function submitForm () {
			com.walker.xml.modifyMediaSeries.editProgramForm.getEl().mask("数据提交中...","x-mask-loading");
			com.walker.xml.modifyMediaSeries.editProgramForm.getForm().submit( {
				method : "POST",
				url : "/walker/program/updProgram",
				params : {
					PROGRAM_ID : com.walker.xml.modifyMediaSeries.programId
				},
				success : function(response) {
					com.walker.xml.modifyMediaSeries.editProgramForm.getEl().unmask();
					Ext.Msg.alert("提示","保存成功!", function() {
						com.walker.xml.modifyMediaSeries.programGrid.store.reload();
						com.walker.xml.modifyMediaSeries.refreshSeriesGrid();
					});
				},
				failure : function(response) {
					com.walker.xml.modifyMediaSeries.editProgramForm.getEl().unmask();
					Ext.Msg.alert("提示", "保存失败!");
				}
			});
		}
		

		var confirmTitle = '提示';
		var confirmContent = '确定保存修改?';
		var confirmIcon = Ext.MessageBox.QUESTION;
		if (com.walker.xml.modifyMediaSeries.editProgramForm.status == '01') {
			confirmTitle = '警告';
			confirmContent = '该节目已上线,确定保存修改?';
			confirmIcon = Ext.MessageBox.WARNING;
		}
		
		Ext.Msg.show({
			title: confirmTitle,
			msg: confirmContent,
			buttons: Ext.Msg.YESNO,
			fn: function(btn) {
				if (btn != 'yes') {
					return;
				}
				submitForm();
			},
			icon: confirmIcon
		});
	}
};
