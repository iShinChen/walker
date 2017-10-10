Ext.namespace("com.walker.preview");

com.walker.preview.previewInfo=function(gridPnl){
	var rec = com.walker.common.getSelectRecord(gridPnl,true);
	if(rec){
		Ext.Ajax.request({
			url: '/walker/info/getInfoById',
			params : {
				id:rec.get('id')
			},
			success: function(response) {
				var result = Ext.decode(response.responseText);
				if(result.success){
					com.walker.preview.showPreviewInfoWin(result.data.content);
				}else{
					Ext.Msg.alert("提示","获取资讯信息失败!");
				}
		     },
			failure: function(response) {
				Ext.Msg.alert("提示","获取资讯信息失败!");
			}
		});
	}
};

com.walker.preview.previewInfoWin=null;

com.walker.preview.showPreviewInfoWin=function(data){
	com.walker.preview.previewInfoWin=com.walker.preview.createPreviewInfoWin(data);
	com.walker.preview.previewInfoWin.show();
};

com.walker.preview.createPreviewInfoWin=function(data){
	var panelTmp = new Ext.Window({
		layout : 'fit',
		width : 800,
		height : 500,
		modal : true,
		constrain : true,
		maximizable: true,  
		maximized :true,
		closeAction:'close',
		title : '内容预览',
		items : [com.walker.preview.previewInfoPnl(data)]
	});
	return panelTmp;
};

com.walker.preview.previewInfoPnl=function(data){
	var pnl = new Ext.Panel({  
	    border : false,  
	    autoScroll: true,
	    html:data  
	}); 
	return pnl;
};