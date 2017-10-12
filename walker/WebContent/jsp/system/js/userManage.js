Ext.namespace("com.walker.userManage");

com.walker.userManage.queryFormPnl = null;
com.walker.userManage.userGridPnl = null;
com.walker.userManage.userFormPnl = null;

com.walker.userManage.getQueryForm = function(){
	
	var queryForm = new Ext.Panel({
		id : 'userSearchPanel',
		layout : 'column',
		region : 'north',
		rowHeight : 0,
		frame:true,
		buttonAlign : 'left',
		autoHeight:false,
		height:80,
		bodyStyle : 'padding:8px 0px 5px 0px',
		tbar:[{
			text : '新增',
			iconCls : 'btnIconSave',
			id : '1000101',
			hidden : true,
			handler : function() {
				com.walker.userManage.editUser("add");
			}
		},{
			text : '修改',
			iconCls : 'btnIconUpdate',
			id : '1000102',
			hidden : true,
			handler : function() {
				com.walker.userManage.editUser("edit");
			}
		},{
			// 删除
			text : '删除',
			iconCls : 'btnIconDel',
			id : '1000103',
			hidden : true,
			handler : function() {
				com.walker.userManage.daleteUser();
			}
		},{
			text : '修改密码',
			iconCls : 'btnIconUpdate',
			id : '1000104',
			hidden : true,
			handler : function() {
				com.walker.userManage.editUserPassword();
			}
		},'->',{
			xtype : 'button',
			style : 'marginLeft:10px',
			iconCls : 'btnIconSearch',
			text : '查询',
			handler : function() {
				com.walker.userManage.queryUser();
			}
		},{
			xtype : 'button',
			style : 'marginLeft:10px',
			iconCls : 'btnIconRecover',
			text : '重置',
			handler : function() {
				Ext.getCmp("userNameInput").setValue();
				Ext.getCmp("userIdInput").setValue();
			}
		}],
		items : [{
			layout : 'form',
			autoHeight : true,
			border : false,
			width : 250,
			labelWidth : 80,
			labelAlign : 'right',
			items : [{
						xtype : 'textfield',
						fieldLabel : '用户ID',
						name : 'userId',
						id:'userIdInput',
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
						fieldLabel : '用户名称',
						name : 'userName',
						id:'userNameInput',
						anchor : '100%'
					}]
		}]
	});
	
	com.walker.userManage.queryFormPnl = queryForm;
	return queryForm;
};

com.walker.userManage.getGridPnl = function(){

	var reader = new Ext.data.JsonReader({
		fields : ["ID","LOGIN_NAME","USER_NAME","PASSWORD","SP_ID","SP_NAME","ROLE_ID","ROLE_NAME","DELETE_FLAG","RU_ID"],
		root : 'rows',
		id : 'ID',
		totalProperty : 'total'
	});
	var store = new Ext.data.Store({
		url : '/walker/user/getUserListPage',
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
				header : "用户ID",
				dataIndex : "ID",
				sortable : true,
				width : 250,
				align : 'center'
			}, {
				header : "登录名称",
				dataIndex : "LOGIN_NAME",
				sortable : true,
				width : 150,
				align : 'left'
			}, {
				header : "用户名称",
				dataIndex : "USER_NAME",
				sortable : true,
				width : 150,
				align : 'left'
			}, {
				header : "运营商ID",
				dataIndex : "SP_ID",
				sortable : true,
				width : 100,
				hidden : true,
				align : 'center'
			}, {
				header : "运营商名称",
				dataIndex : "SP_NAME",
				sortable : true,
				width : 250,
				hidden : true,
				align : 'left'
			}, {
				header : "角色ID",
				dataIndex : "ROLE_ID",
				sortable : true,
				hidden : true,
				width : 200,
				align : 'center'
			}, {
				header : "角色名称",
				dataIndex : "ROLE_NAME",
				sortable : true,
				width : 150,
				align : 'left'
			}, {
				hidden : true,
				header : "RU_ID",
				dataIndex : "RU_ID",
				sortable : true,
				width : 150,
				align : 'center'
			}, {
				header : "状态",
				dataIndex : "DELETE_FLAG",
				align : 'center',
				renderer : function(value){
					var disValue = '';
					if(value == '1'){
						disValue = '启用';
					}else{
						disValue = '停用';
					}
					return disValue;
				}
			}
	]);
	
	var grid = new Ext.grid.GridPanel({
			layout : 'fit',
			title : '用户列表',
			autoWidth : true,
			autoScroll : true,
			cm : model,
			sm : sm,
			store : store,
			stripeRows : true,
			region : 'center',
			bodyStyle : 'padding:5px 5px',
			bbar : new Ext.PagingToolbar({
						pageSize : 20,
						store : store,
						displayInfo : true,
						region : 'center'
					})
	});
	
	com.walker.userManage.userGridPnl = grid;
	grid.store.load();
	return grid;
};

com.walker.userManage.queryUser = function() {
	com.walker.userManage.userGridPnl.store.baseParams.userName = Ext.getCmp("userNameInput").getValue();
	com.walker.userManage.userGridPnl.store.baseParams.userId = Ext.getCmp("userIdInput").getValue();
	com.walker.userManage.userGridPnl.store.load();
};

com.walker.userManage.title=null;
com.walker.userManage.hiddenFlag=false;
com.walker.userManage.creayteType=false;
com.walker.userManage.editUser = function(type) {
	com.walker.userManage.creayteType=type;
	if(type == "edit")
	{
		var records = com.walker.userManage.userGridPnl.getSelectionModel().getSelections();
		if(records.length == 1)
		{
			var user = records[0].data;
			com.walker.userManage.title="修改用户";
			com.walker.userManage.hiddenFlag=true;
			com.walker.userManage.createUserFormPnl(user);
		}else{
			Ext.Msg.alert("提示","请选择一条记录！");
		}
	}else{
		com.walker.userManage.title="新增用户";
		com.walker.userManage.hiddenFlag=false;
		com.walker.userManage.createUserFormPnl(null);
	}
};

com.walker.userManage.createUserFormPnl = function(user) {
	var userFormPnl = new Ext.form.FormPanel({
	    width : 350,
	    height : 240,
	    frame : true,
	    layout : "form",
	    labelWidth : 80,
	    labelAlign : "right",
	    items : [{
		    layout : "form", // 从上往下的布局
		    items : [{
		       xtype : "textfield",
		       name : "USER_NAME",
		       fieldLabel : "用户名称",
		       allowBlank : false,
		       anchor : '80%'
		      }]
	    }//行结束
	    ,{//行开始
		    layout : "form", // 从上往下的布局
		    items : [{
		       xtype : "textfield",
		       name : "LOGIN_NAME",
		       fieldLabel : "登录名称",
		       allowBlank : false,
		       anchor : '80%'
		      }]
	    }//行结束
	    ,{//行开始
	          layout : "form",
	          items : [{
	        	  xtype : "textfield",
	        	  inputType: 'password',
			      name : "PASSWORD",
			      fieldLabel : "密码",
			      allowBlank : false,
			      hidden : com.walker.userManage.hiddenFlag,
			      anchor : '80%'
	          }]
    	}//行结束
	    ,{//行开始
	          layout : "form",
	          items : [{
	        	  xtype : "textfield",
	        	  inputType: 'password',
			      name : "NPASSWORD",
			      fieldLabel : "确认密码",
			      allowBlank : true,
			      hidden : com.walker.userManage.hiddenFlag,
			      anchor : '80%'
	          }]
    	}//行结束
	    ,{//行开始
	          layout : "form",
	          items : [{
	        	xtype : 'combo',
				fieldLabel : '启用状态',
				name : 'DELETE_FLAG',
	            anchor : '80%',
	            triggerAction: 'all',
	            displayField : 'text', 
	            valueField : 'value', 
	            store: new Ext.data.SimpleStore({
	                fields :['text','value'],
	                data : [['请选择...',''],['启用','1'],['停用','0']]
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
	        	xtype : 'combo',
				fieldLabel : '所属角色',
				name : 'ROLE_ID',
	            anchor : '80%',
	            triggerAction: 'all',
	            displayField : 'TEXT', 
	            valueField : 'ID', 
	            store: com.walker.userManage.roleStore,
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
			      xtype : 'textfield',
				  name : 'ID',
				  fieldLabel : '用户ID',
				  hidden: true,
				  hideLabel:true,
				  anchor : '80%'
	          }]
	    }//行结束
	    ,{//行开始
	          layout : "form",
	          items : [{
			      xtype : 'textfield',
				  name : 'RU_ID',
				  fieldLabel : '用户角色ID',
				  hidden: true,
				  hideLabel:true,
				  anchor : '80%'
	          }]
	    }//行结束
	    ]
	});
	
	com.walker.userManage.userFormPnl = userFormPnl;
	
	var window = new Ext.Window({  
        layout : 'fit',
        width:350,
        height:240,
        title:com.walker.userManage.title,
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
        items : [userFormPnl],
        buttons : [{  
            text : '保存',  
            handler : function(){ 
            	
            	var errorInfo = com.walker.common.validateForm(com.walker.userManage.userFormPnl);
            	if(!errorInfo){
            		
            		//创建保存对象
            		var userDataObj = new Object({
            			ID : "",
            			USER_NAME : "",
            			PASSWORD : "",
            			DELETE_FLAG : "",
            			ROLE_ID : "",
            			RU_ID : ""
            		});
            		
            		userDataObj.ID = com.walker.userManage.userFormPnl.find("name","ID")[0].getValue();
            		userDataObj.LOGIN_NAME = com.walker.userManage.userFormPnl.find("name","LOGIN_NAME")[0].getValue().trim();
            		userDataObj.USER_NAME = com.walker.userManage.userFormPnl.find("name","USER_NAME")[0].getValue().trim();
            		
            		if(com.walker.userManage.creayteType='add'){
            			var password=com.walker.userManage.userFormPnl.find("name","PASSWORD")[0].getValue();
                		var npassword=com.walker.userManage.userFormPnl.find("name","NPASSWORD")[0].getValue();
                		
                		if(password!=npassword){
                			Ext.Msg.alert("提示","两次输入密码不一致！");
                			return false;
                		}
            			userDataObj.PASSWORD = Ext.util.MD5(password);
            		}
            		
            		userDataObj.DELETE_FLAG = com.walker.userManage.userFormPnl.find("name","DELETE_FLAG")[0].getValue();
            		userDataObj.ROLE_ID = com.walker.userManage.userFormPnl.find("name","ROLE_ID")[0].getValue();
            		userDataObj.RU_ID = com.walker.userManage.userFormPnl.find("name","RU_ID")[0].getValue();
            		
            		//保存请求后台
    				Ext.Ajax.request({
    				   url: '/walker/user/saveUser',
    				   success: function(response) {
    					   var result = Ext.util.JSON.decode(response.responseText);
    						if(result.success){
    							Ext.Msg.alert("提示","保存成功");
    						}else{
    							Ext.Msg.alert("提示",result.err_msg);
    						}
    						com.walker.userManage.queryUser();
    						window.hide();
    				   },
    				   failure: function(response) {
    						Ext.Msg.alert("提示","保存失败");
    				   },
    				   params: { 
    					   userDataJson : Ext.util.JSON.encode(userDataObj)
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
	
	if(user != null)
	{
		com.walker.userManage.userFormPnl.find("name","ID")[0].setValue(user.ID);
		com.walker.userManage.userFormPnl.find("name","LOGIN_NAME")[0].setValue(user.LOGIN_NAME);
		com.walker.userManage.userFormPnl.find("name","USER_NAME")[0].setValue(user.USER_NAME);
		com.walker.userManage.userFormPnl.find("name","DELETE_FLAG")[0].setValue(user.DELETE_FLAG);
		com.walker.userManage.userFormPnl.find("name","ROLE_ID")[0].setValue(user.ROLE_ID);
		com.walker.userManage.userFormPnl.find("name","RU_ID")[0].setValue(user.RU_ID);
	}
	
	window.show();
};
	

Ext.onReady(function() {
	
	Ext.QuickTips.init();
	
	//角色下拉数据
	com.walker.userManage.roleStore = new Ext.data.Store({
			url : '/walker/user/getRoleStore',
			reader : new Ext.data.JsonReader({
						fields : ["TEXT","ID"],
						root : 'rows'
					}),
			remoteSort : true
	});
	
	com.walker.userManage.roleStore.load();
		
	new Ext.Viewport({
		id : 'row-panel',
		bodyStyle : 'padding:5px',
		layout : 'fit',
		title : 'Row Layout',
		items : [{
			rowHeight : 0,
			xtype : 'panel',
			layout : 'border',
			items : [com.walker.userManage.getQueryForm(),com.walker.userManage.getGridPnl()]
		}]
	});
	
	com.walker.permission.userpermission("10001");
});

com.walker.userManage.record=null;
//修改密码
com.walker.userManage.editUserPassword=function(){
	var records = com.walker.common.getSelectRecord(com.walker.userManage.userGridPnl,true);
	if(records)
	{
		com.walker.userManage.record=records;
		com.walker.userManage.showEditPasswordWin();
	}else{
		Ext.Msg.alert("提示","请选择一条记录！");
	}
};

com.walker.userManage.showEditPasswordWin=function(){
	com.walker.userManage.editPasswordWin = com.walker.userManage.createEditPasswordWin();
	com.walker.userManage.editPasswordWin.show();
};

com.walker.userManage.createEditPasswordWin=function(){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 350,
		height : 200,
		modal : true,
		closeAction:'close',
		title : '修改密码',
		items : [com.walker.userManage.editPasswordWinForm()],
		buttons: [
          { xtype: "button", text: "确定", handler: function () { com.walker.userManage.updateUserPassword(); } },
          { xtype: "button", text: "取消", handler: function () { com.walker.userManage.editPasswordWin.close(); } }
        ]
	});
	return panelTmp;
};

com.walker.userManage.passFormPanel=null;

com.walker.userManage.editPasswordWinForm=function(){
	var formPanel = new Ext.FormPanel({
	    frame : true,
	    layout : 'form',
	    region : 'north',
	    labelWidth : 80,
		labelAlign : "right",
	    items: [{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       inputType: 'password',
				       fieldLabel : "新密码",
				       width : 150,
				       name : 'NPASSWORD',
				       allowBlank : false,
				       blankText : '请输入密码'
				      }]
				 }]
            },{
            	layout : "column",
            	items : [{
            		columnWidth : .95,
				    layout : "form",
				    items : [{
				       xtype : "textfield",
				       inputType: 'password',
				       fieldLabel : "确认密码",
				       width : 150,
				       name : 'NPASSWORD2'
				      }]
				 }]
            }
        ]
	});
	com.walker.userManage.passFormPanel=formPanel;
	return formPanel;
};

com.walker.userManage.updateUserPassword=function(){
	var errorInfo = com.walker.common.validateForm(com.walker.userManage.passFormPanel);
	if(!errorInfo){
		var npassword=com.walker.userManage.passFormPanel.find("name","NPASSWORD")[0].getValue();
		var npassword2=com.walker.userManage.passFormPanel.find("name","NPASSWORD2")[0].getValue();
		
		if(npassword!=npassword2){
			Ext.Msg.alert("提示","两次输入的密码不一致!");
			return false;
		}
		
		Ext.Ajax.request({
			method:"POST",
			waitMsg:"保存中，请稍后...",
			url : "/walker/user/updatePassword",
			params: { 
				userId : com.walker.userManage.record.get("ID"),
				NPASSWORD : Ext.util.MD5(npassword)
			},
			success: function(response) {
				var result = Ext.util.JSON.decode(response.responseText);
				if(result.success){
					var sumBtn = function(btn){
						com.walker.userManage.editPasswordWin.close();
					};
					Ext.Msg.alert('提示', "修改密码成功。", sumBtn);
				}else{
					Ext.Msg.alert("提示", result.err_msg);
				}
		     },
			 failure: function(response) {
				Ext.Msg.alert("提示", "发生异常!");
			 }
		});
		
	}else{
		Ext.Msg.alert("提示",errorInfo);
	}
};

com.walker.userManage.daleteUser=function(){
	var selReds = com.walker.common.getSelectRecord(com.walker.userManage.userGridPnl,false);
	if(selReds){
		var ids = "";
		for (var i = 0; i < selReds.length; i++) {
			ids += "'"+selReds[i].get("ID")+"',";
		}
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			Ext.Ajax.request({
				url: '/walker/user/deleteUser',
				params: {
					ids : ids.substring(0,ids.lastIndexOf(','))
				},
				success: function(response) {
					var result = Ext.decode(response.responseText);
					if(result.success){
						Ext.Msg.alert("提示","删除成功！");
						com.walker.userManage.queryUser();
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