Ext.namespace("com.walker.xml.mediaSeries");

com.walker.xml.mediaSeries.queryFormPnl = null;
com.walker.xml.mediaSeries.seriesStore = null;
com.walker.xml.mediaSeries.seriesGridPnl = null;

com.walker.xml.mediaSeries.episodeType = com.walker.common.getPubCodeStore('MEDIA_EPISODE_TYPE', '1');
com.walker.xml.mediaSeries.mediaTypeStore = com.walker.common.getPubCodeStore('MEDIA_TYPE', '1');
com.walker.xml.mediaSeries.statusStore = com.walker.common.getPubCodeStore('LINE_STATUS', '1');

com.walker.xml.mediaSeries.getQueryForm = function(){
	var queryForm = new Ext.FormPanel({
		layout : 'form',
		region : 'north',
		autoHeight:false,
		height: 95,
//		border: false,
		labelWidth : 120,
		labelAlign : 'right',
		bodyStyle : 'padding-top:10px; border-bottom-width: 1px;',
		margins : '0 0 4 0',
		tbar:[ {
			text : '编辑',
			iconCls : 'btnIconUpdate',
			handler : function() {
				com.walker.xml.mediaSeries.modifySeries();
			}
		}, {
			text : '删除',
			iconCls : 'btnIconDel',
			handler : function() {
				com.walker.xml.mediaSeries.deleteSeries();
			}
		}, '-', {
			// 上线
			text : '上线',
			iconCls : 'btnIconOnLine',
			handler : function() {
				com.walker.xml.mediaSeries.onLineSeries();
			}
		}, {
			// 下线
			text : '下线',
			iconCls : 'btnIconOffLine',
			handler : function() {
				com.walker.xml.mediaSeries.offLineSeries();
			}
		}, '-', {
			// 上线
			text : '智能上线',
			iconCls : 'btnIconAuto',
			handler : function() {
				com.walker.xml.mediaSeries.autoOnLineSeries();
			}
		}, '-', {
			text : '检查分集',
			iconCls : 'btnIconCheck',
			handler : function() {
				com.walker.xml.mediaSeries.checkProgram();
			}
		}, '-', {
			text : '生成工单',
			iconCls : 'btnIconCheck',
			handler : function() {
				com.walker.xml.mediaSeries.buildSeriesXml();
			}
		}, '->', {
			// 查询
			text : '查询',
			iconCls : 'btnIconSearch',
			handler : function() {
				com.walker.xml.mediaSeries.querySeries();
			}
		}, {
			// 查询
			text : '重置',
			iconCls : 'btnIconRecover',
			handler : function() {
				com.walker.xml.mediaSeries.conditionReset();
			}
		} ],
		items : [ {
			layout : "column",
			border : false,
			items : [ {
				columnWidth : 0.27,
				layout : "form",
				border : false,
				items : [ {
					xtype : 'combo',
					fieldLabel : '内容分类',
					name : 'TYPE',
		            triggerAction: 'all',
		            displayField : 'VALUE', 
		            valueField : 'CODE', 
		            store: com.walker.xml.mediaSeries.mediaTypeStore,
		            mode: 'local',
		            editable:false,
		            anchor : '100%'
				} ]
			}, {
				columnWidth : 0.27,
				layout : "form",
				border : false,
				items : [ {
					xtype : 'datefield',
					fieldLabel : '注入日期',
					format : 'Y-m-d',
					name : 'CDN_TIME',
					editable:false,
					anchor : '100%'
				} ]
			}, {
				columnWidth : 0.27,
				layout : "form",
				border : false,
				items : [ {
					xtype : 'combo',
					fieldLabel : '上线状态',
					name : 'STATUS',
		            triggerAction: 'all',
		            displayField : 'VALUE', 
		            valueField : 'CODE', 
		            store: com.walker.xml.mediaSeries.statusStore,
		            mode: 'local',
		            editable:false,
		            anchor : '100%'
				} ]
			} ]
		}, {
			layout : "column",
			border : false,
			items : [ {
				columnWidth : 0.27,
				layout : "form",
				border : false,
				items : [ {
					xtype : 'textfield',
					fieldLabel : '合集ID',
					name : 'SERIES_ID',
					anchor : '100%'
				} ]
			}, {
				columnWidth : 0.27,
				layout : "form",
				border : false,
				items : [ {
					xtype : 'textfield',
					fieldLabel : '合集名称',
					name : 'NAME',
					anchor : '100%'
				} ]
			} ]
		} ]
	});
	
	com.walker.xml.mediaSeries.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.xml.mediaSeries.getGridPnl = function(){
	
	var store = new Ext.data.Store({
		url : '/walker/series/listPage',
		reader : new Ext.data.JsonReader({
			fields : ["SERIES_ID", "ORIGINAL_ID", "CP_ID", "EPISODE_TYPE", "TYPE"
		          , "TYPE_NAME", "NAME", "FULLSPELL", "SIMPLESPELL", "ALIAS_NAME", "EN_NAME", "LANGUAGE"
		          , "CAPTION", "RELEASE_TIME", "AREA", "PROGRAM_COUNT", "NEW_PROGRAM", "DIRECTOR"
		          , "PRODUCER", "ADAPTOR", "ACTOR", "KIND", "TAGS"
		          , "CLARITY", "COPYRIGHT", "COPYRIGHT_DATE", "IMPORT_SOURCE"
		          , "STATUS", "CREATE_TIME", "CDN_TIME", "CDN_ID", "ONLINE_TIME", "UNLINE_TIME"
		          , "REAL_PROGRAM_COUNT", "ONLINE_PROGRAM_COUNT", "IS_LACK_PROGRAM", "IS_REPEAT_PROGRAM"],
			root : 'rows',
			totalProperty : 'total'
		}),
        baseParams : {
			limit : 15,
			start: 0
		}
	});

	var listLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:"数据加载中..."});
	
	store.on("load", function() {
		if(listLoadMask) {
			listLoadMask.hide();
		}
	});
	
	com.walker.xml.mediaSeries.seriesStore = store;
	
	// 创建列模型的多选框模型.
    var sm = new Ext.grid.CheckboxSelectionModel();	
	var model = new Ext.grid.ColumnModel([sm,{
				header : "合集ID",
				dataIndex : "SERIES_ID",
				hidden : true,
				sortable : false,
				width : 250,
				align : 'center'
			}, {
				header : "合集名称",
				dataIndex : "NAME",
				sortable : true,
				width : 250,
				align : 'left'
			}, {
				header : "影片类型",
				dataIndex : "EPISODE_TYPE",
				sortable : true,
				width : 100,
				align : 'center',
				renderer : function(value,metadata){
					return com.walker.common.renderer.getComboValue(com.walker.xml.mediaSeries.episodeType, value, {
						"0" : 'blue',
						"1" : 'green'
					});
				}
			}, {
				header : "内容分类",
				dataIndex : "TYPE_NAME",
				sortable : true,
				width : 80,
				align : 'center'
			},{
				header : "总集数",
				dataIndex : "PROGRAM_COUNT",
				sortable : true,
				width : 80,
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
				header : "实际分集数",
				dataIndex : "REAL_PROGRAM_COUNT",
				sortable : true,
				width : 100,
				align : 'center',
				renderer : function(value,metadata, record, rowindex){
					var ONLINE_PROGRAM_COUNT = record.data.ONLINE_PROGRAM_COUNT;
					return ONLINE_PROGRAM_COUNT + '/' + value;
				}
			}, {
				header : "分集状态",
				dataIndex : "ONLINE_PROGRAM_COUNT",
				sortable : true,
				width : 80,
				align : 'center',
				renderer : function(value,metadata, record, rowindex){
					metadata.css += " x-grid3-cell-icon";
					var IS_LACK_PROGRAM = record.data.IS_LACK_PROGRAM;
					if (value == 0) {
						return '<div title="没有分集" class="novod"></div>';
					}
					else if(IS_LACK_PROGRAM == 1) {
						return '<div title="分集缺集" class="lackvod"></div>';
					}
					return '<div title="分集正常" class="okvod"></div>';
				}
			}, {
				header : "海报状态",
				sortable : true,
				width : 80,
				dataIndex : "SERIES_ID",
				align : 'center',
				renderer : function(value, metadata, record, rowindex){
					metadata.css += " x-grid3-cell-icon";
					var divId = Ext.id();
					jQuery.ajax({
						url : '/walker/picture/checkPictureBySeriesId',
						dataType: "json",
						data : {seriesId : value},
						success : function(data, result) {
							var div = jQuery("#" + divId);
							if(data.isLacked) {
								div.attr("title", "海报缺失").addClass("lackpicture");
							}
							else {
								div.attr("title", "海报正常").addClass("okpicture");
							}
						}
					});
					
					return "<div id='" + divId + "'></div>";
				}
			}
	]);
	
	var grid = new Ext.grid.GridPanel({
		layout : 'fit',
		title : '合集列表',
		autoWidth : true,
		autoScroll : true,
		cm : model,
		sm : sm,
		store : store,
		stripeRows : true,
		region : 'center',
//		border : false,
		style : 'border-top-width:1px;',
		bbar : new Ext.PagingToolbar({
			pageSize : 15,
			store : store,
			displayInfo : true,
			region : 'center'
		})
	});
	
	com.walker.xml.mediaSeries.seriesGridPnl = grid;
	
	com.walker.xml.mediaSeries.seriesStore.load();
	return grid;
};

com.walker.xml.mediaSeries.querySeries = function() {
	com.walker.xml.mediaSeries.seriesStore.baseParams = {
		NAME : com.walker.xml.mediaSeries.queryFormPnl.find("name", "NAME")[0].getValue(),
		TYPE : com.walker.xml.mediaSeries.queryFormPnl.find("name", "TYPE")[0].getValue(),
		STATUS : com.walker.xml.mediaSeries.queryFormPnl.find("name", "STATUS")[0].getValue(),
		CDN_TIME : com.walker.xml.mediaSeries.queryFormPnl.find("name", "CDN_TIME")[0].getRawValue(),
		SERIES_ID : com.walker.xml.mediaSeries.queryFormPnl.find("name","SERIES_ID")[0].getValue()
	};
	com.walker.xml.mediaSeries.seriesStore.load({params:{ start: 0, limit: 15}});
};

com.walker.xml.mediaSeries.conditionReset = function() {
	com.walker.xml.mediaSeries.queryFormPnl.find("name", "NAME")[0].setValue("");
	com.walker.xml.mediaSeries.queryFormPnl.find("name", "TYPE")[0].setValue("");
	com.walker.xml.mediaSeries.queryFormPnl.find("name", "STATUS")[0].setValue("");
	com.walker.xml.mediaSeries.queryFormPnl.find("name", "CDN_TIME")[0].setValue("");
	com.walker.xml.mediaSeries.queryFormPnl.find("name", "SERIES_ID")[0].setValue("");
};

com.walker.xml.mediaSeries.onLineSeries = function() {
	var records = com.walker.xml.mediaSeries.seriesGridPnl.getSelectionModel().getSelections();
	if(records.length > 0)
	{
		var ids = [];
		for (var i = 0; i < records.length; i++) {
			if("01" != records[i].get("STATUS")){
				ids.push(records[i].get("SERIES_ID"));
			}
		}
		
		if(ids.length > 0) {
		
			var sumBtn = function(btn){
				if(btn!='yes'){return;}
				
				Ext.Ajax.request({
					url : '/walker/series/onlineSeries',
					success : function(response) {
						var result = Ext.util.JSON.decode(response.responseText);
						if(result.success) {
							result = result.data;
							Ext.Msg.alert("提示","成功上线" + result.successCount + "个合集!", function() {
								com.walker.xml.mediaSeries.seriesGridPnl.store.reload();
							});
						} else {
							if(result.errorMsg) {
								Ext.Msg.alert("提示", result.errorMsg);
							}
							else {
								Ext.Msg.alert("提示","操作失败!");
							}
						}
					},
					failure : com.walker.xml.mediaSeries.doFail,
					params : {
						seriesIds : ids
					}
				});
			};
			Ext.Msg.confirm('提示', "确定上线所选媒资内容吗?",sumBtn);
		}
		else {
			Ext.Msg.alert("提示", "未选择任何可上线记录!");
		}
	} else {
		Ext.Msg.alert("提示", "请至少选择一条记录!");
	}
};

com.walker.xml.mediaSeries.autoOnLineSeries = function() {
	var records = com.walker.xml.mediaSeries.seriesGridPnl.getSelectionModel().getSelections();
	if(records.length == 1)
	{
		var record = records[0];
		
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			
			Ext.Ajax.request({
				url : '/walker/series/autoOnlineSeries',
				success : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.success) {
						Ext.Msg.alert("提示","智能上线成功!", function() {
							com.walker.xml.mediaSeries.CURRENT_SERIES_ID = record.get("SERIES_ID");
							
							com.walker.xml.mediaSeries.seriesGridPnl.store.reload( {
								callback: function() {
									var datas = com.walker.xml.mediaSeries.seriesGridPnl.store.data;
									if (datas) {
										for(var i = 0; i < datas.length; i++) {
											var rec = com.walker.xml.mediaSeries.seriesGridPnl.store.getAt(i);
											if(com.walker.xml.mediaSeries.CURRENT_SERIES_ID == rec.get("SERIES_ID")) {
												com.walker.xml.mediaSeries.seriesGridPnl.getSelectionModel().selectRow(i);
												break;
											}
										}
									}
								}
							});
						});
					} else {
						if(result.err_msg) {
							Ext.Msg.alert("失败", "智能上线失败,<font color='red'>原因:" + result.err_msg + "</front>");
						}
						else {
							Ext.Msg.alert("提示","操作失败!");
						}
					}
				},
				failure : com.walker.xml.mediaSeries.doFail,
				params : {
					seriesId : record.get("SERIES_ID")
				}
			});
		};
		Ext.Msg.confirm('提示', "确定智能上线所选媒资内容吗?",sumBtn);
	} else {
		Ext.Msg.alert("提示", "请选择一条记录!");
	}
};

com.walker.xml.mediaSeries.offLineSeries = function() {
	var records = com.walker.xml.mediaSeries.seriesGridPnl.getSelectionModel().getSelections();
	if(records.length > 0)
	{
		var ids = [];
		for (var i = 0; i < records.length; i++) {
			if("01" == records[i].get("STATUS") ){
				ids.push(records[i].get("SERIES_ID"));
			}
		}
		if (ids.length > 0) {
			var sumBtn = function(btn){
				if(btn!='yes'){return;}
				
				Ext.Ajax.request({
					url : '/walker/series/offlineSeries',
					success : function(response) {
						var result = Ext.util.JSON.decode(response.responseText);
						if(result.success) {
							result = result.data;
							Ext.Msg.alert("提示","成功下线" + result.successCount + "个合集!", function() {
								com.walker.xml.mediaSeries.seriesGridPnl.store.reload();
							});
						} else {
							if(result.err_msg) {
								Ext.Msg.alert("提示", result.err_msg);
							}
							else {
								Ext.Msg.alert("提示","操作失败!");
							}
						}
					},
					failure : com.walker.xml.mediaSeries.doFail,
					params : {
						seriesIds : ids
					}
				});
			};
			Ext.Msg.confirm('提示', "确定下线所选媒资内容吗?",sumBtn);
		}
		else {
			Ext.Msg.alert("提示", "未选择任何可下线记录!");
		}
	} else {
		Ext.Msg.alert("提示", "请至少选择一条记录!");
	}
};

com.walker.xml.mediaSeries.doFail = function(response) {
	Ext.Msg.alert("提示", "操作发生异常!");
};

com.walker.xml.mediaSeries.modifySeries = function(){
	
	var rec = com.walker.common.getSelectRecord(com.walker.xml.mediaSeries.seriesGridPnl,true);
	if(rec){
		var winTitle = ('编辑合集【' + rec.get('NAME') + '】');
		WALKER.popWindow({
			url:"jsp/xml/modifyMediaSeries.jsp",
			openWindow: top,
			win:{
				initialConfig:{
					title: winTitle,
					width: 1000,
					height: 600,
					modal: true,
					minimizable: false,
					maximizable: false,
					maximized: true
				}
			}
		});
	}
};

com.walker.xml.mediaSeries.checkProgram = function(){
	
	var rec = com.walker.common.getSelectRecord(com.walker.xml.mediaSeries.seriesGridPnl,true);
	if(rec){
		var seriesId = rec.get("SERIES_ID");
		
		var myLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:"数据处理中..."});
		myLoadMask.show();
		Ext.Ajax.request({
			url : '/walker/series/checkSeries',
			success : function(response) {
				myLoadMask.hide();
				Ext.Msg.alert("提示", "缺集检查完成!", function() {
					com.walker.xml.mediaSeries.seriesGridPnl.store.reload();
				});
			},
			failure : function(response) {
				myLoadMask.hide();
				var result = Ext.util.JSON.decode(response.responseText);
				if(result.err_msg){
					Ext.Msg.alert("提示", result.err_msg);
				}else{
					Ext.Msg.alert("提示", "缺集检查失败!");
				}
			},
			params : {
				seriesId : seriesId
			}
		});
	}
};


var loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"数据处理中..."});

//删除合集
com.walker.xml.mediaSeries.deleteSeries = function(){
	var rec = com.walker.common.getSelectRecord(com.walker.xml.mediaSeries.seriesGridPnl,true);
	if(rec){
		var status = rec.get("STATUS");
		if(status === '01') {
			Ext.Msg.alert("提示", "合集已上线,无法删除!");
			return;
		}
		
		var sumBtn = function(btn){
			if(btn!='yes') {return;}
			var seriesId = rec.data.SERIES_ID;
			
			var myLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:"数据处理中..."});
			myLoadMask.show();
			Ext.Ajax.request({
				url : '/walker/series/deleteSeries',
				success : function(response) {
					myLoadMask.hide();
					Ext.Msg.alert("提示", "删除成功!", function() {
						com.walker.xml.mediaSeries.seriesGridPnl.store.reload();
					});
				},
				failure : function(response) {
					myLoadMask.hide();
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.err_msg){
						Ext.Msg.alert("提示", result.err_msg);
					}else{
						Ext.Msg.alert("提示", "删除失败!");
					}
				},
				params : {
					seriesId : seriesId
				}
			});
		};
		Ext.Msg.confirm('提示', "确定<font color='red'>删除</font>所选合集吗?", sumBtn);
	}
};

com.walker.xml.mediaSeries.buildSeriesXml = function(){
	var rec = com.walker.common.getSelectRecord(com.walker.xml.mediaSeries.seriesGridPnl,true);
	if(rec){
		var seriesId = rec.data.SERIES_ID;
		Ext.Msg.alert("提示", seriesId);
		var myLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:"数据处理中..."});
		myLoadMask.show();
		
		Ext.Ajax.request({
			url : '/walker/series/buildSeriesXml',
			success : function(response) {
				myLoadMask.hide();
				var result = Ext.decode(response.responseText);
				if (result.success) {
					Ext.Msg.alert("提示", "操作成功!");
					com.walker.file.message.queryMessage();
				}else{
					Ext.Msg.alert("提示", result.err_msg);
				}
			},
			failure : function(response) {
				Ext.Msg.alert("提示", "操作失败");
			},
			params : {
				ids : seriesId
			}
		});
	}
	
	
	var selRecords= com.walker.common.getSelectRecord(com.walker.xml.mediaSeries.seriesGridPnl,true);
	if(!selRecords){
		return;
	}
	
	var ids="";
	for ( var i = 0; i < selRecords.length; i++) {
		ids += ","+selRecords[i].data.SERIES_ID;
	}
	
};


Ext.onReady(function() {
	Ext.QuickTips.init();
	new Ext.Viewport({
		layout : 'border',
		items : [ 
		    com.walker.xml.mediaSeries.getQueryForm(),
		    com.walker.xml.mediaSeries.getGridPnl()
		]
	});
});