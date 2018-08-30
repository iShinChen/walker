Ext.namespace("com.walker.file.category");
/**
 * 显示入口
 */
Ext.onReady(function() {
	new Ext.Viewport( {
		layout : 'border',
		renderTo : 'contentDev',
		items : [ com.walker.file.category.tree, com.walker.file.category.fmPanel ]
	});
});

com.walker.file.category.currTreeNode = '';

com.walker.file.category.currentTreeNodeId = '';

/**
 *栏目类型 
 */
com.walker.file.category.contentType = new Ext.data.Store({
	url : '/jkcms/user/getPubCode?PUB_CODE_NAME=CONTENT_TYPE',
	reader : new Ext.data.JsonReader({
		fields : ["VALUE","CODE"],
		root : 'rows'
	})
});
com.walker.file.category.contentType.load();
/**
 *上线状态
 */
com.walker.file.category.contentStatus = new Ext.data.Store({
	url : '/jkcms/user/getPubCode?PUB_CODE_NAME=CONTENT_STATUS',
	reader : new Ext.data.JsonReader({
		fields : ["VALUE","CODE"],
		root : 'rows'
	})
});
com.walker.file.category.contentStatus.load();

/**
 * 根节点
 */
com.walker.file.category.root = new Ext.tree.AsyncTreeNode( {
	text : '栏目树',
	id : '0'
});

com.walker.file.category.refreshTreeNode = function(){
	var node = com.walker.file.category.tree.getNodeById(com.walker.file.category.currentTreeNodeId);
	if (node) {
		var path = node.getPath('id');
		com.walker.file.category.root.reload(function(){
			com.walker.file.category.tree.expandPath(path, 'id', function(){
				com.walker.file.category.currTreeNode = com.walker.file.category.tree.getNodeById(com.walker.file.category.currentTreeNodeId);
				com.walker.file.category.currTreeNode.select();
			});
		});
	}
	else {
		com.walker.file.category.root.reload();
	}
	
	return false;
};

/**
 * 节点树
 */
com.walker.file.category.tree = new Ext.tree.TreePanel( {
	title : "栏目树",
	region : "west",
	width : 200,
	collapsible : true,
	autoScroll : true,
	split : true,
	rootVisible : false,
	enableDD: true,
	loader : new Ext.tree.TreeLoader( {
		dataUrl : "/jkcms/category/getCategoryTree"
	}),
	tbar : ['->', {
        xtype:'tbbutton',
        iconCls : 'btnIconRecover',
        id:"tree_refresh",
        text:"刷新",
        handler : com.walker.file.category.refreshTreeNode
    }],
	listeners : {
		click : function(n) {
			com.walker.file.category.currTreeNode = n;
			com.walker.file.category.currentTreeNodeId = n.id;
			if (n.attributes.id == 0) {
				Ext.getCmp('saveBtn').setDisabled(true);
				Ext.getCmp('addCurBtn').setDisabled(true);
				com.walker.file.category.fmPanel.find("name", "categoryName")[0].setValue(n.attributes.text);
				com.walker.file.category.fmPanel.find("name", "categoryID")[0].setValue(n.attributes.id);
				com.walker.file.category.fmPanel.find("name", "cdnId")[0].setValue(n.attributes.cdnId);
				com.walker.file.category.fmPanel.find("name", "parentName")[0].setValue('');
				com.walker.file.category.fmPanel.find("name", "parentID")[0].setValue('');
				com.walker.file.category.fmPanel.find("name", "type")[0].setValue('');
				com.walker.file.category.fmPanel.find("name", "status")[0].setValue('');
				com.walker.file.category.fmPanel.find("name", "description")[0].setValue('');
			} else {
				Ext.getCmp('saveBtn').setDisabled(false);
				Ext.getCmp('addCurBtn').setDisabled(false);
				com.walker.file.category.fmPanel.find("name", "categoryName")[0].setValue(n.attributes.text);
				com.walker.file.category.fmPanel.find("name", "categoryID")[0].setValue(n.attributes.id);
				com.walker.file.category.fmPanel.find("name", "cdnId")[0].setValue(n.attributes.cdnId);
				if (n.attributes.content1 && n.attributes.content1 != 'null') {
					com.walker.file.category.fmPanel.find("name", "description")[0].setValue(n.attributes.content1);
				} else {
					com.walker.file.category.fmPanel.find("name", "description")[0].setValue('');
				}
				com.walker.file.category.fmPanel.find("name", "parentName")[0].setValue(n.parentNode.attributes.text);
				com.walker.file.category.fmPanel.find("name", "parentID")[0].setValue(n.parentNode.attributes.id);
				if(n.attributes.type == "null" || n.attributes.type == 0 ){
					com.walker.file.category.fmPanel.find("name", "type")[0].setValue('');
				}else{
					console.info("TYPE:" + n.attributes.type);
					com.walker.file.category.fmPanel.find("name", "type")[0].setValue(n.attributes.type);
				}
				
				com.walker.file.category.fmPanel.find("name", "status")[0].setValue(n.attributes.status);
				n.expand();
			}
		},
		beforenodedrop : function(obj) {
			// 拖拽源的拖拽数据
			var sourceData = obj.data.node;
			//目标节点
			var targetData = obj.target;
			
			if (obj.point == 'append') {
				if (sourceData.parentNode.id == targetData.id) {
					obj.cancel = true;
					return false;
				}
			}
		},
		nodedrop : function(obj) {
			Ext.Msg.confirm('提示', '确定移动该节点?', function(btn) {
				// 拖拽源的拖拽数据
				var sourceData = obj.data.node;
				//目标节点
				var targetData = obj.target;
				if (btn != "yes") {
					var sourceParentId = sourceData.attributes.parent;
					var targetParentId = targetData.attributes.parent;
					var sourceId = sourceData.id;
					var targetId = targetData.id;
					
					var treeSorter = function (a, b) {
						var aOrder = Number(a.attributes.orderBy);
						var bOrder = Number(b.attributes.orderBy);
						if (aOrder > bOrder) {
							return 1;
						}
						else if (aOrder == bOrder) {
							return 0;
						}
						else {
							return -1;
						}
					};
					
					if (obj.point == 'append') {
						com.walker.file.category.tree.getNodeById(sourceParentId).appendChild(sourceData);
						com.walker.file.category.tree.getNodeById(sourceParentId).sort(treeSorter);
					}
					else {
						if (sourceParentId == targetParentId) {
							com.walker.file.category.tree.getNodeById(sourceParentId).sort(treeSorter);
						} else {
							com.walker.file.category.tree.getNodeById(sourceParentId).appendChild(sourceData);
							com.walker.file.category.tree.getNodeById(sourceParentId).sort(treeSorter);
						}
					}

					if (com.walker.file.category.currentTreeNodeId) {
						com.walker.file.category.tree.getNodeById(com.walker.file.category.currentTreeNodeId).select();
					}
					return;
				}

				var jsonParams = new Object({
					sourceParentId : sourceData.attributes.parent,
					targetParentId : targetData.attributes.parent,
					sourceId : sourceData.id,
					targetId : targetData.id,
					point : obj.point
				});
				
				// 保存请求后台
				Ext.Ajax.request( {
					url : '/jkcms/category/updateOrderIndex',
					params : jsonParams,
					success : function(response) {
						Ext.Msg.alert("提示", "操作成功!");
						var result = Ext.util.JSON.decode(response.responseText);
						if (result.success == 'success') {
							var parentNode = sourceData.parentNode;
							if (parentNode) {
								var path = parentNode.getPath('id');
								com.walker.file.category.root.reload(function(){
									com.walker.file.category.tree.expandPath(path, 'id');
									if (com.walker.file.category.currentTreeNodeId) {
										var currentNode = com.walker.file.category.tree.getNodeById(com.walker.file.category.currentTreeNodeId);
										currentNode.select();
										com.walker.file.category.fmPanel.find("name", "parentName")[0].setValue(currentNode.parentNode.attributes.text);
										com.walker.file.category.fmPanel.find("name", "parentID")[0].setValue(currentNode.parentNode.attributes.id);
									}
								});
							}
						}
					},
					failure : function(response) {
						Ext.Msg.alert("提示", "操作失败!");
						if (com.walker.file.category.currentTreeNodeId) {
							com.walker.file.category.tree.getNodeById(com.walker.file.category.currentTreeNodeId).select();
						}
					}
				});
			});
		},
		expandnode : function(node){
			if(node.childNodes) {
				for ( var i = 0; i < node.childNodes.length; i++) {
					var status = node.childNodes[i].attributes.status;
					if(status != '01') {
						node.childNodes[i].getUI().addClass("offline_category");
					}
					else {
						node.childNodes[i].getUI().addClass("online_category");
					}
				}
			}
		}
	}
});
com.walker.file.category.tree.setRootNode(com.walker.file.category.root);
// 展开节点
com.walker.file.category.root.expand();

/**
 * 栏目内容
 */
com.walker.file.category.fmPanel = new Ext.FormPanel({
	title : '栏目内容',
	frame : true,
	layout : 'form',
	region : 'center',
	labelWidth : 100,
	labelAlign : "right",
	fileUpload : true,
	bodyStyle : 'padding:10px 0px 0px 0px',
	items : [ {
		layout : "column",
		items : [ {
			columnWidth : .95,
			layout : "form",
			items : [ {
				xtype : "textfield",
				fieldLabel : "上级栏目ID",
				width : 300,
				name : 'parentID',
				readOnly : true,
				hidden: true
			} ]
		} ]
	}, {
		layout : "column",
		items : [ {
			columnWidth : .95,
			layout : "form",
			items : [ {
				xtype : "textfield",
				fieldLabel : "上级栏目名称",
				width : 300,
				name : 'parentName',
				readOnly : true
			} ]
		} ]
	}, {
		layout : "column",
		items : [ {
			columnWidth : .95,
			layout : "form",
			items : [ {
				xtype : "textfield",
				fieldLabel : "栏目ID",
				width : 300,
				name : 'categoryID',
				readOnly : true
			} ]
		} ]
	}, {
		layout : "column",
		items : [ {
			columnWidth : .95,
			layout : "form",
			items : [ {
				xtype : "textfield",
				hiddern : true,
				fieldLabel : "CDN_ID",
				width : 300,
				name : 'cdnId',
				readOnly : true
			} ]
		} ]
	}, {
		layout : "column",
		items : [ {
			columnWidth : .95,
			layout : "form",
			items : [ {
				xtype : "textfield",
				fieldLabel : "栏目名称",
				width : 300,
				name : 'categoryName',
				maxLength : 50,
				allowBlank : false
			} ]
		} ]
	}, {
		layout : "column",
		items : [ {
			columnWidth : .95,
			layout : "form",
			items : [ {
			 	xtype : 'combo',
				fieldLabel : '栏目类型',
				hiddenName : 'type',
				name : 'type',
	            triggerAction: 'all',
	            displayField : 'VALUE', 
	            valueField : 'CODE', 
	            mode: 'local',
	            store: com.walker.file.category.contentType,
	            emptyText:'请选择...',
	            blankText : '请选择',
	            editable:false ,
	            width : 300,
	            allowBlank : false
			} ]
		} ]
	},{
		layout : "column",
		items : [ {
			columnWidth : .95,
			layout : "form",
			items : [ {
			 	xtype : 'combo',
				fieldLabel : '上线状态',
				hiddenName : "status",
				name : "status",
	            triggerAction: 'all',
	            displayField : 'VALUE', 
	            valueField : 'CODE', 
	            mode: 'local',
	            store: com.walker.file.category.contentStatus,
	            emptyText:'请选择...',
	            blankText : '请选择',
	            editable:false ,
	            width : 300,
	            allowBlank : false
			} ]
		} ]
	}, {
		layout : "column",
		items : [ {
			columnWidth : .95,
			layout : "form",
			items : [ {
				xtype : "textfield",
				fieldLabel : "导航地址",
				width : 300,
				maxLength : 200,
				name : 'description'
			} ]
		} ]
	}],
	tbar : [{
		text : '保存',
		iconCls : 'btnIconSave',
		id : 'saveBtn',
		disabled : true,
		handler : function() {
			com.walker.file.category.saveTreeNode();
		}
	},
	'|',
	{
		text : '新增同级栏目',
		iconCls : 'btnIconAddIn',
		id : 'addCurBtn',
		handler : function() {
			if (com.walker.file.category.currTreeNode) {
				com.walker.file.category.fmPanel.find("name", "parentName")[0].setValue(com.walker.file.category.currTreeNode.parentNode.attributes.text);
				com.walker.file.category.fmPanel.find("name", "parentID")[0].setValue(com.walker.file.category.currTreeNode.parentNode.attributes.id);
				com.walker.file.category.fmPanel.find("name", "categoryName")[0].setValue("");
				com.walker.file.category.fmPanel.find("name", "description")[0].setValue("");
				com.walker.file.category.fmPanel.find("name", "categoryID")[0].setValue("");
				com.walker.file.category.fmPanel.find("name", "cdnId")[0].setValue("");
				com.walker.file.category.fmPanel.find("name", "type")[0].setValue('');
				com.walker.file.category.fmPanel.find("name", "status")[0].setValue('');
				com.walker.file.category.fmPanel.find("name", "description")[0].setValue('');
			} else {
				Ext.Msg.alert("提示", "请选择一个节点!");
			}
		}
	},
	'|',
	{
		text : '新增下级栏目',
		iconCls : 'btnIconAddIn',
		id : 'addDownBtn',
		handler : function() {
			if (com.walker.file.category.currTreeNode) {
				Ext.getCmp('saveBtn').setDisabled(false);
				com.walker.file.category.fmPanel.find("name", "parentName")[0].setValue(com.walker.file.category.currTreeNode.attributes.text);
				com.walker.file.category.fmPanel.find("name", "parentID")[0].setValue(com.walker.file.category.currTreeNode.attributes.id);
				com.walker.file.category.fmPanel.find("name", "categoryName")[0].setValue("");
				com.walker.file.category.fmPanel.find("name", "description")[0].setValue("");
				com.walker.file.category.fmPanel.find("name", "categoryID")[0].setValue("");
				com.walker.file.category.fmPanel.find("name", "cdnId")[0].setValue("");
				com.walker.file.category.fmPanel.find("name", "type")[0].setValue('');
				com.walker.file.category.fmPanel.find("name", "status")[0].setValue('');
				com.walker.file.category.fmPanel.find("name", "description")[0].setValue('');
			} else {
				Ext.Msg.alert("提示", "请选择一个节点!");
			}
		}
	}, '|', {
		text : '删除',
		iconCls : 'btnIconDel',
		handler : function() {
			com.walker.file.category.deleteTreeNode();
		}
	}]
});

/**
 * 保存栏目
 */
com.walker.file.category.saveTreeNode = function() {
	if (com.walker.file.category.currTreeNode) {
		if (com.walker.file.category.fmPanel.getForm().isValid()) {
			var jsonParams = new Object({
				NAME : com.walker.file.category.fmPanel.find("name", "categoryName")[0].getValue(),
				CATEGORY_ID : com.walker.file.category.fmPanel.find("name", "categoryID")[0].getValue(),
				CDN_ID : com.walker.file.category.fmPanel.find("name", "cdnId")[0].getValue(),
				DESCRIPTION : com.walker.file.category.fmPanel.find("name", "description")[0].getValue(),
				PARENTID : com.walker.file.category.fmPanel.find("name", "parentID")[0].getValue(),
				TYPE : com.walker.file.category.fmPanel.find("name","type")[0].getValue(),
				STATUS : com.walker.file.category.fmPanel.find("name","status")[0].getValue()
			});

			// 保存请求后台
			Ext.Ajax.request( {
				url : '/jkcms/category/saveCategory',
				params : {
					jsonParams : Ext.util.JSON.encode(jsonParams)
				},
				success : function(response) {
					var result = Ext.util.JSON.decode(response.responseText);
					if (result.success == 'success') {
						Ext.Msg.alert("提示", "保存成功!", function(){
							com.walker.file.category.fmPanel.find("name", "categoryID")[0].setValue(result.CATEGORY_ID);
							com.walker.file.category.fmPanel.find("name", "cdnId")[0].setValue(result.CDN_ID);
							com.walker.file.category.currentTreeNodeId = result.CATEGORY_ID;
							
							var parentId = com.walker.file.category.fmPanel.find("name", "parentID")[0].getValue();
							var parentNode = com.walker.file.category.tree.getNodeById(parentId);
							if (parentNode) {
								var path = parentNode.getPath('id') + "/" + result.CATEGORY_ID;
								com.walker.file.category.root.reload(function(){
									com.walker.file.category.tree.expandPath(path, 'id', function(){
										com.walker.file.category.currTreeNode = com.walker.file.category.tree.getNodeById(com.walker.file.category.currentTreeNodeId);
										com.walker.file.category.currTreeNode.select();
									});
								});
							}
							
							return false;
						});
					}
				},
				failure : function(response) {
					Ext.Msg.alert("提示", "保存失败!");
				}
			});
		}
	} else {
		Ext.Msg.alert("提示", "请选择一个节点!");
	}
};

/**
 * 删除栏目
 */
com.walker.file.category.deleteTreeNode = function(){
	var nodeObj = new Object();
	nodeObj.nodeIds = '';
	com.walker.common.getChildNodes(com.walker.file.category.currTreeNode, nodeObj);
	var nodes = nodeObj.nodeIds.substring(0, nodeObj.nodeIds.lastIndexOf(','));
	
	Ext.Ajax.request( {
		url : '/jkcms/category/delCategory',
		success : function(response) {
			var result =Ext.decode(response.responseText);
			if (result.success == 'success') {
				Ext.Msg.alert("提示", "删除成功!", function(){
					var node = com.walker.file.category.tree.getNodeById(com.walker.file.category.currentTreeNodeId);
					if (node) {
						node.remove();
					}
					
					Ext.getCmp('saveBtn').setDisabled(false);
					com.walker.file.category.fmPanel.find("name", "parentName")[0].setValue('');
					com.walker.file.category.fmPanel.find("name", "parentID")[0].setValue('');
					com.walker.file.category.fmPanel.find("name", "categoryName")[0].setValue("");
					com.walker.file.category.fmPanel.find("name", "description")[0].setValue("");
					com.walker.file.category.fmPanel.find("name", "categoryID")[0].setValue("");
					com.walker.file.category.fmPanel.find("name", "cdnId")[0].setValue("");
					com.walker.file.category.fmPanel.find("name", "type")[0].setValue('');
					com.walker.file.category.fmPanel.find("name", "status")[0].setValue('');
					com.walker.file.category.fmPanel.find("name", "description")[0].setValue('');
					com.walker.file.category.currTreeNode = '';
				});
			}
		},
		failure : function(response) {
			Ext.Msg.alert("提示", "删除失败!");
		},
		params : {
			categoryId : nodes
		}
	});
};

/**
 * 更新海报
 */
com.walker.file.category.updateImage = function(imgUrl,resloution){
	Ext.Ajax.request( {
		url : '/jkcms/category/updateImage',
		success : function(response) {
			var result = Ext.decode(response.responseText);
			if (result.success == 'success') {
				Ext.Msg.alert("提示", "海报上传成功!");
				com.walker.file.category.refreshTreeNode();
			}
		},
		failure : function(response) {
			Ext.Msg.alert("提示", "海报上传失败!");
		},
		params : {
			diffType : diffType,
			requestId : requestId
		}
	});
};

com.walker.file.category.doSuccess = function(response) {
	var result = Ext.decode(response.responseText);
	if(result.success){
		Ext.Msg.alert("提示","操作成功!");
	}else{
		Ext.Msg.alert("提示","操作失败!");
	}
};

com.walker.file.category.doFail = function(response) {
	Ext.Msg.alert("提示",result.error);
};