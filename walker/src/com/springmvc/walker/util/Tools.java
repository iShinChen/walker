package com.springmvc.walker.util;

import java.awt.FileDialog;
import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Tools {
	public static String getRootPath() {
		String classPath = Tools.class.getClassLoader().getResource("/").getPath();
		
		try {
			classPath = java.net.URLDecoder.decode(classPath,"utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		String rootPath = "";
		// windows下
		if ("\\".equals(File.separator)) {
			rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));
			rootPath = rootPath.replace("/", "\\");
		}
		// linux下
		if ("/".equals(File.separator)) {
			rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
			rootPath = rootPath.replace("\\", "/");
		}
		return rootPath;
	}
	
	@SuppressWarnings("deprecation")
	public static String getLocalPath(){
		JFrame frame = new JFrame();
		FileDialog fileDialog = new FileDialog(frame);  
        fileDialog.show();  
        String filePath = fileDialog.getDirectory();         
        String fileName = fileDialog.getFile();
        JTextField filePathFild = new JTextField();
        
        if(filePath == null  || fileName == null){            
        }else{  
        	  filePathFild.setText(filePath + fileName);  
        }
		return filePath;
	}
	
	public static String getFolderPath(){
		//设置选择器UI
		if(UIManager.getLookAndFeel().isSupportedLookAndFeel()){
			final String platform = UIManager.getSystemLookAndFeelClassName();
			// If the current Look & Feel does not match the platform Look & Feel,
			// change it so it does.
			if (!UIManager.getLookAndFeel().getName().equals(platform)) {
				try {
					UIManager.setLookAndFeel(platform);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
		
		//文件选择器
		JFileChooser fileChooser = new JFileChooser("D://");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(fileChooser);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			return fileChooser.getSelectedFile().getAbsolutePath();//这个就是你选择的文件夹的路径
		}else{
			return null;
		}
	}
}
