package com.springmvc.walker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil { 
	
	public static boolean isExcel2003(String name){
		if(name.endsWith(".xls")){
			return true;
		}
		return false;
	}
	
	public static boolean isExcel2007(String name){
		if(name.endsWith(".xlsx")){
			return true;
		}
		return false;
	}
	
	/**
	 * 读取2003版本Excel文件数据
	 * 
	 * @param file File
	 * @return List<String>
	 */
	@SuppressWarnings("deprecation")
	public static List<String> readExcelVer2003(File file){
		List<String> values = new ArrayList<String>();
		try {
			InputStream inputStream = new FileInputStream(file.getAbsolutePath());
	        HSSFWorkbook wb = new HSSFWorkbook(inputStream);
	        HSSFSheet sheet=wb.getSheetAt(0);
	        //得到Excel的行数
	        int rowNo = sheet.getPhysicalNumberOfRows();
	        int celNo = 0;
	        if(rowNo>=1 && sheet.getRow(0) != null){
	        	celNo = sheet.getRow(0).getPhysicalNumberOfCells();
	        }
	        for(int r=1;r<rowNo;r++){
	        	HSSFRow row = sheet.getRow(r);
	        	if (row == null) continue;
	        	
	        	String rowValue = "";
	        	for(int c = 0; c <celNo ; c++){
	        		HSSFCell cell = row.getCell(c);
	        		if (cell != null) {
						switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_FORMULA:
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								if(HSSFDateUtil.isCellDateFormatted(cell)){
									//处理日期类型的数据
									Date date = cell.getDateCellValue();
									DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
									rowValue += formater.format(date) + ",";
								}else{
									//处理整数带小数点的问题
									DecimalFormat format = new DecimalFormat("######");
									rowValue += format.format(cell.getNumericCellValue()) + ",";
//									cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//									rowValue += cell.getStringCellValue() + ",";
								}	
								break;
							case HSSFCell.CELL_TYPE_STRING:
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
		file.delete();
		return values;
	}
    
    /**
	 * 读取2007版本Excel文件数据
	 * 
	 * @param file File
	 * @return List<String>
	 */
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
	        for(int r=1;r<rowNo;r++){
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
		file.delete();
		return values;
	}
	
	public static XSSFWorkbook exportExcelVer2007(List<Map<String, Object>> list){
		// 声明一个工作薄  
        XSSFWorkbook workbook = new XSSFWorkbook();  
        // 生成一个表格  
        XSSFSheet sheet = workbook.createSheet("sheet1");  
        // 设置表格默认列宽度为10个字节  
        sheet.setDefaultColumnWidth((short) 10);  
        // 产生表格标题行  
        XSSFRow row = sheet.createRow(0);
        String[] title = {"序号","姓名","性别","年龄","生日","住址"};
        for(int i = 0;i < 6;i++){
        	XSSFCell cell = row.createCell(i);
        	XSSFRichTextString text = new XSSFRichTextString(title[i]);  
            cell.setCellValue(text);
        }
		//写入数据行
        for(int rowNo=1; rowNo <= list.size();rowNo++ ){
        	row = sheet.createRow(rowNo);
        	int cellNo = 0;
        	//序号
        	XSSFCell cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(rowNo);
        	//姓名
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(list.get(rowNo-1).get("name").toString());
        	//性别
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(list.get(rowNo-1).get("sex").toString());
        	//年龄
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(list.get(rowNo-1).get("age").toString());
        	//生日
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(list.get(rowNo-1).get("birth").toString());
        	//住址
        	cell = row.createCell(cellNo++);
        	cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        	cell.setCellValue(list.get(rowNo-1).get("address").toString());	
        }
        return workbook;
	}
	
}
