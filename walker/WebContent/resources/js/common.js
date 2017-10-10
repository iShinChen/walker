Ext.namespace("com.walker.common");

/**
 * 校验表单
 */
com.walker.common.validateForm=function(formPanel){
	var formFlds=[];
	var fn = function(c){
        if(c.doLayout && c != formPanel){
            if(c.items){
                c.items.each(fn);
            }
        }else if(c.isFormField){
            formFlds.push(c);
        }
    }
    formPanel.items.each(fn);
    if (formFlds.length==0) return "";
	var str=[];
	for (var i=0;i<formFlds.length;i++){
		var childTmp=formFlds[i];
		var fieldLabel=childTmp.initialConfig["fieldLabel"];
		if (!childTmp.disabled && childTmp.isVisible() && !childTmp.validate()){
			var b=true;
			for (var j=0;j<str.length;j++){
				if (str[j]==fieldLabel) {
					b=false;
					break;
				}
			}
			if (b) str.push(fieldLabel);
		}
	}
	if (str.length>0) return "有如下字段:"+str.toString()+";\n数据不合法，请查看提示，重新填写！";
	return "";        
};

/**
 * grid 表格对象
 * flag true 单选项;false 多选项
 */
com.walker.common.getSelectRecord = function(grid,flag) {
	if (flag == undefined)
		flag = true;
	if (flag && grid.getSelectionModel().getCount() != 1) {
		Ext.Msg.alert("提示","请选择一条记录!");
		return;
	}
	if (flag) {
		var rec = grid.getSelectionModel().getSelected();
		return rec;
	} else {
		var recs = grid.getSelectionModel().getSelections();
		if (recs.length < 1) {
			Ext.Msg.alert('提示 ', "请至少选择一条记录!");
			return;
		}
		return recs;
	}
};

/**
 * 获取子节点
 */
com.walker.common.getChildNodes = function(parentNode,nodeObj){
	nodeObj.nodeIds += "'"+parentNode.attributes.id +"',"
	var nodes = parentNode.childNodes;
	for(var i=0;i<nodes.length;i++){
		com.walker.common.getChildNodes(nodes[i],nodeObj);
	}
}