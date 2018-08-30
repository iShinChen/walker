package com.springmvc.walker.controller.customer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileName {  
    
	
	
    /**根据文件名和上级目录路径，查找指定文件的绝对路径 
     * @param fileName 
     * @param fileSaveRootPath 
     * @return 文件绝对路径 
     * @throws BusinessException 
     * @author Guan 
     */  
    public String findFilePath(String fileName, String fileSaveRootPath) {  
        List<String> pathList = new ArrayList<String>();  
        researchfile(new File(fileSaveRootPath),fileName,pathList);  
        String path = null;  
        if(pathList.size()>=1)path = pathList.get(0);  
        return path;  
    }  
      
      
      
    /**根据文件名和上级目录，查找所有该文件的绝对路径 
     * @param file 
     * @param fileName 
     * @param pathList 
     * @author Guan 
     */  
    public static void researchfile(File file,String fileName,List<String> pathList) {    
        if (file.isDirectory()) {    
            File[] filearry = file.listFiles();   
            for (File f : filearry) {    
                researchfile(f,fileName,pathList);  
            }    
        }else{  
            if(file.getName().equals(fileName)){  
                pathList.add(file.getAbsolutePath());  
            }  
        }  
    }   
    
    private static void test(String fileDir) {  
        List<File> fileList = new ArrayList<File>();  
        File file = new File(fileDir);  
        File[] files = file.listFiles();// 获取目录下的所有文件或文件夹  
        if (files == null) {// 如果目录为空，直接退出  
            return;  
        }  
        // 遍历，目录下的所有文件  
        for (File f : files) {  
            if (f.isFile()) {  
                fileList.add(f);  
            } else if (f.isDirectory()) {  
//                System.out.println(f.getAbsolutePath());  
                test(f.getAbsolutePath());  
            }  
        }  
        for (File f1 : fileList) {
            System.out.println(fileDir.replace("\\", "/") +"/"+ f1.getName());  
        }
    }
  
    public static void main(String[] args) {  
        /*test("D://ftp"); */ 
    } 
} 
