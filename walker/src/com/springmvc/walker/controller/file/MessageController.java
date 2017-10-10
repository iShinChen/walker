package com.springmvc.walker.controller.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.springmvc.walker.constant.GlobalConstant;
import com.springmvc.walker.entity.Page;
import com.springmvc.walker.service.FileService;
import com.springmvc.walker.service.ImportExcelService;
import com.springmvc.walker.service.MessageService;
import com.springmvc.walker.util.ContinueFTP;
import com.springmvc.walker.util.ExcelUtil;
import com.springmvc.walker.util.ParamUtil;
import com.springmvc.walker.util.StringUtils;

@Controller
@RequestMapping("/file") 
public class MessageController {
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(MessageController.class);
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private ImportExcelService importExcelService;
	
	@Autowired
	private FileService fileService;
	
	/**
	 * 获取信息列表
	 */
	@RequestMapping(value = "/getMessageListPage")
	public void getMessageListPage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer = null;
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		String[] fileds = { "start", "limit","name"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			
			Page page = new Page();
			List<Map<String, Object>> list = messageService.getMessagePage(paraMap, page);
			jsonMap.put("total",page.getTotalRow());
			jsonMap.put("rows",list);
			jsonMap.put("page",page.getPageRow());
			jsonMap.put("success", true);
			
		} catch (Exception e) {
			logger.error(e);
			jsonMap.put("success", false);
		}
		
		writer.write(JSONObject.toJSONString(jsonMap));
		writer.flush();
		writer.close();
	}
	
	@RequestMapping(value = "/getMessageById")
	public void getMessageById(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String id = request.getParameter("id");
		try {
			writer = response.getWriter();
			Map<String, Object> resultMap = messageService.getMessageById(id);
			jsonObj.put("data", resultMap);
			jsonObj.put("success", true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			jsonObj.put("error", e);
		} finally {
			writer.write(jsonObj.toString());
			writer.flush();
			writer.close();
		}	
	}
	
	/**
	 * 保存信息
	 */
	@RequestMapping(value = "/saveMessage")
	public void saveMessage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String[] fileds = {"id","version","name","age","sex","birth","address"};
		try {
			writer = response.getWriter();
			Map<String, Object> paraMap = ParamUtil.getParamMap(request, fileds);
			messageService.saveMessage(paraMap);
			jsonObj.put("success", true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			jsonObj.put("success", false);
		} finally {
			writer.write(jsonObj.toString());
			writer.flush();
			writer.close();
		}
	}
	
	/**
	 * 删除信息
	 */
	@RequestMapping(value = "/deleteMessage")
	public void deleteMessage(HttpServletRequest request,HttpServletResponse response) {
		PrintWriter writer=null;
		JSONObject jsonObj = new JSONObject();
		String ids = request.getParameter("ids");
		try {
			writer = response.getWriter();
			messageService.deleteMessage(ids);
			jsonObj.put("success", true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			jsonObj.put("error", e);
		} finally {
			writer.write(jsonObj.toString());
			writer.flush();
			writer.close();
		}	
	}
	
	@RequestMapping(value = "/importExcel")
	public void importExcel(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="filePath") MultipartFile mfile){

  		JSONObject jsonObj = new JSONObject();
  		PrintWriter writer=null;
  		response.setContentType("text/html;charset=utf-8");
  		
  		String uploadPath = request.getSession().getServletContext().getRealPath("/upload/");
  		File url = new File(uploadPath);
  		if (!url.exists()) {
  			url.mkdirs();
		}
		String name = mfile.getOriginalFilename();
        CommonsMultipartFile cf= (CommonsMultipartFile)mfile;
        File file = new File(uploadPath+"/"+name);

		try {
			cf.getFileItem().write(file); 
			writer = response.getWriter();
			//判断并解析excel文件
			List<String> values = new ArrayList<String>();
			if(ExcelUtil.isExcel2003(name)){
				values = ExcelUtil.readExcelVer2003(file);
			}else if(ExcelUtil.isExcel2007(name)){
				values = ExcelUtil.readExcelVer2007(file);
			}
			//导入数据库
	        importExcelService.insertIntoTMessage(values);
	        jsonObj.put("success", true);
		}catch(IOException e){
			logger.error("程序异常", e);
			jsonObj.put("success", false);
		}catch (Exception e){
			logger.error("程序异常", e);
			jsonObj.put("success", false);
		}finally {
			writer.write(jsonObj.toString());
			writer.flush();
			writer.close();
		}
  		
	}
	
	@RequestMapping(value = "/exportExcel")
	public void exportExcel(HttpServletRequest request,HttpServletResponse response){
		JSONObject jsonObj = new JSONObject();
  		PrintWriter writer=null;
  		String ids = request.getParameter("ids");
  		try {  	  
			writer = response.getWriter();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = messageService.getMessageByIds(ids);
			if(null!= list){
				XSSFWorkbook workbook = ExcelUtil.exportExcelVer2007(list);
				String fileName = "";
				//连接ftp
				ContinueFTP ftp = new ContinueFTP();
				boolean connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
						Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
						GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
						GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
				boolean flag = false;
				if(connected){
					fileName =  GlobalConstant.SYS_MAP.get(GlobalConstant.EXPORT_EXCEL) 
							+ StringUtils.getYearMonth() + "/" + StringUtils.getDateTime()+ ".xlsx";
					flag = ftp.exportExcel(fileName, workbook);
					if(flag){
						Map<String, Object> paraMap = new HashMap<String, Object>();
						paraMap.put("name", fileName.substring(fileName.lastIndexOf("/")+ 1 , fileName.length()));
						paraMap.put("type", "xlsx");
						paraMap.put("url", fileName);
						paraMap.put("file_size", "100");
						fileService.saveFile(paraMap);
					}	
				}
				ftp.disconnect();
				jsonObj.put("success", flag);
			}
		} catch (IOException e) {
			e.printStackTrace();
			jsonObj.put("success", false);
		}
  		writer.write(jsonObj.toString());
		writer.flush();
		writer.close();
	}
	
	@RequestMapping(value = "/exportPDF")
	public void exportPDF(HttpServletRequest request,HttpServletResponse response){
		JSONObject jsonObj = new JSONObject();
  		PrintWriter writer=null;
  		String id = request.getParameter("id");
  		try {
  			writer = response.getWriter();
  			ContinueFTP ftp = new ContinueFTP();
  			String fileName = "";
			boolean connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
					Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));
			
			PdfReader reader = new PdfReader("D://ftp/export/pdf_model/调查兵团.pdf"); 			
	  		
  			ByteArrayOutputStream[] bos = new ByteArrayOutputStream[1];
  			bos[0] = new ByteArrayOutputStream();
  			PdfStamper ps = new PdfStamper(reader, bos[0]);
  			AcroFields fields = ps.getAcroFields();
  			Map<String, Object> resultMap = messageService.getMessageById(id);
  			if("1".equals(resultMap.get("sex"))){
  				resultMap.put("sex", "男");
  			}else if("2".equals(resultMap.get("sex"))){
  				resultMap.put("sex", "女");
  			}
  			//遍历结果集并将对象映射到PDF的相应域中
  			for(String key: resultMap.keySet()){
  				String value =  resultMap.get(key).toString();
  				fields.setField(key, value);
  			}
  			ps.setFormFlattening(true);
  			ps.close();
  			
  			boolean flag = false;
			if(connected){
				fileName =  GlobalConstant.SYS_MAP.get(GlobalConstant.EXPORT_PDF) 
						+ StringUtils.getYearMonth() + "/" + resultMap.get("name") + ".pdf";
			}
			flag = ftp.exportPDF(fileName, bos, 1);
			if(flag){
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("name", fileName.substring(fileName.lastIndexOf("/")+ 1 , fileName.length()));
				paraMap.put("type", "pdf");
				paraMap.put("url", fileName);
				paraMap.put("file_size", "100");
				fileService.saveFile(paraMap);
			}
			ftp.disconnect();
  			jsonObj.put("success", flag);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObj.put("success", false);
		}
  		writer.write(jsonObj.toString());
		writer.flush();
		writer.close();
	}
	
	@RequestMapping(value = "/exportListPDF")
	public void exportListPDF(HttpServletRequest request,HttpServletResponse response){
		JSONObject jsonObj = new JSONObject();
  		PrintWriter writer=null;
  		String ids = request.getParameter("ids");
  		try {
  			writer = response.getWriter();
  			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = messageService.getMessageByIds(ids);
  			String[] id = ids.split(",");
  			int pageNo = 0;
			if (id.length >= 2 && id.length % 2 == 0) {  
				pageNo = id.length / 2;  
            } else {  
            	pageNo = id.length / 2 + 1;  
            }
			String fileName = "";
			ContinueFTP ftp = new ContinueFTP();
			boolean connected = ftp.connect(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP),
					Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT)),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER),
					GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD));

			//用于存储每页生成PDF流
			ByteArrayOutputStream bos[] = new ByteArrayOutputStream[pageNo];
			//向PDF模板中插入数据
			int lineNo = 0;
			for(int item = 0; item < pageNo; item++){
				bos[item] = new ByteArrayOutputStream();
				PdfReader reader = new PdfReader("D://ftp/export/pdf_model/统计表.pdf");
//				PdfReader reader = ftp.readModelPDF(modelPDF);
				PdfStamper ps = new PdfStamper(reader, bos[item]);
				AcroFields fields = ps.getAcroFields();
				fields.setField("currentPage", String.valueOf(item+1));
				fields.setField("totalPage", String.valueOf(pageNo));
				for(int pageLine = 0; pageLine<2;pageLine++){
					if(lineNo >= id.length) break;
					for(String key: list.get(lineNo).keySet()){
		  				String value =  list.get(lineNo).get(key).toString();
		  				fields.setField(key+pageLine, value);
		  			}
					lineNo++;
				}
				ps.setFormFlattening(true);
	  			ps.close();
			}
			boolean flag = false;
			if(connected){
				fileName =  GlobalConstant.SYS_MAP.get(GlobalConstant.EXPORT_PDF) 
						+ StringUtils.getYearMonth() + "/" + "统计表" + StringUtils.getDateTime() +".pdf";
			}
			flag = ftp.exportPDF(fileName, bos, pageNo);
			if(flag){
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("name", fileName.substring(fileName.lastIndexOf("/")+ 1 , fileName.length()));
				paraMap.put("type", "pdf");
				paraMap.put("url", fileName);
				paraMap.put("file_size", "100");
				fileService.saveFile(paraMap);
			}	
			ftp.disconnect();
  			jsonObj.put("success", flag);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObj.put("success", false);
		}
  		writer.write(jsonObj.toString());
		writer.flush();
		writer.close();
	}
}
