package com.springmvc.walker.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springmvc.walker.entity.Page;
import com.springmvc.walker.mapper.file.MessageMapper;
import com.springmvc.walker.service.MessageService;
import com.springmvc.walker.util.UUIDUtil;

@Service("messageService")
public class MessageServiceImpl implements MessageService{
	
	private final static Logger logger = Logger.getLogger(MessageServiceImpl.class);
	
	@Resource
	private MessageMapper messageMapper;

	@Override
	public List<Map<String, Object>> getMessagePage(Map<String, Object> paraMap, Page page) {
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			int count = messageMapper.getMessageCount(paraMap);
			page.setTotalRow(count);
			page.setCurrPage(Integer.parseInt(paraMap.get("start").toString()));
			page.setPageRow(Integer.parseInt(paraMap.get("limit").toString()));
			
			paraMap.put("start", page.getCurrPage());
			paraMap.put("limit", page.getPageRow());
			
			logger.info("分页查询信息集合START");
			list = messageMapper.getMessagePage(paraMap);
			logger.info("分页查询信息集合SUCCESS,共"+list.size()+"条记录");
		} catch (Exception e) {
			logger.error("查询信息集合发生异常", e);
		}
		return list;
	}

	@Override
	@Transactional
	public int saveMessage(Map<String, Object> paraMap) {
		int result = 0;
		if(null == paraMap){
			return result;
		}
		
		if(null != paraMap.get("id") && !"".equals(paraMap.get("id"))){
			logger.info("更新信息数据"+paraMap.toString());
			result = messageMapper.updateMessage(paraMap);
			logger.info("更新信息数据成功");
		}else{
			paraMap.put("id", UUIDUtil.getUUID());
			logger.info("插入信息数据"+paraMap.toString());
			result = messageMapper.insertMessage(paraMap);
			logger.info("插入信息数据成功");
		}
		return result;
		
	}

	@Override
	public void deleteMessage(String ids) {
		try {
			if(null != ids && !"".equals(ids)){
				logger.info("逻辑删除信息数据");
				String[] id = ids.split(",");
				messageMapper.deleteMessage(id);
				logger.info("删除数据成功");
			}else{
				logger.info("物理删除信息数据");
				String[] id = ids.split(",");
				messageMapper.deleteCompletely(id);
				logger.info("删除数据成功");
			}
		} catch (Exception e) {
			logger.error("操作发生异常", e);
		}
	}

	@Override
	public Map<String, Object> getMessageById(String id) {
		Map<String, Object> message = new HashMap<String, Object>();
		try {
			logger.info("查询单条信息id:" + id);
			message = messageMapper.getMessageById(id);
			logger.info("查询单条信息成功:" + message.toString());
		} catch (Exception e) {
			logger.error("查询单条信息发生异常", e);
			message = null;
		}
		return message;
	}

	@Override
	public List<Map<String, Object>> getMessageByIds(String ids) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			logger.info("查询多条信息id:" + ids);
			String[] id = ids.split(",");
			list = messageMapper.getMessageByIds(id);
			logger.info("查询多条信息成功:" + list.toString());
		} catch (Exception e) {
			logger.error("查询单条信息发生异常", e);
			list = null;
		}
		return list;
	}

}
