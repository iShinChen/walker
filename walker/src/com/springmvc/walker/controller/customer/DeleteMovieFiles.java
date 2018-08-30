package com.springmvc.walker.controller.customer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.springmvc.framework.util.ContinueFTP;


public class DeleteMovieFiles {
	public int number = 0;
	private final static Logger logger = Logger.getLogger(DeleteMovieFiles.class);
	
	public static void main(String[] args) {
		run();
	}
	
	private static void run(){
		String uploadPath = "";
		uploadPath = "D://山东移动/山东移动.xlsx";
		File file = new File(uploadPath);
		List<String> values = readExcelVer2007(file);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = parseXls(values);
		logger.info(list.size());
		String exit = "";
		List<Map<String, Object>> ftpList = ftpServer();
		int firstFtp = 0;
		int secondFtp = 0;
		int thirdFtp = 0;
		int fourthFtp = 0;
	
		int notExit = 0;
		List<Map<String, Object>> excel0 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> excel1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> excel2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> excel3 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> excel4 = new ArrayList<Map<String, Object>>();
		
		List<Map<String, Object>> listTest = new ArrayList<Map<String, Object>>();
		for(int index = 0;index<10;index++){
			listTest.add(list.get(index));
		}
		int total = list.size();
		int progress = 0;
		for(Map<String, Object> movieMap : list){
			exit = deleteFiles("",ftpList,movieMap);
			if("ftp://dev1:123456@192.168.1.254:21".equals(exit)){
				firstFtp++;
				excel1.add(movieMap);
			}else if("ftp://dev2:123456@192.168.1.254:21".equals(exit)){
				secondFtp++;
				excel2.add(movieMap);
			}else if("ftp://dev3:123456@192.168.1.254:21".equals(exit)){
				thirdFtp++;
				excel3.add(movieMap);
			}else if("ftp://dev1:123456@192.168.1.252:21".equals(exit)){
				fourthFtp++;
				excel4.add(movieMap);
			}else{
				notExit++;
				excel0.add(movieMap);
			}
			progress++;
			logger.info("检索进度：" + progress + "/" + total);
		}
		
		writeToExcel("D://山东移动/山东HD1.xlsx",excel1);
		writeToExcel("D://山东移动/山东HD2.xlsx",excel2);
		writeToExcel("D://山东移动/山东HD3.xlsx",excel3);
		writeToExcel("D://山东移动/山东HD4.xlsx",excel4);
		writeToExcel("D://山东移动/不存在.xlsx",excel0);
		
		logger.info("检索到存在硬盘1的片源的个数为：" + firstFtp);
		logger.info("检索到存在硬盘2的片源的个数为：" + secondFtp);
		logger.info("检索到存在硬盘3的片源的个数为：" + thirdFtp);
		logger.info("检索到存在硬盘4的片源的个数为：" + fourthFtp);
		logger.info("检索到不存在的片源的个数为：" + notExit);
	}
	
	private static void  writeToExcel(String fileName,List<Map<String, Object>> excel){
		XSSFWorkbook workbook = new XSSFWorkbook();
		File file1 = new File(fileName);
		workbook = exportExcelVer2007(workbook,excel);
		try {
			OutputStream out =  new FileOutputStream(file1);
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> readExcelVer2007(File file){
		List<String> values = new ArrayList<String>();
		try {
			InputStream inputStream = new FileInputStream(file.getAbsolutePath());
	        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
	        XSSFSheet sheet=wb.getSheetAt(0);
	        //得到Excel的行数
	        int rowNo = sheet.getPhysicalNumberOfRows();
	        int celNo = 0;
	        if(rowNo>=1 && sheet.getRow(0) != null){
	        	celNo = sheet.getRow(0).getPhysicalNumberOfCells();
	        }
	        for(int r=0;r<rowNo;r++){
	        	XSSFRow row = sheet.getRow(r);
	        	if (row == null) continue;
	        	
	        	String rowValue = "";
	        	for(int c = 0; c <celNo ; c++){
	        		XSSFCell cell = row.getCell(c);
	        		if (cell != null) {
						switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_FORMULA:
								break;
							case XSSFCell.CELL_TYPE_NUMERIC:
								if(DateUtil.isCellDateFormatted(cell)){
									//处理日期类型的数据
									Date date = cell.getDateCellValue();
									DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
									rowValue += formater.format(date) + ",";
								}else{
									//处理整数带小数点的问题
									cell.setCellType(XSSFCell.CELL_TYPE_STRING);
									rowValue += cell.getStringCellValue() + ",";
								}	
								break;
							case XSSFCell.CELL_TYPE_STRING:
								rowValue += cell.getStringCellValue() + ",";
								break;
							default:
								rowValue += ",";
								break;
						}
					}else{
						rowValue +=",";
					}
	        	}
	        	//行读取结束flag
	        	rowValue +="end";
	        	values.add(rowValue);
	        }
	        inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			values = null;
		}
		return values;
	}
	
	private static List<Map<String, Object>> parseXls(List<String> values) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(null == values) return list;
		
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
				continue;
			}
			//数据映射
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("seriesId", val[0]);
			paraMap.put("seriesName", val[1]);
			paraMap.put("programName", val[2]);
			paraMap.put("FileURL", val[3]);	
			paraMap.put("type", val[4]);	
			paraMap.put("programNo", val[5]);	
			list.add(paraMap);
	    }
		return list;
	}
	
	private static List<Map<String, Object>> ftpServer(){
		List<Map<String, Object>> ftpList = new ArrayList<Map<String, Object>>();
		Map<String, Object> ftpMessage =  new HashMap<String, Object>();
//		
//		//dev7-252
//		ftpMessage =  new HashMap<String, Object>();
//		ftpMessage.put("IP", "192.168.1.252");
//		ftpMessage.put("PORT", "21");
//		ftpMessage.put("USER", "dev7");
//		ftpMessage.put("PASSWORD", "123456");
//		ftpMessage.put("FTP_ADDR", "ftp://dev7:123456@192.168.1.252:21");
//		ftpList.add(ftpMessage);
		
//		//dev4-254
//		ftpMessage =  new HashMap<String, Object>();
//		ftpMessage.put("IP", "192.168.1.254");
//		ftpMessage.put("PORT", "21");
//		ftpMessage.put("USER", "dev4");
//		ftpMessage.put("PASSWORD", "123456");
//		ftpMessage.put("FTP_ADDR", "ftp://dev4:123456@192.168.1.254:21");
//		ftpList.add(ftpMessage);
//		
//		//dev5-254
//		ftpMessage =  new HashMap<String, Object>();
//		ftpMessage.put("IP", "192.168.1.254");
//		ftpMessage.put("PORT", "21");
//		ftpMessage.put("USER", "dev5");
//		ftpMessage.put("PASSWORD", "123456");
//		ftpMessage.put("FTP_ADDR", "ftp://dev5:123456@192.168.1.254:21");
//		ftpList.add(ftpMessage);
		
		//dev1-254
		ftpMessage =  new HashMap<String, Object>();
		ftpMessage.put("IP", "192.168.1.254");
		ftpMessage.put("PORT", "21");
		ftpMessage.put("USER", "dev1");
		ftpMessage.put("PASSWORD", "123456");
		ftpMessage.put("FTP_ADDR", "ftp://dev1:123456@192.168.1.254:21");
		ftpList.add(ftpMessage);
		
		//dev2-254
		ftpMessage =  new HashMap<String, Object>();
		ftpMessage.put("IP", "192.168.1.254");
		ftpMessage.put("PORT", "21");
		ftpMessage.put("USER", "dev2");
		ftpMessage.put("PASSWORD", "123456");
		ftpMessage.put("FTP_ADDR", "ftp://dev2:123456@192.168.1.254:21");
		ftpList.add(ftpMessage);
		
		//dev3-254
		ftpMessage =  new HashMap<String, Object>();
		ftpMessage.put("IP", "192.168.1.254");
		ftpMessage.put("PORT", "21");
		ftpMessage.put("USER", "dev3");
		ftpMessage.put("PASSWORD", "123456");
		ftpMessage.put("FTP_ADDR", "ftp://dev3:123456@192.168.1.254:21");
		ftpList.add(ftpMessage);
		
		//dev1-252
		ftpMessage =  new HashMap<String, Object>();
		ftpMessage.put("IP", "192.168.1.252");
		ftpMessage.put("PORT", "21");
		ftpMessage.put("USER", "dev1");
		ftpMessage.put("PASSWORD", "123456");
		ftpMessage.put("FTP_ADDR", "ftp://dev1:123456@192.168.1.252:21");
		ftpList.add(ftpMessage);

		return ftpList;
	}
	
	private static String deleteFiles(String exit,List<Map<String, Object>> ftpList,Map<String, Object> movieMap){
		String FTP_ADDR = "";
		for(Map<String, Object> ftpMap : ftpList){
			ContinueFTP ftp = new ContinueFTP();
			boolean connected = false;
			try {
				connected = ftp.connect(String.valueOf(ftpMap.get("IP")),Integer.valueOf(String.valueOf(ftpMap.get("PORT"))),String.valueOf(ftpMap.get("USER")),String.valueOf(ftpMap.get("PASSWORD")));
				if(connected && ftp.isFileExists(String.valueOf(movieMap.get("FileURL"))))
				{
					FTP_ADDR = String.valueOf(ftpMap.get("FTP_ADDR"));
					logger.info(FTP_ADDR + movieMap.get("FileURL") + "存在！");
					logger.info("文件存在，所在ftp为："+ FTP_ADDR);
//					Boolean flag = ftp.deleteFile(movieMap.get("FileURL").toString());
//					logger.info("删除文件:"+movieMap.get("programName") + flag);
//					exit++;
					exit = FTP_ADDR;
					break;
				}else{
					logger.info(String.valueOf(ftpMap.get("FTP_ADDR")) + movieMap.get("FileURL") + "不存在！");
					exit = "";
					continue;
				}
			} catch (Exception e) {
				logger.error("FTP连接出错" ,e);
			} finally {
				try {
					ftp.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return exit;
	}
	
	public static XSSFWorkbook exportExcelVer2007(XSSFWorkbook workbook,List<Map<String, Object>> list){ 
        // 生成一个表格  
        XSSFSheet sheet = workbook.createSheet("sheet1");  
        // 设置表格默认列宽度为10个字节  
        sheet.setDefaultColumnWidth((short) 10);  
        // 产生表格标题行  
        XSSFRow row = sheet.createRow(0);
        if(null == list){
        	return workbook;
        }
		//写入数据行
        for(int rowNo=0; rowNo < list.size();rowNo++ ){
        	row = sheet.createRow(rowNo);
        	int cellNo = 0;
        	//合集ID
        	XSSFCell cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(checkNullOrEmpty(list.get(rowNo).get("seriesId")));
        	//合集名称
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(checkNullOrEmpty(list.get(rowNo).get("seriesName")));
        	//分集名称
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(checkNullOrEmpty(list.get(rowNo).get("programName")));
        	//文件路径
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(checkNullOrEmpty(list.get(rowNo).get("FileURL")));
        	//类型
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(checkNullOrEmpty(list.get(rowNo).get("type")));
        	//分集号
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(checkNullOrEmpty(list.get(rowNo).get("programNo")));
        }
        return workbook;
	}
	
	private static String checkNullOrEmpty(Object obj){
		if(null != obj){
			return obj.toString();
		}
		return "";
	}
}
