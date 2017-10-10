Ext.namespace("com.walker.permission");

com.walker.permission.rolePermissionTree = null;
com.walker.permission.rolePermissionChecked = "";

com.walker.permission.userpermission = function(menuId) {
	//查询请求后台
	Ext.Ajax.request({
	   url: '/sync/user/getPermission',
	   success: com.walker.permission.loadPerSuccess,
	   params : {
			menuId : menuId
	   }
	});
};

com.walker.permission.loadPerSuccess = function(response) {
	var result = Ext.util.JSON.decode(response.responseText);
	var permission = result.permission;
	if(permission != null && permission != '')
	{
		var buttonArr = permission.split(",");
		//设置权限按钮
		for(var i = 0; i < buttonArr.length; i++){
			Ext.getCmp(buttonArr[i]).setVisible(true);
		}
	}
};


com.walker.permission.showRolePermissionTree = function(roleId){
	var root = new Ext.tree.AsyncTreeNode({
		expanded: true,
		text: '权限树形结构',
		draggable:false,
		id:'source'
	});
	
	var tree = new Ext.tree.TreePanel({
		useArrows:true,
		autoScroll:true,
		animate:true,
		enableDD:true,
		containerScroll: true,
	    height:395,
	    width:288,
	    root:root,
	    tbar : ['->',{
	            xtype:'tbbutton',
	            iconCls : 'btnIconRecover',
	            //id:"tree_refresh",
	            text:"刷    新",
	            handler : function() {
					root.reload();
				}
            }],
		loader: new Ext.tree.TreeLoader({
			dataUrl : '/sync/user/getTreeMenuPermissionListByRole',
			baseParams : {  
                roleId : roleId
            } 
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
	
	com.walker.permission.rolePermissionTree = tree;
	
	var window = new Ext.Window({  
        layout : 'fit',//设置window里面的布局  
        width:300,
        height:450,
        title:'系统权限树',
        //关闭时执行隐藏命令,如果是close就不能再show出来了 
        closeAction:'close',//hide
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
        items : [
                 com.walker.permission.rolePermissionTree
             ],
        buttons : [{  
            text : '保存',  
            handler : function(){  
            	com.walker.permission.rolePermissionChecked = "";
            	var nodes = com.walker.permission.rolePermissionTree.getRootNode().childNodes;  
                for (var j = 0; j < nodes.length; j++) {  
                    var node = nodes[j];
                    if (node.getUI().checkbox.checked) {
                    	com.walker.permission.rolePermissionChecked += "," + node.id;
                    	com.walker.permission.getRolePermissionTreeNode(node);
                    }
                }  
                
                //查询请求后台
            	Ext.Ajax.request({
            	   url: '/sync/user/saveRoleMenuPermission',
            	   success: function(response) {
            		   var result = Ext.util.JSON.decode(response.responseText);
            		   com.walker.permission.rolePermissionTree.getRootNode().reload();
            	   },
            	   params : {
            			roleMenuPermission : com.walker.permission.rolePermissionChecked,
            			roleId : roleId
            	   }
            	});
            }  
        },{  
            text : '取消',
            handler : function(){  
                window.hide();
            }
        }]
    });  
	window.show();
};

com.walker.permission.getRolePermissionTreeNode = function(node){
	if (node.hasChildNodes()) {  
        for (var i = 0; i < node.childNodes.length; i++) {  
        	var cnode = node.childNodes[i];
            if (cnode.getUI().checkbox.checked) {  
                 com.walker.permission.rolePermissionChecked += "," + cnode.id;
                 com.walker.permission.getRolePermissionTreeNode(cnode);
            }  
        } 
    }  
};


