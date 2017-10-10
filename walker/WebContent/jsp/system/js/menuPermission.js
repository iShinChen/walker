Ext.namespace("com.walker.menuPermission");

com.walker.menuPermission.contentPnl = null;
com.walker.menuPermission.currSelectedNode = null;
com.walker.menuPermission.treeRoot = null;
com.walker.menuPermission.nodeFormLevel = null;

Ext.onReady(function(){

	var treePnl = new Ext.Panel({
		title : '系统权限',
		region : "west",
		id : 'treePanel',
		width : 280,
		layout : "accordion",
		bodyStyle : "display:block;font-weight:bold;letter-spacing:3px;"
	});
	
	var contentPanel = new Ext.form.FormPanel({
		title : '权限内容',
		region : "center",
	    width : 650,
	    frame : true,
	    layout : "form",
	    labelWidth : 100,
	    labelAlign : "right",
	    tbar : [
	            {
	            	text:'保存',
		            iconCls : 'btnIconSave',
		            id : 'saveNodeBtn',
		            handler : function(){
		            	com.walker.menuPermission.saveTreeNode();
		            }
	            },{
	            	text:'删除',
		            iconCls : 'btnIconDel',
		            id : 'deleteNodeBtn',
		            handler : function(){
		            	com.walker.menuPermission.deleteTreeNode();
		            }
	            }
	            ],
	    items : [{//行开始
	        	layout : "column", // 从左往右的布局
	        	items : [{
						    columnWidth : .95, // 该列有整行中所占百分比
						    layout : "form", // 从上往下的布局
						    style : 'padding:10px 0px 0px 0px',
						    items : [{
						       xtype : "textfield",
						       fieldLabel : "父节点ID",
						       name : "PARENT_ID",
						       width : 300,
						       readOnly : true,
							   style:'background:#E6E6E6',
							   editable : false
						      }]
						}
	        	]
	    }//行结束
	    ,{//行开始
	    	layout : "column", // 从左往右的布局
        	items : [{
					    columnWidth : .95, // 该列有整行中所占百分比
					    layout : "form", // 从上往下的布局
					    items : [{
					       xtype : "textfield",
					       fieldLabel : "节点ID",
					       name : "MENU_ID",
					       width : 300,
					       readOnly : true,
						   style:'background:#E6E6E6',
						   editable : false
					      }]
					}
        	]
		}//行结束
	    ,{//行开始
	    	layout : "column", // 从左往右的布局
        	items : [{
					    columnWidth : .95, // 该列有整行中所占百分比
					    layout : "form", // 从上往下的布局
					    items : [{
					       xtype : "textfield",
					       fieldLabel : "节点名称",
					       name : "MENU_NAME",
					       width : 300,
					       allowBlank : false,
					       blankText : '请填写节点名称',
					      }]
					}
        	]
		}//行结束
	    ,{//行开始
	    	layout : "column", // 从左往右的布局
        	items : [{
					    columnWidth : .95, // 该列有整行中所占百分比
					    layout : "form", // 从上往下的布局
					    items : [{
					       xtype : "combo",
					       fieldLabel : "节点类型",
					       name : "MENU_TYPE",
					       width : 300,
					       triggerAction: 'all',
				           displayField : 'text', 
				           valueField : 'value', 
				           store: new Ext.data.SimpleStore({
				                fields :['text','value'],
				                data : [['请选择...',''],['菜单','menu'],['按钮','button']]
				           }),
				           mode: 'local',
				           editable:false
					      }]
					}
        	]
		}//行结束
	    ,{//行开始
	    	layout : "column", // 从左往右的布局
        	items : [{
					    columnWidth : .95, // 该列有整行中所占百分比
					    layout : "form", // 从上往下的布局
					    items : [{
					       xtype : "combo",
					       fieldLabel : "末级节点",
					       name : "LEAF",
					       width : 300,
					       triggerAction: 'all',
				           displayField : 'text', 
				           valueField : 'value', 
				           store: new Ext.data.SimpleStore({
				                fields :['text','value'],
				                data : [['请选择...',''],['是','1'],['否','0']]
				           }),
				           mode: 'local',
				           editable:false
					      }]
					}
        	]
		}//行结束
	    ,{//行开始
	    	layout : "column", // 从左往右的布局
        	items : [{
					    columnWidth : .95, // 该列有整行中所占百分比
					    layout : "form", // 从上往下的布局
					    items : [{
					       xtype : "textfield",
					       fieldLabel : "功能URL",
					       name : "URL",
					       width : 300
					      }]
					}
        	]
		}//行结束
	    ,{//行开始
	    	layout : "column", // 从左往右的布局
        	items : [{
					    columnWidth : .95, // 该列有整行中所占百分比
					    layout : "form", // 从上往下的布局
					    items : [{
					       xtype : "textfield",
					       fieldLabel : "排序号",
					       name : "MENU_ORDER",
					       width : 300
					      }]
					}
        	]
		}//行结束
	    ]
	});//formPanel结束
	
	com.walker.menuPermission.contentPnl = contentPanel;
	
	new Ext.Viewport({
		renderTo : 'permissionDiv',
		id : 'permissionView',
		bodyStyle : 'padding:5px',
		layout : 'border',
		title : 'Row Layout',
		items : [
	         treePnl,
	         contentPanel
		],
		listeners : {
			afterrender : function(){
				// set the root node
				var Tree = Ext.tree;
				
				var root = new Tree.AsyncTreeNode({
					text: '权限树形结构',
					draggable:false,
					id:'0'
				});
				
				com.walker.menuPermission.treeRoot = root;
				
				var tree = new Tree.TreePanel({
					useArrows:true,
					autoScroll:true,
					animate:true,
					enableDD:true,
					containerScroll: true,
				    el : 'treePanel',
				    autoHeight: false,
				    width:280,
				    height:505,
				    tbar : [{
				            xtype:'tbbutton',
				            iconCls : 'btnIconAddIn',
				            id:"add_currLevel",
				            text:"新增同级",
				            handler : function() {
				            	if(com.walker.menuPermission.currSelectedNode != null)
			            		{
				            		if(com.walker.menuPermission.currSelectedNode.attributes.id == "0")
			            			{
				            			Ext.Msg.alert("提示","根节点无法新增同级，请重新选择！");
			            			}else{
			        					com.walker.menuPermission.contentPnl.find("name","MENU_ID")[0].setValue("");
			        					com.walker.menuPermission.contentPnl.find("name","MENU_NAME")[0].setValue("");
			        					com.walker.menuPermission.contentPnl.find("name","URL")[0].setValue("");
			        					com.walker.menuPermission.contentPnl.find("name","MENU_ORDER")[0].setValue("");
			            			}
			            		}else{
			            			Ext.Msg.alert("提示","请选择一个节点！");
			            		}
							}
			            },'-',{
				            xtype:'tbbutton',
				            iconCls : 'btnIconAddIn',
				            id:"add_nextLevel",
				            text:"新增下级",
				            handler : function() {
				            	if(com.walker.menuPermission.currSelectedNode != null)
			            		{
				            		com.walker.menuPermission.contentPnl.find("name","PARENT_ID")[0].setValue(com.walker.menuPermission.currSelectedNode.attributes.id);
									com.walker.menuPermission.contentPnl.find("name","MENU_ID")[0].setValue("");
									com.walker.menuPermission.contentPnl.find("name","MENU_NAME")[0].setValue("");
									com.walker.menuPermission.contentPnl.find("name","MENU_TYPE")[0].setValue("");
									com.walker.menuPermission.contentPnl.find("name","URL")[0].setValue("");
									com.walker.menuPermission.contentPnl.find("name","MENU_ORDER")[0].setValue("");
									com.walker.menuPermission.nodeFormLevel = Number(com.walker.menuPermission.nodeFormLevel) + 1;
			            		}else{
			            			Ext.Msg.alert("提示","请选择一个节点！");
			            		}
							}
			            },'->',{
				            xtype:'tbbutton',
				            iconCls : 'btnIconRecover',
				            id:"tree_refresh",
				            text:"刷    新",
				            handler : function() {
								root.reload();
								com.walker.menuPermission.currSelectedNode = null;
								com.walker.menuPermission.contentPnl.find("name","PARENT_ID")[0].setValue("");
								com.walker.menuPermission.contentPnl.find("name","MENU_ID")[0].setValue("");
								com.walker.menuPermission.contentPnl.find("name","MENU_NAME")[0].setValue("");
								com.walker.menuPermission.contentPnl.find("name","MENU_TYPE")[0].setValue("");
								com.walker.menuPermission.contentPnl.find("name","LEAF")[0].setValue("");
								com.walker.menuPermission.contentPnl.find("name","URL")[0].setValue("");
								com.walker.menuPermission.contentPnl.find("name","MENU_ORDER")[0].setValue("");
								com.walker.menuPermission.nodeFormLevel = null;
							}
			            }],
					loader: new Tree.TreeLoader({
						dataUrl : '/sync/user/getTreeMenuPermissionList'
					})
				});
				
				tree.on('checkchange', function(node, checked) {  
					node.expand();
					node.attributes.checked = checked;  
					node.eachChild(function(child) {  
						child.ui.toggleCheck(checked);  
						child.attributes.checked = checked;  
						child.fireEvent('checkchange', child, checked);  
						});  
				}, tree); 
				
				tree.on('click',function(node) {
					com.walker.menuPermission.contentPnl.find("name","PARENT_ID")[0].setValue(node.attributes.parent);
					com.walker.menuPermission.contentPnl.find("name","MENU_ID")[0].setValue(node.attributes.id);
					com.walker.menuPermission.contentPnl.find("name","MENU_NAME")[0].setValue(node.attributes.text);
					com.walker.menuPermission.contentPnl.find("name","MENU_TYPE")[0].setValue(node.attributes.type);
					if(node.attributes.url=='null'){
						com.walker.menuPermission.contentPnl.find("name","URL")[0].setValue();
					}else{
						com.walker.menuPermission.contentPnl.find("name","URL")[0].setValue(node.attributes.url);
					}
					
					if(node.attributes.orderBy=='null'){
						com.walker.menuPermission.contentPnl.find("name","MENU_ORDER")[0].setValue();
					}else{
						com.walker.menuPermission.contentPnl.find("name","MENU_ORDER")[0].setValue(node.attributes.orderBy);
					}
					
					com.walker.menuPermission.contentPnl.find("name","LEAF")[0].setValue(node.attributes.realLeaf?'1':'0');
					com.walker.menuPermission.nodeFormLevel = node.attributes.level;
					
					com.walker.menuPermission.currSelectedNode = node;
					
					if(node.attributes.id == "0")
					{
						Ext.getCmp('add_currLevel').setDisabled(true);
						Ext.getCmp('add_nextLevel').setDisabled(true);
						Ext.getCmp('saveNodeBtn').setDisabled(true);
					}else{
						Ext.getCmp('saveNodeBtn').setDisabled(false);
						Ext.getCmp('add_currLevel').setDisabled(false);
						if(node.attributes.type == "button")
						{
							Ext.getCmp('add_nextLevel').setDisabled(true);
						}else{
							Ext.getCmp('add_nextLevel').setDisabled(false);
						}
					}
				});
				
				tree.setRootNode(root);
				// render the tree
				tree.render();
				root.expand();
			}
		}
	});
});

com.walker.menuPermission.deleteTreeNode = function() {
	if(com.walker.menuPermission.currSelectedNode != null)
	{
		var sumBtn = function(btn){
			if(btn!='yes'){return;}
			//保存请求后台
			Ext.Ajax.request({
			   url: '/sync/user/deleteMenu',
			   params: { 
				   MENU_ID : com.walker.menuPermission.contentPnl.find("name","MENU_ID")[0].getValue()
			   },
			   success: function(response) {
				   var result = Ext.util.JSON.decode(response.responseText);
					if(result.success == 'success'){
						Ext.Msg.alert("提示","删除成功！");
						com.walker.menuPermission.treeRoot.reload();
						
						com.walker.menuPermission.currSelectedNode = null;
						com.walker.menuPermission.contentPnl.find("name","PARENT_ID")[0].setValue();
						com.walker.menuPermission.contentPnl.find("name","MENU_ID")[0].setValue();
						com.walker.menuPermission.contentPnl.find("name","MENU_NAME")[0].setValue();
						com.walker.menuPermission.contentPnl.find("name","MENU_TYPE")[0].setValue();
						com.walker.menuPermission.contentPnl.find("name","LEAF")[0].setValue();
						com.walker.menuPermission.contentPnl.find("name","URL")[0].setValue();
						com.walker.menuPermission.contentPnl.find("name","MENU_ORDER")[0].setValue();
						com.walker.menuPermission.nodeFormLevel = null;
					}else{
						Ext.Msg.alert("提示","删除失败！");
					}
					
			   },
			   failure: function(response) {
					Ext.Msg.alert("提示","保存失败");
			   }
			});
		};
		Ext.Msg.confirm('系统提示',"确定删除吗？",sumBtn);
	}else{
		Ext.Msg.alert("提示","请选择一个节点！");
	}
};

com.walker.menuPermission.saveTreeNode = function() {
	
	if(com.walker.menuPermission.currSelectedNode == null)
	{
		Ext.Msg.alert("提示","请选择一个节点！");
		return false;
	}
	var errorInfo = com.walker.common.validateForm(com.walker.menuPermission.contentPnl);
	if(!errorInfo){
		var menuPermissionDataObj = new Object({
			ID : com.walker.menuPermission.contentPnl.find("name","MENU_ID")[0].getValue(),
			MENU_TYPE : com.walker.menuPermission.contentPnl.find("name","MENU_TYPE")[0].getValue(),
			MENU_CODE : com.walker.menuPermission.contentPnl.find("name","MENU_ID")[0].getValue(),
			MENU_NAME : com.walker.menuPermission.contentPnl.find("name","MENU_NAME")[0].getValue(),
			URL : com.walker.menuPermission.contentPnl.find("name","URL")[0].getValue(),
			LEVEL : com.walker.menuPermission.nodeFormLevel,
			PARENT_ID : com.walker.menuPermission.contentPnl.find("name","PARENT_ID")[0].getValue(),
			DELETE_FLAG : "1",
			LEAF : com.walker.menuPermission.contentPnl.find("name","LEAF")[0].getValue().toString(),
			MENU_ORDER : com.walker.menuPermission.contentPnl.find("name","MENU_ORDER")[0].getValue()
		});
		
		//保存请求后台
		Ext.Ajax.request({
		   url: '/sync/user/saveMenu.do',
		   success: function(response) {
			   var result = Ext.util.JSON.decode(response.responseText);
				if(result.success == 'success'){
					Ext.Msg.alert("提示","保存成功");
					com.walker.menuPermission.treeRoot.reload();
					
					com.walker.menuPermission.currSelectedNode = null;
					com.walker.menuPermission.contentPnl.find("name","PARENT_ID")[0].setValue();
					com.walker.menuPermission.contentPnl.find("name","MENU_ID")[0].setValue();
					com.walker.menuPermission.contentPnl.find("name","MENU_NAME")[0].setValue();
					com.walker.menuPermission.contentPnl.find("name","MENU_TYPE")[0].setValue();
					com.walker.menuPermission.contentPnl.find("name","LEAF")[0].setValue();
					com.walker.menuPermission.contentPnl.find("name","URL")[0].setValue();
					com.walker.menuPermission.contentPnl.find("name","MENU_ORDER")[0].setValue();
					com.walker.menuPermission.nodeFormLevel = null;
				}else{
					Ext.Msg.alert("提示","保存失败");
				}
		   },
		   failure: function(response) {
				Ext.Msg.alert("提示","保存失败");
		   },
		   params: { 
			   menuPermissionDataJson : JSON.stringify(menuPermissionDataObj)
		   }
		});
	}else{
		Ext.Msg.alert("提示",errorInfo);
	}
};