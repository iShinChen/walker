Ext.namespace("com.walker.system");
// 系统菜单开始-----------------
com.walker.system.tree = new Ext.Panel({
	title : '系统菜单',
	region : "west",
	collapsible : true,
	width : 208,
	layout : "accordion",
	bodyStyle : "display:block;font-weight:bold;letter-spacing:3px;"
});

/**
 * 删除相同名称tab
 */
function delSameNameTab(tabObj, node) {
	var currtitle = null;
	var currid = null;
	currtitle = node.attributes.text;
	currid = node.attributes.id;
	tabObj.items.each(function(item) {
		var title = item.title;
		var id = item.id;
		if (item.closable && title == currtitle && id != currid) {
			tabObj.remove(item, true);
		}
	});
}
/**
 * 删除相同名称tab方法2(不要删我)
 */
com.walker.system.delSameNameTab2 = function(tabObj, currid, currtitle) {
	if (tabObj != null && currid != null && currtitle != null) {
		tabObj.items.each(function(item) {
			var title = item.title;
			var id = item.id;
			if (item.closable && title == currtitle && id == currid) {
				tabObj.remove(item, true);
			}
		});
	}
};

// 插件定义-----------------------------------------------------
Ext.ux.TabCloseMenu = function() {

	var tabs, menu, ctxItem;

	this.init = function(tp) {
		tabs = tp;
		tabs.on('contextmenu', onContextMenu);
	};

	function onContextMenu(ts, item, e) {

		if (!menu) { // create context menu on first right click
			menu = new Ext.menu.Menu([ {
				id : tabs.id + '-close',
				text : '关闭',
				handler : function() {
					tabs.remove(ctxItem, false);
				}
			}, {
				id : tabs.id + '-close-others',
				text : '关闭其他',
				handler : function() {
					tabs.items.each(function(item) {
						if (item.closable && item != ctxItem) {
							tabs.remove(item, false);
						}
					});
				}
			}, {

				id : tabs.id + '-close-all',

				text : '关闭所有',

				handler : function() {
					tabs.items.each(function(item) {
						if (item.closable) {
							tabs.remove(item, false);
						}
					});
				}
			} ]);
		}

		ctxItem = item;

		var items = menu.items;

		items.get(tabs.id + '-close').setDisabled(!item.closable);

		var disableOthers = true;

		tabs.items.each(function() {

			if (this != item && this.closable) {

				disableOthers = false;

				return false;

			}

		});

		items.get(tabs.id + '-close-others').setDisabled(disableOthers);

		var disableAll = true;

		tabs.items.each(function() {

			if (this.closable) {

				disableAll = false;

				return false;

			}

		});

		items.get(tabs.id + '-close-all').setDisabled(disableAll);
		menu.showAt(e.getPoint());
	}

};

// 插件定义-----------------------------------------------------
com.walker.system.tabPanel = new Ext.TabPanel(
		{
			region : "center",
			plit : true,
			border : false,
			id : "main",
			enableTabScroll : true,
			deferredRender : false,
			activeTab : 0,
			minTabWidth : 159,
			resizeTabs : true,
			plugins : new Ext.ux.TabCloseMenu(),
			items : [ {
				title : '系统首页',
				layout : 'fit',
				html : '<iframe name="welcome" style="border:0" width=100% height=100% src="welcome.jsp" />'
			} ],
			listeners : {
				'tabchange' : function(t, nowTab) {
					// 切换TAB时，重新加载相应表格数据
					if (nowTab.title == '系统首页') {
						var dbsy = window.frames['welcome'];
					}
				}
			}
		});

com.walker.system.topTap = function() {
	var p = new Ext.Panel({
		region : "north",
		height : 65,
		resizable : false,
		bodyStyle : "background: #fff;",
		layout : 'border',
		items : [ {
			id : 'item_cy_logo',
			xtype : 'panel',
			region : 'west',
			border : false,
			width : 350
		}, {
			xtype : 'panel',
			region : 'center',
			border : false
		}, {
			xtype : 'panel',
			region : 'east',
			id : 'logininfo',
			width : 150,
			border : false,
			items : [ {
				layout : "column",
				border : false,
				items : [ {
					xtype : "displayfield",
					value : '欢迎您!',
					width : 50
				}, {
					id : "username",
					xtype : "displayfield",
					width : 60
				}, {
					id : "loginout",
					xtype : "displayfield",
					value : '退出',
					width : 40
				} ]
			} ],
			listeners : {
				afterrender : function() {
					Ext.Ajax.request({
						url : "/walker/user/getUserSession",
						success : function(response, options) {
							var data = Ext.decode(response.responseText);
							jQuery("#username").append(data.USER_NAME);
						}
					});
					
					jQuery("#loginout").click(function() {
						Ext.Msg.confirm('提示',"确定退出本平台吗?", function(btn) {
							if(btn == 'yes'){
								Ext.Ajax.request({
									url : '/walker/user/loginOut',
									success : function(response){
										var result = Ext.util.JSON.decode(response.responseText);
										if(result.success)
										{
											top.location.reload();
										}
									}
								});
							}
						});
					});
				}
			}
		} ]
	});
	return p;
};

function getHiddenFrame() {
	var id = Ext.id();
	var frame = document.createElement('iframe');
	frame.id = id;
	frame.name = id;
	frame.className = 'x-hidden';
	if (Ext.isIE) {
		frame.src = Ext.SSL_SECURE_URL;
	}
	document.body.appendChild(frame);
	if (Ext.isIE) {
		document.frames[id].name = id;
	}
	return frame;
}

com.walker.system.userpermission = function() {
	//查询请求后台
	Ext.Ajax.request({
		url : 'sysAction_getPermission.do',
		success : com.walker.system.loadPerSuccess
	});
};

com.walker.system.loadPerSuccess = function(response) {
	var result = Ext.util.JSON.decode(response.responseText);
	com.walker.system.permissionStr = result.permission;
};

com.walker.system.viewport  = function (){
	var viewport = new Ext.Viewport({
		enableTabScroll : true,
		layout : "border",
       items:[
            com.walker.system.topTap(),
            com.walker.system.tree,
            com.walker.system.tabPanel
   	   ]
	});
	Ext.Ajax.request({
		url : "/walker/user/getLevelOneMenuList",
		success : function(response, options) {
			var data=Ext.decode(response.responseText);
			for ( var i = 0; i < data.length; i++) {
				var treeContainer = new Ext.Panel({
					title : data[i].MENU_NAME,
					collapsible : true,
					collapsed: true,
					treeRoot: data[i],
					tools: [{
						id: 'refresh',
						handler: function(event, toolEl, panel) {
							if (panel.tree && !panel.collapsed) {
								panel.tree.getRootNode().reload();
							}
						}
					}],
					listeners: {
						'collapse': function(a) {
							if (this.tree) {
								this.tree.hide();
							}
						},
						'expand' : function(a){
							if (!this.tree) {
								this.tree = new Ext.tree.TreePanel(
								{
									id: 'left-tree' + this.treeRoot.ID,
									rootVisible : false,
									autoHeight: false,
									autoScroll: true,
									border: false,
									root: new Ext.tree.AsyncTreeNode({  
								        text : "根",
								        id : this.treeRoot.ID,  
								        expanded : false
								    }),
									loader : new Ext.tree.TreeLoader( {
										dataUrl : '/walker/user/getLevelTwoMenuList',
										listeners : {
											'beforeload' : function(loader, node) {
												var pid = node.attributes.id;
												this.baseParams = {
													'trId' : pid
												};
											}
										}
									}),
									listeners: {
										'click': function(node) {
											if ('null' == node.attributes.url || node.attributes.url == '') {
												return;
											}
											delSameNameTab(com.walker.system.tabPanel, node);

											var n;
											n = com.walker.system.tabPanel.getComponent(node.id);
											if (!n) {
												var htmlStr = '<iframe style="border:0" width=100% height=100% src='
													+ node.attributes.url + '?currtitle='
													+ encodeURIComponent(node.text) + '&currid=' + node.id
													+ ' />';

												n = com.walker.system.tabPanel
													.add({
													id : node.id,
													title : node.text,
													// hideMode: 'offsets',// 可以解决快速点击多个菜单出现的js问题，但是点击过的菜单会有空白
													html : htmlStr,
													closable : true
												});
												com.walker.system.tabPanel.setActiveTab(n);
											} else {
												n.setTitle(node.text);
												com.walker.system.tabPanel.setActiveTab(node.id);
											}
										}
									}
								});
								this.add(this.tree);
							}
							this.tree.show();
						}
					}
				});
				
				com.walker.system.tree.add(treeContainer);
			}
			com.walker.system.tree.doLayout();
			if (data.length > 0) {
				com.walker.system.tree.items.get(0).expand();
			}
		}
	});
};
