Ext.namespace("com.walker.roleManage");

com.walker.roleManage.queryFormPnl = null;
com.walker.roleManage.roleGridPnl = null;
com.walker.roleManage.roleFormPnl = null;

com.walker.roleManage.getQueryForm = function(){
	
	var queryForm = new Ext.Panel({
		id : 'roleSearchPanel',
		layout : 'column',
		region : 'north',
		rowHeight : 0,
		frame:true,
		buttonAlign : 'left',
		autoHeight:false,
		height:80,
		tbar:[{
				text : '新增',
				iconCls : 'btnIconSave',
				id : "1000201",
				hidden : true,
				handler : function() {
					com.walker.roleManage.editRole("add");
				}
			},{
				text : '修改',
				iconCls : 'btnIconUpdate',
				id : "1000202",
				hidden : true,
				handler : function() {
					com.walker.roleManage.editRole("edit");
				}
			},{
				text : '删除',
				iconCls : 'btnIconDel',
				id : "1000203",
				hidden : true,
				handler : function() {
					com.walker.roleManage.deleteRole();
				}
			}],
		bodyStyle : 'padding:5px 0px 27px 0px',
		items : [
				{
					layout : 'form',
					autoHeight : true,
					border : false,
					width : 250,
					labelWidth : 80,
					labelAlign : 'right',
					items : [{
								xtype : 'textfield',
								fieldLabel : '角色名称',
								name : 'roleName',
								id:'roleNameInput',
								anchor : '100%'
							}]
				},{
					layout : 'form',
					autoHeight : true,
					border : false,
					width : 250,
					labelWidth : 80,
					labelAlign : 'right',
					items : [{
								xtype : 'textfield',
								fieldLabel : '角色ID',
								name : 'roleId',
								id:'roleIdInput',
								anchor : '100%'
							}]
				},{
					xtype : 'button',
					style : 'marginLeft:10px',
					iconCls : 'btnIconSearch',
					text : '查	询',
					handler : function() {
						com.walker.roleManage.queryRole();
					}
				}]
	});
	
	com.walker.roleManage.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.roleManage.getGridPnl = function(){
	
	var reader = new Ext.data.JsonReader({
		fields : ["ID","CODE","NAME","SYS_FLAG","DESCRIPTION","STATE"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/sync/user/getRoleListPage',
		reader : reader,
		baseParams : {
			limit : 20,
			start: 0
		},
		remoteSort : true
	});
	
	// 创建列模型的多选框模型.
    var sm = new Ext.grid.CheckboxSelectionModel();	
	var model = new Ext.grid.ColumnModel([sm,{
				header : "角色ID",
				dataIndex : "ID",
				sortable : true,
				width : 220,
				align : 'left'
			},{
				header : "角色编码",
				dataIndex : "CODE",
				sortable : true,
				width : 220,
				align : 'left'
			}, {
				header : "角色名称",
				dataIndex : "NAME",
				sortable : true,
				width : 150,
				align : 'left'
			}, {
				header : "是否系统内置",
				dataIndex : "SYS_FLAG",
				sortable : true,
				width : 100,
				align : 'left',
				renderer : function(value){
					var disValue = '';
					if(value == '1'){
						disValue = '是';
					}else{
						disValue = '否';
					}
					return disValue;
				}
			}, {
				header : "状态",
				dataIndex : "STATE",
				align : 'left',
				renderer : function(value){
					var disValue = '';
					if(value == '1'){
						disValue = '启用';
					}else{
						disValue = '停用';
					}
					return disValue;
				}
			}, {
				header : "角色描述",
				dataIndex : "DESCRIPTION",
				sortable : true,
				width : 220,
				align : 'left'
			}, {
				header : "权限分配",
				dataIndex : "",
		        renderer: function (value, meta, record) {
		             var formatStr = "<image class='imgBtn' src='" + BASE_PATH + "resources/images/button/report_disk.gif' style='cursor:pointer' onclick='javascript:return false;'></image>"; 
		             var resultStr = String.format(formatStr);
		             return "<div class='controlBtn'>" + resultStr + "</div>";
		        }.createDelegate(this),
		        align : 'left'
			}
	]);
	
	var grid = new Ext.grid.GridPanel({
			layout : 'fit',
			title : '角色列表',
			autoWidth : true,
			autoScroll : true,
			cm : model,
			sm : sm,
			store : store,
			stripeRows : true,
			region : 'center',
			bbar : new Ext.PagingToolbar({
						pageSize : 20,
						store : store,
						displayInfo : true,
						region : 'center',
						displayMsg : '显示数目 {0} - {1} of {2}',
						emptyMsg : '没有发现记录'
					})
	});
	
	grid.on('cellclick', function (grid, rowIndex, columnIndex, e) {
		var btn = e.getTarget('.imgBtn');
		if (btn) {
            record = grid.getStore().getAt(rowIndex);
            var roleId = record.data.ID;
            com.walker.permission.showRolePermissionTree(roleId);
		}
	});
	
	com.walker.roleManage.roleGridPnl = grid;
	grid.store.load();
	return grid;
};

com.walker.roleManage.queryRole = function() {
	com.walker.roleManage.roleGridPnl.store.baseParams.roleName = Ext.getCmp("roleNameInput").getValue();
	com.walker.roleManage.roleGridPnl.store.baseParams.roleId = Ext.getCmp("roleIdInput").getValue();
	com.walker.roleManage.roleGridPnl.store.load();
};

com.walker.roleManage.deleteRole = function() {
	var selReds = com.walker.common.getSelectRecord(com.walker.roleManage.roleGridPnl,false);
	if(selReds){
		var ids = "";
		for (var i = 0; i < selReds.length; i++) {
			ids += "'"+selReds[i].get("ID")+"',";
		}
		
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			Ext.Ajax.request({
				url: '/sync/user/deleteRole',
				params: {
					ids : ids.substring(0,ids.lastIndexOf(','))
				},
				success: function(response) {
					var result = Ext.decode(response.responseText);
					if(result.success){
						Ext.Msg.alert("提示","删除成功！");
						com.walker.roleManage.roleGridPnl.store.baseParams.roleName = Ext.getCmp("roleNameInput").getValue();
						com.walker.roleManage.roleGridPnl.store.baseParams.roleId = Ext.getCmp("roleIdInput").getValue();
						com.walker.roleManage.roleGridPnl.store.load();
					}else
					{
						Ext.Msg.alert("提示","删除失败！");
					}
				},
				failure: function(response) {
					Ext.Msg.alert("提示","删除失败！");
				}
			});
		};
		Ext.Msg.confirm('系统提示',"确定删除吗？",sumBtn);
	}
};

com.walker.roleManage.editRole = function(type) {
	
	if(type == "edit")
	{
		var records = com.walker.roleManage.roleGridPnl.getSelectionModel().getSelections();
		if(records.length == 1)
		{
			var role = records[0].data;
			com.walker.roleManage.createRoleFormPnl(role);
		}else{
			Ext.Msg.alert("提示","请选择一条记录！");
		}
	}else{
		com.walker.roleManage.createRoleFormPnl(null);
	}
};


com.walker.roleManage.createRoleFormPnl = function(role) {
	var roleFormPnl = new Ext.form.FormPanel({
	    width : 400,
	    height : 300,
	    frame : true,
	    layout : "form",
	    labelWidth : 80,
	    labelAlign : "right",
	    items : [{//行开始
		    layout : "form", // 从上往下的布局
		    items : [{
		       xtype : "textfield",
		       name : "ROLE_NAME",
		       fieldLabel : "角色名称",
		       allowBlank : false,
		       blankText : '请输入角色名称',
		       anchor : '80%'
		      }]
		}//行结束
	    ,{//行开始
	          layout : "form",
	          items : [{
        	    xtype : 'combo',
				fieldLabel : '启用状态',
				name : 'STATE',
	            anchor : '80%',
	            triggerAction: 'all',
	            displayField : 'text', 
	            valueField : 'value', 
	            store: new Ext.data.SimpleStore({
	                fields :['text','value'],
	                data : [['请选择...',''],['停用','0'],['启用','1']]
	            }),
	            mode: 'local',
	            emptyText:'请选择...',
	            blankText : '请选择',
	            editable:false,
	            allowBlank:false
	          }]
    	}//行结束
	    ,{//行开始
	          layout : "form",
	          items : [{
			      xtype : 'textarea',
				  name : 'DESCRIPTION',
				  fieldLabel : '角色描述',
				  maxLength:256,
				  maxLengthText:'字段长度超长',
				  anchor : '80%'
	          }]
    	}//行结束
	    ,{//行开始
	          layout : "form",
	          items : [{
			      xtype : 'textfield',
				  name : 'ID',
				  fieldLabel : '角色ID',
				  hidden: true,
				  hideLabel:true,
				  anchor : '80%'
	          }]
	    }//行结束
	    ]
	});
	
	com.walker.roleManage.roleFormPnl = roleFormPnl;
	
	var window = new Ext.Window({  
        layout : 'fit',//设置window里面的布局  
        width:400,
        height:300,
        title:'新增角色',
        //关闭时执行隐藏命令,如果是close就不能再show出来了  
        closeAction:'hide',  
        //draggable : false, //不允许窗口拖放  
        //maximizable : true,//最大化  
        //minimizable : true,//最小话  
        constrain : true,//防止窗口超出浏览器  
        //constrainHeader : true,//只保证窗口顶部不超出浏览器  
        //resizble : true,//是否可以改变大小  
        //resizHandles : true,//设置窗口拖放的方式  
        modal : true,//屏蔽其他组件,自动生成一个半透明的div  
        //animateTarget : 'target',//弹出和缩回的效果  
        //plain : true,//对窗口进行美化,可以看到整体的边框  
          
        buttonAlign : 'center',//按钮的对齐方式  
        defaultButton : 0,//默认选择哪个按钮 
        items : [roleFormPnl],
        buttons : [{  
            text : '保存',  
            handler : function(){  
            	var errorInfo = com.walker.common.validateForm(com.walker.roleManage.roleFormPnl);
            	if(!errorInfo){
            		//创建保存对象
            		var roleDataObj = new Object({
            			ID : "",
            			NAME : "",
            			STATE : "",
            			DESCRIPTION : ""
            		});
            		
            		roleDataObj.ID = com.walker.roleManage.roleFormPnl.find("name","ID")[0].getValue();
            		roleDataObj.NAME = com.walker.roleManage.roleFormPnl.find("name","ROLE_NAME")[0].getValue().trim();
            		roleDataObj.STATE = com.walker.roleManage.roleFormPnl.find("name","STATE")[0].getValue();
            		roleDataObj.DESCRIPTION = com.walker.roleManage.roleFormPnl.find("name","DESCRIPTION")[0].getValue().trim();
            		
            		//保存请求后台
    				Ext.Ajax.request({
    				   url: '/sync/user/saveRole',
    				   success: function(response) {
    					   var result = Ext.util.JSON.decode(response.responseText);
    						if(result.success == 'success'){
    							Ext.Msg.alert("提示","保存成功");
    						}
    						com.walker.roleManage.queryRole();
    						window.hide();
    				   },
    				   failure: function(response) {
    						Ext.Msg.alert("提示","保存失败");
    				   },
    				   params: { 
    					   roleDataJson : JSON.stringify(roleDataObj)
    				   }
    				});
            	}else{
            		Ext.Msg.alert("提示",errorInfo);
            	}
            }  
        },{  
            text : '取消',
            handler : function(){  
                window.hide();  
            }
        }]  
    });  
	
	if(role != null)
	{
		com.walker.roleManage.roleFormPnl.find("name","ID")[0].setValue(role.ID);
		com.walker.roleManage.roleFormPnl.find("name","ROLE_NAME")[0].setValue(role.NAME);
		com.walker.roleManage.roleFormPnl.find("name","STATE")[0].setValue(role.STATE);
		com.walker.roleManage.roleFormPnl.find("name","DESCRIPTION")[0].setValue(role.DESCRIPTION);
	}
	
	window.show();
	new Ext.Viewport({
		items : [window]
	});
};


Ext.onReady(function() {
	
	new Ext.Viewport({
		renderTo : 'roleQuery',
		id : 'row-panel',
		bodyStyle : 'padding:5px',
		layout : 'fit',
		title : 'Row Layout',
		items : [{
			rowHeight : 0,
			xtype : 'panel',
			layout : 'border',
			items : [com.walker.roleManage.getQueryForm(),com.walker.roleManage.getGridPnl()]
		}]
	});
	
	com.walker.permission.userpermission("10002");
});

