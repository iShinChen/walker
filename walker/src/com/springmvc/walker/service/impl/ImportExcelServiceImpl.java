package com.springmvc.walker.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springmvc.walker.service.ImportExcelService;
import com.springmvc.walker.service.MessageService;

@Service("importExcelService")
public class ImportExcelServiceImpl implements ImportExcelService{

	@Autowired
    private MessageService messageService;
	
	/**
	 * 将数据插入到mysql数据表中(t_message)
	 * 
	 * @param value String
	 * @param jsonObj JSONObject
	 * @return JSONObject
	 */
	@Override
	public void insertIntoTMessage(List<String> values) {
		if(null == values) return;
		for(int i = 0;i < values.size();i++){
	    	String[] val = values.get(i).split(",");
			//判断该行数据是否为空
			boolean spaceFlag = true;
			for(int index =1;index <val.length-1;index++ ){
				if(null!=val[index] && !val[index].isEmpty()){
					spaceFlag = false;	
				}
			}
			if(spaceFlag){
				return;
			}
			//数据映射
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("name", val[1]);
			if("男".equals(val[2])){
				paraMap.put("sex", "1");
			}else if("女".equals(val[2])){
				paraMap.put("sex", "2");
			}
			paraMap.put("age", val[3]);
			paraMap.put("birth", val[4]);
			paraMap.put("address", val[5]);
			messageService.saveMessage(paraMap);		
	    }
	}

}
