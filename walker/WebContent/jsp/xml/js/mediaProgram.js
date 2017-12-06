Ext.namespace("com.walker.xml.mediaProgram");

com.walker.xml.mediaProgram.queryFormPnl = null;
com.walker.xml.mediaProgram.programStore = null;
com.walker.xml.mediaProgram.programGridPnl = null;
com.walker.xml.mediaProgram.statusStore = null;

com.walker.xml.mediaProgram.clarityStore = com.walker.common.getPubCodeStore('MEIDA_CLARITY', '1');
com.walker.xml.mediaProgram.episodeType = com.walker.common.getPubCodeStore('MEDIA_EPISODE_TYPE', '1');
com.walker.xml.mediaProgram.statusStore = com.walker.common.getPubCodeStore('LINE_STATUS', '1');

com.walker.xml.mediaProgram.getQueryForm = function(){
	var queryForm = new Ext.FormPanel({
		layout : 'form',
		region : 'north',
		autoHeight:false,
		height: 95,
		labelWidth : 120,
		labelAlign : 'right',
		bodyStyle : 'padding-top: 10px;',
//		border : false,
		style : 'border-bottom-width: 1px;',
		margins : '0 0 4 0',
		tbar:[ {
			text : '编辑',
			iconCls : 'btnIconUpdate',
			handler : function() {
				com.walker.xml.mediaProgram.modifyProgram();
			}
		}, {
			text : '删除',
			iconCls : 'btnIconDel',
			handler : function() {
				com.walker.xml.mediaProgram.deleteProgram();
			}
		}, '-', {
			// 上线
			text : '上线',
			iconCls : 'btnIconOnLine',
			handler : function() {
				com.walker.xml.mediaProgram.onLineProgram();
			}
		}, {
			// 下线
			text : '下线',
			iconCls : 'btnIconOffLine',
			handler : function() {
				com.walker.xml.mediaProgram.offLineProgram();
			}
		}, '-', {
			text : '绑定合集',
			iconCls : 'btnIconBind',
			handler : function() {
				com.walker.xml.mediaProgram.bindingSeries();
			}
		}, '-', {
			text : '拆条',
			iconCls : 'btnIconSplit',
			handler : function() {
				com.walker.xml.mediaProgram.splitProgram();
			}
		}, '-', {
			text : '下载',
			iconCls : 'btnIconDownload',
			handler : function() {
				com.walker.xml.mediaProgram.downloadMovie();
			}
		}, '-', {
			text : '导出记录',
			iconCls : 'btnIconExcel',
			handler : function() {
				com.walker.xml.mediaProgram.exportRecords();
			}
		},'->',{
			// 查询
			text : '查询',
			iconCls : 'btnIconSearch',
			handler : function() {
				com.walker.xml.mediaProgram.queryProgram();
			}
		},{
			// 查询
			text : '重置',
			iconCls : 'btnIconRecover',
			handler : function() {
				com.walker.xml.mediaProgram.conditionReset();
			}
		}],
		items : [{
			layout : "column",
			border : false,
			items : [ {
				columnWidth : 0.27,
				layout : "form",
				border : false,
				items : [ {
					xtype : 'textfield',
					fieldLabel : '分集名称',
					name : 'NAME',
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
					editable: false,
					anchor : '100%'
				} ]
			}, {
				columnWidth : 0.27,
				layout : "form",
				border : false,
				items : [ {
					xtype : 'datefield',
					fieldLabel : '下线日期',
					format : 'Y-m-d',
					name : 'UNLINE_TIME',
					editable: false,
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
					name : 'SERIES_NAME',
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
		            store: com.walker.xml.mediaProgram.statusStore,
		            mode: 'local',
		            editable: false,
		            anchor : '100%' 
				} ]
			} ]
		}]
	});
	
	com.walker.xml.mediaProgram.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.xml.mediaProgram.getGridPnl = function(){
	
	var store = new Ext.data.Store({
		url : '/walker/program/getProgramPage',
		reader : new Ext.data.JsonReader({
			fields : ["PROGRAM_ID", "ORIGINAL_ID", "SERIES_ID", "NAME", "ORIGINAL_SERIES_ID"
			          , {name:"SERIALNO",type:"int"}, "DURATION", "AWARDS", "DESCRIPTION", "KEYWORDS", "DIRECTOR"
			          , "ADAPTOR", "ACTOR", "UPDATE_TIME", "IMPORT_SOURCE"
			          , "CREATE_TIME", "MODIFY_TIME", "STATUS", "CDN_STATUS", "CDN_TIME", "CDN_ID"
			          , "MOVIE_COUNT", "ONLINE_MOVIE_COUNT", "ONLINE_TIME", "UNLINE_TIME"
			          , "SERIES_NAME", "EPISODE_TYPE", "TYPE_NAME", "SERIES_STATUS"],
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
	
	com.walker.xml.mediaProgram.programStore = store;
	
	// 创建列模型的多选框模型.
    var sm = new Ext.grid.CheckboxSelectionModel();	
	var model = new Ext.grid.ColumnModel([
	    sm, {
			header : "原始合集ID",
			dataIndex : "ORIGINAL_SERIES_ID",
			hidden : true,
			sortable : true,
			width : 250,
			align : 'center'
		}, {
			header : "合集ID",
			dataIndex : "SERIES_ID",
			hidden : true,
			sortable : true,
			width : 250,
			align : 'center'
		}, {
			header : "分集ID",
			dataIndex : "PROGRAM_ID",
			hidden : true,
			sortable : true,
			width : 250,
			align : 'center'
		}, {
			header : "分集名称",
			dataIndex : "NAME",
			sortable : true,
			width : 250,
			align : 'left'
		}, {
			header : "合集名称",
			dataIndex : "SERIES_NAME",
			sortable : true,
			width : 250,
			align : 'left'
		}, {
			header : "影片类型",
			dataIndex : "EPISODE_TYPE",
			sortable : true,
			width : 80,
			align : 'center',
			renderer : function(value, metadata){
				return com.walker.common.renderer.getComboValue(com.walker.xml.mediaProgram.episodeType, value, {
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
		}, {
			header : "集号",
			dataIndex : "SERIALNO",
			sortable : true,
			width : 60,
			align : 'right'
		}, {
			header : "时长(秒)",
			dataIndex : "DURATION",
			sortable : true,
			width : 80,
			hidden : true,
			align : 'right'
		}, {
			header : "导演",
			dataIndex : "DIRECTOR",
			sortable : true,
			hidden : true,
			width : 100,
			align : 'left'
		}, {
			header : "编剧",
			dataIndex : "ADAPTOR",
			sortable : true,
			hidden : true,
			width : 100,
			align : 'left'
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
					return "<a href='javascript:void();' class='movielack' onclick='com.walker.xml.mediaProgram.showMovieList(\"" + programId + "\");return false;'>0/" + movieCount + "</a>";
				}
				else {
					return "<a href='javascript:void();' class='movienormal' onclick='com.walker.xml.mediaProgram.showMovieList(\"" + programId + "\");return false;'>" + value + "/" + movieCount + "</a>";
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
		}
	]);
	
	var grid = new Ext.grid.GridPanel({
			layout : 'fit',
			title : '分集列表',
			autoWidth : true,
			autoScroll : true,
			cm : model,
			sm : sm,
			store : store,
			stripeRows : true,
			region : 'center',
//			border : false,
			style : 'border-top-width:1px;',
			bbar : new Ext.PagingToolbar({
				pageSize : 15,
				store : store,
				displayInfo : true,
				region : 'center'
			})
	});
	
	com.walker.xml.mediaProgram.programGridPnl = grid;
	
	com.walker.xml.mediaProgram.programStore.load();
	return grid;
};

com.walker.xml.mediaProgram.showMovieList = function(programId) {
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
					return com.walker.common.renderer.getComboValue(com.walker.xml.mediaProgram.clarityStore, value, {
						"00" : 'black',
						"01" : 'green',
						"02" : 'red',
						"03" : 'blue'
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
						return '<font color="green" title="上线时间:' + onlineTime + '">已上线</font>';
					}else if (value == '02') {
						var unlineTime = "";
						if(record.data.UNLINE_TIME) {
							unlineTime = record.data.UNLINE_TIME;
						}
						return '<font color="red" title="下线时间:' + unlineTime + '">已下线</font>';
					}
				}
			}
		]);
		
		var grid = new Ext.grid.GridPanel({
			layout : 'fit',
			autoWidth : true,
			autoScroll : true,
			cm : model,
			sm : sm,
			store : store,
			stripeRows : true,
			region : 'center',
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
								
								var optLoadMask = new Ext.LoadMask(Ext.WindowMgr.getActive().getEl(), {msg:"数据提交中..."});
								optLoadMask.show();
								
								Ext.Ajax.request({
									url : '/walker/movie/onlineMovie',
									success : function(response) {
										optLoadMask.hide();
										Ext.Msg.alert("提示", "上线成功!", function() {
											store.reload();
											com.walker.xml.mediaProgram.programStore.reload();
										});
									},
									failure : function(response) {
										optLoadMask.hide();
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
								
								var optLoadMask = new Ext.LoadMask(Ext.WindowMgr.getActive().getEl(), {msg:"数据提交中..."});
								optLoadMask.show();
								
								Ext.Ajax.request({
									url : '/walker/movie/offlineMovie',
									success : function(response) {
										optLoadMask.hide();
										Ext.Msg.alert("提示", "下线成功!", function() {
											store.reload();
											com.walker.xml.mediaProgram.programStore.reload();
										});
									},
									failure : function(response) {
										optLoadMask.hide();
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
						
						var status = record.get("STATUS");
						if(status === '01') {
							Ext.Msg.alert("提示", "片源已上线,无法删除!");
							return;
						}
						
						var movieId = record.data.MOVIE_ID;
						Ext.Msg.confirm('提示',"确定<font color='red'>删除</font>所选片源吗?", function(btn) {
							if (btn != 'yes') {
								return;
							}

							var optLoadMask = new Ext.LoadMask(Ext.WindowMgr.getActive().getEl(), {msg:"数据提交中..."});
							optLoadMask.show();

							Ext.Ajax.request({
								url : '/walker/movie/deleteMovie',
								success : function(response) {
									optLoadMask.hide();
									Ext.Msg.alert("提示", "删除成功!", function() {
										store.reload();
										com.walker.xml.mediaProgram.programStore.reload();
									});
								},
								failure : function(response) {
									optLoadMask.hide();
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
		});
		
		new Ext.Window( {
			title : "片源列表",
			layout : 'border',
			width : 700,
			height : 350,
			modal : true,
			border : false,
			constrain : true,
			autoScroll : true,
			maximizable : false,
			minimizable : false,
			plain: true,
			resizable : false,
			closeAction : 'close',
			items : [ grid ]
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

com.walker.xml.mediaProgram.queryProgram = function() {
	
	com.walker.xml.mediaProgram.programStore.baseParams = {
		NAME : com.walker.xml.mediaProgram.queryFormPnl.find("name","NAME")[0].getValue(),
		STATUS : com.walker.xml.mediaProgram.queryFormPnl.find("name","STATUS")[0].getValue(),
		CDN_TIME : com.walker.xml.mediaProgram.queryFormPnl.find("name","CDN_TIME")[0].getRawValue(),
		SERIES_NAME : com.walker.xml.mediaProgram.queryFormPnl.find("name","SERIES_NAME")[0].getValue(),
		SERIES_ID : com.walker.xml.mediaProgram.queryFormPnl.find("name","SERIES_ID")[0].getValue(),
		UNLINE_TIME : com.walker.xml.mediaProgram.queryFormPnl.find("name","UNLINE_TIME")[0].getValue()
	};
	com.walker.xml.mediaProgram.programStore.load({params:{ start: 0, limit: 15}});
};

com.walker.xml.mediaProgram.conditionReset = function() {
	com.walker.xml.mediaProgram.queryFormPnl.find("name","NAME")[0].setValue("");
	com.walker.xml.mediaProgram.queryFormPnl.find("name","STATUS")[0].setValue("");
	com.walker.xml.mediaProgram.queryFormPnl.find("name","CDN_TIME")[0].setValue("");
	com.walker.xml.mediaProgram.queryFormPnl.find("name","SERIES_NAME")[0].setValue("");
	com.walker.xml.mediaProgram.queryFormPnl.find("name","SERIES_ID")[0].setValue("");
	com.walker.xml.mediaProgram.queryFormPnl.find("name","UNLINE_TIME")[0].setValue("");
};

com.walker.xml.mediaProgram.onLineProgram = function() {
	var records = com.walker.xml.mediaProgram.programGridPnl.getSelectionModel().getSelections();
	if(records.length > 0)
	{
		var ids = [];
		for(var i = 0; i < records.length; i++){
			if("01" != records[i].data.STATUS) {
				ids.push(records[i].data.PROGRAM_ID);
			}
	    }
		if(ids.length > 0) {
			var sumBtn = function(btn){
				if(btn!='yes'){return;}
				
				Ext.Ajax.request({
					url : '/walker/program/onlineProgram',
					success : function(response) {
						var result = Ext.util.JSON.decode(response.responseText);
						if(result.success){
							
							Ext.Msg.alert("提示","成功上线" + result.successCount + "个分集!", function() {
								com.walker.xml.mediaProgram.programGridPnl.store.reload();
							});
						}else{
							Ext.Msg.alert("提示","操作失败!");
						}
					},
					failure : com.walker.xml.mediaProgram.doFail,
					params : {
						programIds : ids
					}
				});
			};
			Ext.Msg.confirm('提示',"确定上线所选媒资内容吗?",sumBtn);
		}
		else {
			Ext.Msg.alert("提示","未选择任何可上线记录!");
		}
	} else {
		Ext.Msg.alert("提示","请至少选择一条记录!");
	}
};

com.walker.xml.mediaProgram.offLineProgram = function() {
	var records = com.walker.xml.mediaProgram.programGridPnl.getSelectionModel().getSelections();
	if(records.length > 0)
	{
		var ids = [];
		for(var i = 0; i < records.length; i++){
			if("01" == records[i].data.STATUS) {
				ids.push(records[i].data.PROGRAM_ID);
			}
	    }
		if(ids.length > 0) {
			var sumBtn = function(btn){
				if(btn!='yes'){return;}
				
				Ext.Ajax.request({
					url : '/walker/program/offlineProgram',
					success : function(response) {
						var result = Ext.util.JSON.decode(response.responseText);
						if(result.success){
							
							Ext.Msg.alert("提示","成功下线" + result.successCount + "个分集!", function() {
								com.walker.xml.mediaProgram.programGridPnl.store.reload();
							});
						}else{
							Ext.Msg.alert("提示","操作失败!");
						}
					},
					failure : com.walker.xml.mediaProgram.doFail,
					params : {
						programIds : ids
					}
				});
			};
			Ext.Msg.confirm('提示',"确定下线所选媒资内容吗?",sumBtn);
		}
		else {
			Ext.Msg.alert("提示","未选择任何可下线记录!");
		}
	} else {
		Ext.Msg.alert("提示","请至少选择一条记录!");
	}
};

com.walker.xml.mediaProgram.downloadMovie = function() {
	var records = com.walker.xml.mediaProgram.programGridPnl.getSelectionModel().getSelections();
	if(records.length > 0)
	{
		var programDataJson = new Object({
			programObj : []
		});
		
		for(var i=0;i<records.length;i++){ 
			var program = new Object({
				PROGRAM_ID : "",
				ORIGINAL_ID : "",
				ORIGINAL_SERIES_ID : ""
			});
			program.PROGRAM_ID = records[i].data.PROGRAM_ID;
			program.ORIGINAL_ID = records[i].data.ORIGINAL_ID;
			program.ORIGINAL_SERIES_ID = records[i].data.ORIGINAL_SERIES_ID;
			programDataJson.programObj[i] = program;
		}
		
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			
			Ext.Ajax.request({
				url : '/walker/movie/downloadMovie',
				success : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.success){
						com.walker.xml.mediaProgram.programGridPnl.store.reload();
					}else{
						Ext.Msg.alert("提示","操作失败!");
					}
				},
				failure : com.walker.xml.mediaProgram.doFail,
				params : {
					programDataJson : JSON.stringify(programDataJson)
				}
			});
		};
		Ext.Msg.confirm('提示',"确定下载所选媒资视频文件吗?",sumBtn);
		
	} else {
		Ext.Msg.alert("提示","请至少选择一条记录!");
	}
};

com.walker.xml.mediaProgram.exportRecords = function() {
	var STATUS = com.walker.xml.mediaProgram.queryFormPnl.find("name","STATUS")[0].getValue();
	var CDN_TIME = com.walker.xml.mediaProgram.queryFormPnl.find("name","CDN_TIME")[0].getRawValue();
	var UNLINE_TIME = com.walker.xml.mediaProgram.queryFormPnl.find("name","UNLINE_TIME")[0].getRawValue();

	var sumBtn = function(btn){
		if(btn!='yes'){return;}
		
		var exportUrl = "/walker/movie/exportRecords?STATUS="+ STATUS +"&CDN_TIME=" + CDN_TIME + "&UNLINE_TIME=" + UNLINE_TIME;
		window.location.href = exportUrl;
	};
	Ext.Msg.confirm('提示',"确定导出媒资文件记录吗?",sumBtn);
}

com.walker.xml.mediaProgram.doSuccess = function(response) {
	var result = Ext.util.JSON.decode(response.responseText);
	if(result.success){
		Ext.Msg.alert("提示","操作成功!", function() {
			com.walker.xml.mediaProgram.programGridPnl.store.reload();
		});
	}else{
		Ext.Msg.alert("提示","操作失败!");
	}
};

com.walker.xml.mediaProgram.doFail = function(response) {
	var result = Ext.util.JSON.decode(response.responseText);
	Ext.Msg.alert("提示",result.err_msg);
};

//删除分集
com.walker.xml.mediaProgram.deleteProgram = function(){
	var record = com.walker.common.getSelectRecord(com.walker.xml.mediaProgram.programGridPnl, true);
	if(record){
		var status = record.get("STATUS");
		if(status === '01') {
			Ext.Msg.alert("提示", "分集已上线,无法删除!");
			return;
		}
		
		var sumBtn = function(btn){
			if(btn!='yes') {return;}
			var programId = record.data.PROGRAM_ID;
			var seriesId = record.data.SERIES_ID;
			
			var myLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:"数据处理中..."});
			myLoadMask.show();
			Ext.Ajax.request({
				url : '/walker/program/deleteProgram',
				success : function(response) {
					myLoadMask.hide();
					Ext.Msg.alert("提示","删除成功!", function() {
						com.walker.xml.mediaProgram.programGridPnl.store.reload();
					});
				},
				failure : function(response) {
					myLoadMask.hide();
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.errorMsg){
						Ext.Msg.alert("提示", result.errorMsg);
					}else{
						Ext.Msg.alert("提示","删除失败!");
					}
				},
				params : {
					programId : programId,
					seriesId : seriesId
				}
			});
		};
		Ext.Msg.confirm('提示',"确定<font color='red'>删除</font>所选分集吗?", sumBtn);
	}
};
//拆条
com.walker.xml.mediaProgram.splitProgram = function(){
	var record = com.walker.common.getSelectRecord(com.walker.xml.mediaProgram.programGridPnl, true);
	if(record){
		var sumBtn = function(btn){
			if(btn!='yes') {return;}
			var programId = record.data.PROGRAM_ID;
			
			var myLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:"数据处理中..."});
			myLoadMask.show();
			Ext.Ajax.request({
				url : '/walker/program/splitProgram',
				success : function(response) {
					myLoadMask.hide();
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.success){
						if (result.resultCode == '0') {
							Ext.Msg.alert("提示","拆条成功!", function() {
								com.walker.xml.mediaProgram.programGridPnl.store.reload();
							});
						}
						else if(result.resultCode == '9000') {
							Ext.Msg.alert("失败","错误的参数传递!");
						}
						else if(result.resultCode == '9001') {
							Ext.Msg.alert("失败","该媒资不存在或不完整,无法拆条!");
						}
						else if(result.resultCode == '9002') {
							Ext.Msg.alert("失败","该媒资影片类型不是多集,无法拆条!");
						}
						else if(result.resultCode == '9999') {
							Ext.Msg.alert("失败","未知错误!");
						}
					}else{
						Ext.Msg.alert("提示","操作失败!");
					}
				},
				failure : function(response) {
					myLoadMask.hide();
					var result = Ext.util.JSON.decode(response.responseText);
					if(result.errorMsg){
						Ext.Msg.alert("提示", result.errorMsg);
					}else{
						Ext.Msg.alert("提示","操作失败!");
					}
				},
				params : {
					programId : programId
				}
			});
		};
		Ext.Msg.confirm('提示',"确定对该分集进行拆条吗?", sumBtn);
	}
};

com.walker.xml.mediaProgram.modifyProgram = function(){
	var rec = com.walker.common.getSelectRecord(com.walker.xml.mediaProgram.programGridPnl, true);
	if(rec){
		var STATUS = rec.get("STATUS");
		var PROGRAM_ID = rec.get("PROGRAM_ID");
		
		var editForm = new Ext.FormPanel( {
			region : 'center',
			layout : 'form',
			labelWidth : 80,
			labelAlign : 'right',
			autoHeight: false,
			bodyStyle: 'padding-top: 10px;',
			border : false,
			items: [{
				layout : "column",
				border : false,
				items : [ {
					layout : "form",
					columnWidth : 0.5,
					border : false,
					items : [ {
						xtype : 'textfield',
						fieldLabel: '分集名称',
						id: 'NAME',
						name: 'NAME',
					    maxLength : 100,
						allowBlank: false,
						width : 200
			        } ]
				}, {
					layout : "form",
					border : false,
					columnWidth : 0.5,
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
						width : 200
			        } ]
				} ]
			}, {
				layout : "column",
				border : false,
				items : [ {
					layout : "form",
					border : false,
					columnWidth : 0.5,
					items : [ {
						xtype : 'numberfield',
			            fieldLabel: '时长(秒)',
			            id: 'DURATION',
			            name: 'DURATION',
					    minValue : 1,
					    maxLength : 5,
			            allowBlank: false,
					    allowNegative: false,
					    allowDecimals: false,
						width : 200
			        } ]
				}, {
					layout : "form",
					border : false,
					columnWidth : 0.5,
					items : [ {
			            xtype : "datefield",
			            fieldLabel: '更新日期',
				        id : 'UPDATE_TIME',
				        name : 'UPDATE_TIME',
				        format : 'Y-m-d',
						width : 200
			        } ]
				} ]
			}, {
				layout : "column",
				border : false,
				items : [ {
					layout : "form",
					border : false,
					columnWidth : 0.5,
					items : [ {
						xtype : 'textfield',
			            fieldLabel: '导演',
			            id: 'DIRECTOR',
			            name: 'DIRECTOR',
					    maxLength : 100,
						width : 200
			        } ]
				}, {
					layout : "form",
					border : false,
					columnWidth : 0.5,
					items : [ {
						xtype : 'textfield',
			            fieldLabel: '编剧',
			            id: 'ADAPTOR',
			            name: 'ADAPTOR',
					    maxLength : 100,
						width : 200
			        } ]
				} ]
			}, {
				layout : "column",
				border : false,
				items : [ {
					layout : "form",
					border : false,
					items : [ {
						xtype : 'textfield',
			            fieldLabel: '演员',
			            id: 'ACTOR',
			            name: 'ACTOR',
					    maxLength : 200,
						width : 542
			        } ]
				} ]
			}, {
				layout : "column",
				border : false,
				items : [ {
					layout : "form",
					border : false,
					items : [ {
			            xtype : "textfield",
			            fieldLabel: '关键字',
				        id : 'KEYWORDS',
				        name : 'KEYWORDS',
					    maxLength : 200,
						width : 542
			        } ]
				} ]
			}, {
				layout : "column",
				border : false,
				items : [ {
					layout : "form",
					border : false,
					items : [ {
			            xtype : "textfield",
			            fieldLabel: '看点',
			            id: 'AWARDS',
			            name: 'AWARDS',
					    maxLength : 200,
						width : 542
			        } ]
				} ]
			}, {
				layout : "column",
				border : false,
				items : [ {
					layout : "form",
					border : false,
					items : [ {
			            xtype : "textarea",
			            fieldLabel: '描述',
			            id: 'DESCRIPTION',
			            name: 'DESCRIPTION',
					    maxLength : 500,
						width : 542,
			            height: 80
			        } ]
				} ]
			} ],
			tbar : [ {
				text : '保存',
				iconCls : 'btnIconSave',
				handler : function() {
					if (editForm.getForm().isValid()) {
						var submitForm = function () {
							editForm.getEl().mask("数据提交中...","x-mask-loading");
							editForm.getForm().submit({
								url : '/walker/program/updProgram',
								method : "post",
								success : function(response) {
									editForm.getEl().unmask();
									Ext.Msg.alert("提示","保存成功!");
										com.walker.xml.mediaProgram.programGridPnl.getStore().reload({
										callback : function(records){
											for ( var i = 0; i < records.length; i++) {
												if (records[i].get("PROGRAM_ID") == PROGRAM_ID) {
													com.walker.xml.mediaProgram.programGridPnl.getSelectionModel().selectRow(i);
												}
											}
										}
									});
								},
								failure : function(response) {
									editForm.getEl().unmask();
									Ext.Msg.alert("提示","保存发生异常!");
								},
								params : {
									PROGRAM_ID : rec.get("PROGRAM_ID")
								}
							});
						};
						var confirmTitle = '提示';
						var confirmContent = '确定保存修改?';
						var confirmIcon = Ext.MessageBox.QUESTION;
						if (STATUS == '01') {
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
				}
			}, '-', {
				text : '取消',
				iconCls : 'btnIconReset',
				handler : function() {
					Ext.WindowMgr.getActive().close();
				}
			} ]
		});
		
		new Ext.Window( {
			title : "编辑分集内容",
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
			items : [ editForm ],
			listeners : {
				show : function() {
					editForm.getEl().mask("数据加载中...","x-mask-loading");
					Ext.Ajax.request({
						url : '/walker/program/getProgramById',
						success : function(response) {
							editForm.getEl().unmask();
							var result = Ext.util.JSON.decode(response.responseText);
							STATUS = result.STATUS;
							for ( var o in result) {
								var field = editForm.getForm().findField(o);
								if (field) {
									if (result[o] != null && result[o] != 'null') {
										field.setValue(result[o]);
									}
								}
							}
						},
						failure : function(response) {
							editForm.getEl().unmask();
							var result = Ext.util.JSON.decode(response.responseText);
							Ext.Msg.alert("提示","查询发生异常!\n" + result.errorMsg);
						},
						params : {
							programId : rec.get("PROGRAM_ID")
						}
					});
				}
			}
		}).show();
	}
};

com.walker.xml.mediaProgram.bindingSeries = function() {
	var records = com.walker.xml.mediaProgram.programGridPnl.getSelectionModel().getSelections();
	if(records.length > 0) {
		var PROGRAM_IDs = [];
		for ( var i = 0; i < records.length; i++) {
			PROGRAM_IDs.push(records[i].get("PROGRAM_ID"));
		}
		
		com.walker.xml.mediaProgram.bindQueryPnl = new Ext.FormPanel( {
			layout : 'form',
			region : 'north',
			height : 70,
			labelWidth : 70,
			bodyStyle: 'padding-top: 10px;',
			margins : '0 0 4 0',
			border: false,
			style: 'border-bottom-width: 1px;',
			items : [ {
				layout : "column",
				border : false,
				labelAlign : 'right',
				items : [ {
					columnWidth : .3,
					layout : "form",
					border : false,
					items : [ {
						xtype : "textfield",
						fieldLabel : "合集ID",
						anchor : '90%',
						name : 'ID'
					} ]
				}, {
					columnWidth : .3,
					layout : "form",
					border : false,
					items : [ {
						xtype : "textfield",
						fieldLabel : "合集名称",
						anchor : '90%',
						name : 'NAME'
					} ]
				}, {
					columnWidth : .3,
					layout : "form",
					border : false,
					items : [ {
						xtype : 'datefield',
						fieldLabel : '注入日期',
						name : 'CDN_TIME',
						anchor : '90%',
						editable : false,
						format : 'Y-m-d'
					} ]
				} ]
			} ],
			tbar : [ {
				text : '绑定',
				iconCls : 'btnIconSave',
				handler : function() {
					var selReds = com.walker.common.getSelectRecord(com.walker.xml.mediaProgram.bindGrid, true);
					if (selReds) {
						var sumBtn = function(btn) {
							if (btn != 'yes') {
								return;
							}
							
							Ext.WindowMgr.getActive().getEl().mask("数据处理中...","x-mask-loading");
							
							Ext.Ajax.request( {
								url : '/walker/program/bindProgramToSeries',
								success : function(response) {
									Ext.WindowMgr.getActive().getEl().unmask();
									var result = Ext.decode(response.responseText);
									if (result.success) {
										Ext.Msg.alert("提示", "操作成功!", function() {
											Ext.WindowMgr.getActive().close();
											com.walker.xml.mediaProgram.programGridPnl.store.reload();
										});
									}
								},
								failure : function(response) {
									Ext.WindowMgr.getActive().getEl().unmask();
									Ext.Msg.alert("提示", "操作失败!");
								},
								params : {
									programIds : PROGRAM_IDs,
									seriesId : selReds.data.SERIES_ID
								}
							});
						};
						Ext.Msg.confirm('提示', "确定绑定?", sumBtn);
					}
				}
			}, '-', {
				text : '取消',
				iconCls : 'btnIconReset',
				handler : function() {
					Ext.WindowMgr.getActive().close();
				}
			}, '->', {
				text : '查询',
				iconCls : 'btnIconSearch',
				handler : function() {
					com.walker.xml.mediaProgram.bindGrid.store.baseParams.NAME = com.walker.xml.mediaProgram.bindQueryPnl.find("name", "NAME")[0].getValue();
					com.walker.xml.mediaProgram.bindGrid.store.baseParams.ID = com.walker.xml.mediaProgram.bindQueryPnl.find("name", "ID")[0].getValue();
					com.walker.xml.mediaProgram.bindGrid.store.baseParams.CDN_TIME = com.walker.xml.mediaProgram.bindQueryPnl.find("name", "CDN_TIME")[0].getRawValue();
					com.walker.xml.mediaProgram.bindGrid.store.load();
				}
			}, {
				text : '重置',
				iconCls : 'btnIconRecover',
				handler : function() {
					com.walker.xml.mediaProgram.bindQueryPnl.find("name", "NAME")[0].setValue('');
					com.walker.xml.mediaProgram.bindQueryPnl.find("name", "ID")[0].setValue('');
					com.walker.xml.mediaProgram.bindQueryPnl.find("name", "CDN_TIME")[0].setValue('');
				}
			} ]
		});
		
		new Ext.Window( {
			autoScroll : true,
			layout : 'border',
			width : 1000,
			closeAction : 'close',
			maximizable : true,
			modal : true,
			constrain : true,
			height : 510,
			title : "绑定集合",
			items : [ com.walker.xml.mediaProgram.bindQueryPnl, com.walker.xml.mediaProgram.bindingInitGrid() ]
		}).show();
	}
	else {
		Ext.Msg.alert("提示","未选择任何记录!");
	}
};

com.walker.xml.mediaProgram.bindingInitGrid = function() {
	var reader = new Ext.data.JsonReader( {
		fields : [ "SERIES_ID", "NAME", "TYPE", "TYPE_NAME",
				"RELEASE_TIME", "AREA", "DIRECTOR", "PROGRAM_COUNT", "CDN_TIME", "STATUS" ],
		root : 'rows',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store( {
		url : '/walker/series/listPage',
		reader : reader,
		baseParams : {
			limit : 15,
			start : 0
		}
	});
	store.load();

	var sm = new Ext.grid.CheckboxSelectionModel();
	var colModel = new Ext.grid.ColumnModel( {
		columns : [ sm, new Ext.grid.RowNumberer( {
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
			header : "合集ID",
			width : 100,
			align : 'center',
			hidden : true,
			sortable : false,
			dataIndex : 'SERIES_ID'
		}, {
			header : "合集名称",
			width : 200,
			hidden : false,
			sortable : false,
			dataIndex : 'NAME',
			align : 'left'
		}, {
			header : "内容分类",
			width : 100,
			hidden : false,
			sortable : false,
			dataIndex : 'TYPE_NAME',
			align : 'left'
		}, {
			header : "总集数",
			width : 80,
			align : 'center',
			hidden : false,
			sortable : false,
			dataIndex : 'PROGRAM_COUNT',
			align : 'right'
		}, {
			header : "导演",
			width : 100,
			hidden : true,
			sortable : false,
			dataIndex : 'DIRECTOR',
			align : 'left'
		}, {
			header : "上映地区",
			width : 100,
			hidden : false,
			sortable : false,
			dataIndex : 'AREA',
			align : 'left'
		}, {
			header : "上映日期",
			width : 100,
			align : 'center',
			hidden : false,
			sortable : false,
			dataIndex : 'RELEASE_TIME'
		}, {
			header : "注入时间",
			width : 140,
			align : 'center',
			hidden : false,
			sortable : false,
			dataIndex : 'CDN_TIME'
		} ]
	});
	var grid = new Ext.grid.GridPanel( {
		title: '合集列表',
		region : 'center',
		store : store,
		sm : sm,
		loadMask : true, // 加载数据时为元素做出类似于遮罩的效果
		colModel : colModel,
		border: false,
		style: 'border-top-width: 1px;',
		bbar : new Ext.PagingToolbar( {
			store : store,
			displayInfo : true,
			pageSize : 15
		})
	});
	com.walker.xml.mediaProgram.bindGrid = grid;
	return grid;
};

Ext.onReady(function() {
	new Ext.Viewport({
		layout : 'border',
		items : [ 
		    com.walker.xml.mediaProgram.getQueryForm(),
			com.walker.xml.mediaProgram.getGridPnl()
		]
	});
	
});