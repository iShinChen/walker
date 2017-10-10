package com.springmvc.walker.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.springmvc.walker.constant.DownloadStatus;
import com.springmvc.walker.constant.GlobalConstant;
import com.springmvc.walker.constant.MappingConstant;
import com.springmvc.walker.constant.UploadStatus;

public class ContinueFTP {
	private final static Logger logger = Logger.getLogger(ContinueFTP.class);

	public FTPClient ftpClient = new FTPClient(); 
	public FTPClient targetClient = new FTPClient();
	
	private boolean downloading = true;
    
    public ContinueFTP(){  
    }  
    
    public void stopDownLoad() {
    	this.downloading = false;
    	try {
			this.disconnect();
			this.disconnectTarget();
		} catch (IOException e) {
		}
    }
      
    /** 
     * 连接到FTP服务器 
     * @param hostname 主机名 
     * @param port 端口 
     * @param username 用户名 
     * @param password 密码 
     * @return 是否连接成功 
     * @throws IOException 
     */  
    public boolean connect(String hostname,int port,String username,String password) throws IOException{  
        ftpClient.connect(hostname, port);  
        ftpClient.setControlEncoding("GBK");  
        if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
            if(ftpClient.login(username, password)){  
                return true;  
            }  
        }  
        disconnect();  
        return false;  
    }  
    
    
    /** 
     * 连接到FTP服务器 
     * @param hostname 主机名 
     * @param port 端口 
     * @param username 用户名 
     * @param password 密码 
     * @return 是否连接成功 
     * @throws IOException 
     */  
    public boolean connectTarget(String hostname,int port,String username,String password) throws IOException{  
    	targetClient.connect(hostname, port);  
    	targetClient.setControlEncoding("GBK");  
        if(FTPReply.isPositiveCompletion(targetClient.getReplyCode())){  
            if(targetClient.login(username, password)){  
                return true;  
            }  
        }
        disconnectTarget();
        return false;  
    }  
    
    /**
     * 从远程FTP下载文件到目标FTP
     * 
     * @param name
     * @param movieId
     * @param fileUrl
     * @return
     * @throws IOException
     */
    public DownloadStatus downloadFromRemotetoRemote(String name, String movieId, String fileUrl) throws IOException{  
    	
    	//设置被动模式  
    	ftpClient.enterLocalPassiveMode();
    	targetClient.enterLocalPassiveMode();
    	
    	InputStream in = null;
    	OutputStream out = null;
    	
    	String filePath = fileUrl.substring(0, fileUrl.lastIndexOf("/"));
    	String fileName= fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length());
    	boolean changeWdFlag = false;
    	
        try {
	    	//设置以二进制流的方式传输  
	    	ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	    	targetClient.setFileType(FTP.BINARY_FILE_TYPE);
	    	targetClient.setControlEncoding("GBK");
	    	
	    	//检查远程文件是否存在  
	        FTPFile[] fromFiles = ftpClient.listFiles(new String(fileUrl.getBytes("GBK"),"iso-8859-1"));
	        if(fromFiles.length != 1){  
	        	boolean chgFlag = ftpClient.changeWorkingDirectory(new String(filePath.getBytes("GBK"),"iso-8859-1"));
	        	if(chgFlag)
	        	{
	        		fromFiles = ftpClient.listFiles(new String(fileName.getBytes("GBK"),"iso-8859-1"));  
		        	if(fromFiles.length == 1)
		        	{
		        		changeWdFlag = true;
		        		logger.info(movieId + "||" + fileUrl + "文件存在,已切换到文件目录");
		        	}else{
		        		logger.info(movieId + "||" + fileUrl + "远程文件不存在");
		        		return DownloadStatus.Remote_File_Noexist;
		        	}
	        	}else{
	        		logger.info(movieId + "||" + fileUrl + "远程文件不存在");
	        		return DownloadStatus.Remote_File_Noexist;
	        	}
	        }
	    	
	        //对远程目录的处理  
	        String remoteFileName = fileUrl;  
	        if(fileUrl.contains("/")){  
	            remoteFileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);  
	            //创建服务器远程目录结构，创建失败直接返回  
	            if(CreateDirecroty(fileUrl, targetClient)==UploadStatus.Create_Directory_Fail){  
	            	return DownloadStatus.Create_Directory_Fail;
	            }
	        }
	        
	        //检查远程是否存在文件  
	        FTPFile[] targetFiles = targetClient.listFiles(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));  
	        
	        // 目标地址已存在文件，进行断点续传
	        if(targetFiles.length == 1)
	        {
	        	logger.info("文件存在，进行断点续传");
	        	long fromSize = fromFiles[0].getSize();
	            long targetSize = targetFiles[0].getSize();
	            logger.info(movieId+"源文件大小："+fromSize);
	            logger.info(movieId+"目标文件大小："+targetSize);
	            
	            MappingConstant.movieSize.put(movieId, targetSize);
	            
	        	if(targetSize >= fromSize)
	        	{
	        		if(targetSize == fromSize) {
	        			logger.info(name+",目标文件等于上游文件，下载中止"); 
	            		return DownloadStatus.Local_Equals_Remote;
	        		}else{
	        			logger.info(name+",目标文件大于上游文件，下载中止"); 
	            		return DownloadStatus.Local_Bigger_Remote;
	        		}
	        	}
	        	
	        	out = targetClient.appendFileStream(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));  
	        	ftpClient.setRestartOffset(targetSize);
	        	
	        	if(changeWdFlag)
	        	{
	        		in= ftpClient.retrieveFileStream(new String(fileName.getBytes("GBK"),"iso-8859-1"));  
	        	}else{
	        		in= ftpClient.retrieveFileStream(new String(fileUrl.getBytes("GBK"),"iso-8859-1"));  
	        	}
	        	
	        	byte[] bytes = new byte[1024];
	        	int c;  
	        	long step = fromSize /100;  
	            long process=targetSize /step; 
	            while((c = in.read(bytes))!= -1){  
	            	if (!downloading) {
	            		return DownloadStatus.Download_Interrupt;
	            	}
	                out.write(bytes,0,c);
	                targetSize+=c;  
	                long nowProcess = targetSize /step;  
	                if(nowProcess > process){  
	                    process = nowProcess;  
	                    if(process % 2 == 0)  
	                    {
	                    	logger.info("续传文件下载进度："+process);
	                    	MappingConstant.DOWNLOADING_PROGRESS.put(movieId, process);
	                    }
	                }  
	            }
	            if(process == 100)
				{
					try {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.flush();
							out.close();
						}
					} catch (IOException e) {
						logger.error("关闭流异常", e);
					}
				}
	            boolean isDo = ftpClient.completePendingCommand();  
	            if(isDo || process == 100){ 
	            	MappingConstant.DOWNLOADING_PROGRESS.remove(movieId);
	                return DownloadStatus.Download_From_Break_Success;  
	            }else {
	            	return DownloadStatus.Download_From_Break_Failed;  
	            }
	        }else{
	        	long fromSize = fromFiles[0].getSize();
	        	long targetSize = 0L;
	        	
	        	MappingConstant.movieSize.put(movieId, targetSize);
	        	
	        	if(changeWdFlag)
	        	{
	        		in= ftpClient.retrieveFileStream(new String(fileName.getBytes("GBK"),"iso-8859-1"));  
	        	}else{
	        		in= ftpClient.retrieveFileStream(new String(fileUrl.getBytes("GBK"),"iso-8859-1"));  
	        	}
	        	
	        	out = targetClient.appendFileStream(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));  
	        	
	        	byte[] bytes = new byte[1024];
	        	long step = fromSize /100; 
	            long process=0;  
	        	int c;
	        	while((c = in.read(bytes))!= -1){
	        		if (!downloading) {
	            		return DownloadStatus.Download_Interrupt;
	            	}
	        		out.write(bytes, 0, c);
	        		targetSize+=c;  
	                long nowProcess = targetSize /step;  
	                if(nowProcess > process){  
	                    process = nowProcess;  
	                    if(process % 2 == 0)  
	                    {
	                    	logger.info("《"+name+"》新文件下载进度："+process);
	                    	MappingConstant.DOWNLOADING_PROGRESS.put(movieId, process);
	                    }
	                }  
	        	}
	        	if(process == 100)
				{
					try {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.flush();
							out.close();
						}
					} catch (IOException e) {
						logger.error("关闭流异常", e);
					}
				}
	            boolean upNewStatus = ftpClient.completePendingCommand();  
	            if(upNewStatus || process == 100){
	            	MappingConstant.DOWNLOADING_PROGRESS.remove(movieId);
	                return DownloadStatus.Download_New_Success;  
	            }else {  
	                return DownloadStatus.Download_New_Failed;  
	            }
	        }
        } catch (IOException e) {
			logger.error("ContinueFTP IO异常",e);
		} finally {
			if(in != null)
			{
				in.close();
			}
			if(out != null)
			{
				out.flush();
				out.close();
			}
		}
        return null;
    }
      
    /** 
     * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报 
     * @param remote 远程文件路径 
     * @param local 本地文件路径 
     * @return 上传的状态 
     * @throws IOException 
     */  
    public DownloadStatus download(String name, String movieId, String remote, String local) throws IOException{  
    	//设置被动模式  
        ftpClient.enterLocalPassiveMode();
        String suffix = remote.substring(remote.lastIndexOf("."));
        InputStream in = null;
        FileOutputStream out = null;
        
        try {
        	//设置以二进制方式传输  
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			
			//检查远程文件是否存在  
	        FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("GBK"),"iso-8859-1"));  
	        if(files.length != 1){  
	            logger.info("远程文件不存在");  
	            return DownloadStatus.Remote_File_Noexist;  
	        }  
	        
	        long lRemoteSize = files[0].getSize();  
	        File f = new File(local+"/"+ name + suffix);
	        
	        //本地存在文件，进行断点下载  
	        if(f.exists()){  
	            long localSize = f.length();  
	            //判断本地文件大小是否大于远程文件大小  
	            if(localSize > lRemoteSize){  
	                logger.info("本地文件大于远程文件，下载中止");  
	                return DownloadStatus.Local_Bigger_Remote;  
	            } 
	            if(localSize == lRemoteSize){  
	                logger.info("本地文件等于远程文件，下载中止");  
	                return DownloadStatus.Local_Equals_Remote;
	            }
	            
	            MappingConstant.movieSize.put(movieId, localSize);
	              
	            //进行断点续传，并记录状态  
	            out = new FileOutputStream(f,true);  
	            ftpClient.setRestartOffset(localSize);  
	            in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"),"iso-8859-1"));  
	            byte[] bytes = new byte[1024];  
	            long step = lRemoteSize /100;  
	            long process=localSize /step;  
	            int c;  
	            while((c = in.read(bytes))!= -1){  
	            	if (!downloading) {
	            		return DownloadStatus.Download_Interrupt;
	            	}
	                out.write(bytes,0,c);  
	                localSize+=c;  
	                long nowProcess = localSize /step;  
	                if(nowProcess > process){  
	                    process = nowProcess;  
	                    if(process % 2 == 0)  
	                    {
	                    	logger.info("《"+name+"》续传文件下载进度："+process);
	                    	MappingConstant.DOWNLOADING_PROGRESS.put(movieId, process);
	                    }
	                }  
	            }  
	            if(process == 100)
				{
					try {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.flush();
							out.close();
						}
					} catch (IOException e) {
						logger.error("关闭流异常", e);
					}
				}
	            boolean isDo = ftpClient.completePendingCommand();  
	            if(isDo || process == 100){  
	                return DownloadStatus.Download_From_Break_Success;  
	            }else {  
	            	return DownloadStatus.Download_From_Break_Failed;  
	            }  
	        }else {  
	        	//本地不存在文件
	        	out = new FileOutputStream(f);  
	            in= ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"),"iso-8859-1"));  
	            byte[] bytes = new byte[1024];  
	            long step = lRemoteSize /100;  
	            long process=0;
	            long localSize = 0L;
	            MappingConstant.movieSize.put(movieId, localSize);
	            int c;  
	            while((c = in.read(bytes))!= -1){  
	            	if (!downloading) {
	            		return DownloadStatus.Download_Interrupt;
	            	}
	                out.write(bytes, 0, c);  
	                localSize+=c;  
	                long nowProcess = localSize /step;  
	                if(nowProcess > process){  
	                    process = nowProcess;  
	                    if(process % 2 == 0)
	                    {
	                    	logger.info("《"+name+"》新文件下载进度："+process);
	                    	MappingConstant.DOWNLOADING_PROGRESS.put(movieId, process);
	                    }
	                }  
	            }  
	            if(process == 100)
				{
					try {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.flush();
							out.close();
						}
					} catch (IOException e) {
						logger.error("关闭流异常", e);
					}
				}
	            boolean upNewStatus = ftpClient.completePendingCommand();  
	            if(upNewStatus || process == 100){  
	            	return DownloadStatus.Download_New_Success;
	            }else {  
	            	return DownloadStatus.Download_New_Failed;
	            }  
	        }  
		} catch (IOException e) {
			logger.error("ContinueFTP IO异常",e);
		} finally {
			try {
				if(in != null)
				{
					in.close();
				}
				if(out != null)
				{
					out.flush();
					out.close();
				}
			} catch (IOException e2) {
				logger.error("关闭流异常", e2);
			}
		}
        return null; 
    }  
      
    /** 
     * 上传文件到FTP服务器，支持断点续传 
     * @param local 本地文件名称，绝对路径 
     * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构 
     * @return 上传结果 
     * @throws IOException 
     */  
    public UploadStatus upload(String local,String remote,String videoId) throws IOException{  
        //设置PassiveMode传输  
        ftpClient.enterLocalPassiveMode();  
        //设置以二进制流的方式传输  
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);  
        ftpClient.setControlEncoding("GBK");  
        UploadStatus result;  
        //对远程目录的处理  
        String remoteFileName = remote;  
        if(remote.contains("/")){  
            remoteFileName = remote.substring(remote.lastIndexOf("/")+1);  
            //创建服务器远程目录结构，创建失败直接返回  
            if(CreateDirecroty(remote, ftpClient)==UploadStatus.Create_Directory_Fail){  
                return UploadStatus.Create_Directory_Fail;  
            }  
        }  
          
        //检查远程是否存在文件  
        FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));  
        if(files.length == 1){  
            long remoteSize = files[0].getSize();  
            File f = new File(local);
            long localSize = f.length();
            if(remoteSize==localSize){  
                return UploadStatus.File_Exits;  
            }else if(remoteSize > localSize){  
                return UploadStatus.Remote_Bigger_Local;  
            }  
              
            //尝试移动文件内读取指针,实现断点续传  
            result = uploadVideo(remoteFileName, f, ftpClient, remoteSize,videoId);  
              
            //如果断点续传没有成功，则删除服务器上文件，重新上传  
            if(result == UploadStatus.Upload_From_Break_Failed){  
                if(!ftpClient.deleteFile(remoteFileName)){  
                    return UploadStatus.Delete_Remote_Faild;  
                }  
                result = uploadVideo(remoteFileName, f, ftpClient, 0,videoId);  
            }  
        }else {  
            result = uploadVideo(remoteFileName, new File(local), ftpClient, 0,videoId);  
        }
        return result;  
    } 
    
    /** 
     * 断开与远程服务器的连接 
     * @throws IOException 
     */  
    public void disconnect() throws IOException{  
        if(ftpClient != null && ftpClient.isConnected()){  
            ftpClient.disconnect();  
        }  
    } 
    
    /** 
     * 断开与远程服务器的连接 
     * @throws IOException 
     */  
    public void disconnectTarget() throws IOException{  
        if(targetClient != null && targetClient.isConnected()){  
        	targetClient.disconnect();  
        }  
    } 
      
    /** 
     * 递归创建远程服务器目录 
     * @param remote 远程服务器文件绝对路径 
     * @param ftpClient FTPClient对象 
     * @return 目录创建是否成功 
     * @throws IOException 
     */  
    public UploadStatus CreateDirecroty(String remote,FTPClient ftpClient) throws IOException{  
        UploadStatus status = UploadStatus.Create_Directory_Success;  
        String directory = remote.substring(0,remote.lastIndexOf("/")+1); 
        logger.info("-------"+remote);
        logger.info("*******"+!directory.equalsIgnoreCase("/"));
        logger.info("+++++++"+!ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"),"iso-8859-1")));
        if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"),"iso-8859-1"))){  
            //如果远程目录不存在，则递归创建远程服务器目录  
            int start=0;  
            int end = 0;  
            if(directory.startsWith("/")){  
                start = 1;  
            }else{  
                start = 0;  
            }  
            end = directory.indexOf("/",start);  
            while(true){  
                String subDirectory = new String(remote.substring(start,end).getBytes("GBK"),"iso-8859-1");  
                if(!ftpClient.changeWorkingDirectory(subDirectory)){  
                    if(ftpClient.makeDirectory(subDirectory)){  
                        ftpClient.changeWorkingDirectory(subDirectory);  
                    }else {  
                        logger.info("创建目录失败");  
                        return UploadStatus.Create_Directory_Fail;
                    }  
                }  
                  
                start = end + 1;  
                end = directory.indexOf("/",start);  
                  
                //检查所有目录是否创建完毕  
                if(end <= start){  
                    break;  
                }  
            }  
        }  
        return status;  
    }  
      
    /** 
     * 上传文件到服务器,新上传和断点续传 
     * @param remoteFile 远程文件名，在上传之前已经将服务器工作目录做了改变 
     * @param localFile 本地文件File句柄，绝对路径 
     * @param processStep 需要显示的处理进度步进值 
     * @param ftpClient FTPClient引用 
     * @return 
     * @throws IOException 
     */  
    public UploadStatus uploadVideo(String remoteFile,File localFile,FTPClient ftpClient,long remoteSize,String videoId) throws IOException{  
        UploadStatus status;  
        //显示进度的上传  
        long step = localFile.length() / 100;  
        long process = 0;  
        long localreadbytes = 0L;  
        RandomAccessFile raf = new RandomAccessFile(localFile,"r");  
        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("GBK"),"iso-8859-1"));  
        //断点续传  
        if(remoteSize>0){  
            ftpClient.setRestartOffset(remoteSize);  
            process = remoteSize /step;  
            raf.seek(remoteSize);  
            localreadbytes = remoteSize;  
        }  
        byte[] bytes = new byte[1024];  
        int c;  
        while((c = raf.read(bytes))!= -1){  
            out.write(bytes,0,c);  
            localreadbytes+=c;  
            if(localreadbytes / step != process){  
                process = localreadbytes / step;  
                if(process % 2 == 0)  
                {
                	logger.info("上传进度:" + process);
                	MappingConstant.UPLOADING_PROGRESS.put(videoId, process);
                }
            }  
        }  
        out.flush();  
        raf.close();  
        out.close();  
        boolean result =ftpClient.completePendingCommand();  
        if(remoteSize > 0){  
            status = result?UploadStatus.Upload_From_Break_Success:UploadStatus.Upload_From_Break_Failed;  
        }else {  
            status = result?UploadStatus.Upload_New_File_Success:UploadStatus.Upload_New_File_Failed;  
        }
        logger.info("localFile:"+localFile);
        if(UploadStatus.Upload_New_File_Success.equals(status)){
        	localFile.delete();
        }
        return status;  
    }  
    
    public UploadStatus uploadPictures(InputStream in, String fileUrl) throws IOException{  
    	
    	logger.info("图片存储路径："+fileUrl);
    	logger.info("先删除现有文件！");
    	ftpClient.sendCommand("DELE " + fileUrl + "\r\n");
    	//对远程目录的处理  
        String remoteFileName = fileUrl;  
        if(fileUrl.contains("/")){  
            remoteFileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);  
            //创建服务器远程目录结构，创建失败直接返回 
            if(CreateDirecroty(fileUrl, ftpClient)==UploadStatus.Create_Directory_Fail){  
                return UploadStatus.Create_Directory_Fail;  
            }  
        }
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        OutputStream out = ftpClient.appendFileStream(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));  
        
    	try {
    		byte[] bytes = new byte[1024];
        	int c;
        	while((c = in.read(bytes))!= -1){
        		out.write(bytes, 0, c);
        	}
		} catch (Exception e) {
			logger.error("操作发生异常", e);
		} finally {
			try {
				Thread.sleep(2000);
				in.close();
				out.flush();
	    		out.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("图片已经写入指定路径");
		}
    	
    	return UploadStatus.Upload_New_File_Success;
    }
    
    public UploadStatus uploadXML(String xml, String fileUrl) throws IOException{
    	InputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));  
    	logger.info("XML存储路径："+fileUrl);
    	logger.info("先删除现有文件！");
    	ftpClient.sendCommand("DELE " + fileUrl + "\r\n");
    	//对远程目录的处理  
        String remoteFileName = fileUrl;  
        if(fileUrl.contains("/")){  
            remoteFileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);  
            //创建服务器远程目录结构，创建失败直接返回  
            if(CreateDirecroty(fileUrl, ftpClient)==UploadStatus.Create_Directory_Fail){  
                return UploadStatus.Create_Directory_Fail;  
            }  
        }
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        OutputStream out = ftpClient.appendFileStream(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));  
    	
        try {
    		byte[] bytes = new byte[1024];
        	int c;
        	while((c = in.read(bytes))!= -1){
        		out.write(bytes, 0, c);
        	}
		} catch (Exception e) {
			logger.error("操作发生异常", e);
		} finally {
			try {
				Thread.sleep(2000);
				in.close();
				out.flush();
	    		out.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("XML已经写入指定路径");
		}
        
    	return UploadStatus.Upload_New_File_Success;
    }
    
    /**
	 * 删除文件
	 * 
	 * @param fileUrl
	 *            删除的文件路径
	 * @return
	 */
	public boolean deleteFile(String fileUrl) {
		boolean result = false;
		try {
//			ftpClient.sendCommand("DELE " + fileUrl + "\r\n");
			//检查远程是否存在文件  
			FTPFile[] files = ftpClient.listFiles(new String(fileUrl.getBytes("GBK"),"iso-8859-1"));
			if(files.length == 1){
				result = ftpClient.deleteFile(new String(fileUrl.getBytes("GBK"), "iso-8859-1"));
			}else{
				result = false;
			}
		} catch (IOException e) {
			logger.error(e);
		}
		return result;
	}
	
	/**
	 * 获取远程文件大小
	 */
	public String listenDownload(String fileUrl) throws IOException {  
		//检查远程是否存在文件  
		FTPFile[] files = ftpClient.listFiles(new String(fileUrl.getBytes("GBK"),"iso-8859-1"));
		if(files.length == 1){  
            long remoteSize = files[0].getSize();
            return String.valueOf(remoteSize);
		}else{
			return "fileNotExists";
		}
	}
	
	public boolean isFileExists(String fileUrl) throws IOException{
    	boolean isFileExists = false;
    	
    	ftpClient.setControlEncoding("GBK");
    	
    	String filePath = fileUrl.substring(0, fileUrl.lastIndexOf("/"));
    	String fileName= fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length());
    	
        ftpClient.enterLocalPassiveMode();
        ftpClient.changeWorkingDirectory(new String(filePath.getBytes("GBK"),"iso-8859-1"));
    	
    	//检查远程文件是否存在  
        FTPFile[] files = ftpClient.listFiles(new String(fileName.getBytes("GBK"),"iso-8859-1"));
        
        if(files.length == 1){
            logger.info("--------"+fileUrl+"文件存在");
            isFileExists = true;
        }else{
        	logger.info("--------"+fileUrl+"文件不存在");
        	isFileExists = false;
        }
    	return isFileExists;
    }
	
	//导出excel
	public boolean exportExcel(String fileUrl,XSSFWorkbook workbook){
		String remoteFileName = fileUrl;
		if(fileUrl.contains("/")){  
            remoteFileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);  
            //创建服务器远程目录结构，创建失败直接返回  
            try {
				if(CreateDirecroty(fileUrl, ftpClient)==UploadStatus.Create_Directory_Fail){  
				    return false;  
				}
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		        OutputStream out = ftpClient.appendFileStream(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));
				workbook.write(out);
				out.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}  
        }
		return false;
	}
	
	public PdfReader readModelPDF(String modelPDF){
		String remoteFileName = GlobalConstant.SYS_MAP.get(GlobalConstant.EXPORT_PDF_MODEL);
		String fileUrl = remoteFileName + modelPDF;
		try {
			if(CreateDirecroty(fileUrl, ftpClient)==UploadStatus.Create_Directory_Fail){  
			    return null;  
			}
			InputStream in = null;
			in = ftpClient.retrieveFileStream(new String(fileUrl.getBytes("GBK"),"iso-8859-1"));
			logger.info("输入流："+in);
			PdfReader reader = new PdfReader(in);
			in.close();
			return reader;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public boolean readModelPDF2Local(String modelPDF,String uploadPath){
		String remoteFileName = GlobalConstant.SYS_MAP.get(GlobalConstant.EXPORT_PDF_MODEL);
		String fileUrl = remoteFileName + modelPDF;
		File file = new File(uploadPath+"/"+modelPDF);
		try {
			if(CreateDirecroty(fileUrl, ftpClient)==UploadStatus.Create_Directory_Fail){  
			    return false;  
			}
			InputStream in = null;
			OutputStream out = new FileOutputStream(file,true);
			in = ftpClient.retrieveFileStream(new String(fileUrl.getBytes("GBK"),"iso-8859-1"));
			byte[] temp = new byte[1024];
			int length = 0;  
	        // 源文件读取一部分内容  
	        while ((length = in.read(temp)) != -1) {  
	            // 目标文件写入一部分内容  
	        	out.write(temp, 0, length);  
	        }
			in.close();
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean exportPDF(String fileUrl,ByteArrayOutputStream[] bos,int pageNo){
		String remoteFileName = fileUrl;
		if(fileUrl.contains("/")){  
            remoteFileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);  
            //创建服务器远程目录结构，创建失败直接返回  
            try {
				if(CreateDirecroty(fileUrl, ftpClient)==UploadStatus.Create_Directory_Fail){  
				    return false;  
				}
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		        OutputStream out = ftpClient.appendFileStream(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));
		        
		        Document doc = new Document();  
	            PdfCopy pdfCopy = new PdfCopy(doc, out);  
	            doc.open();
	            PdfImportedPage impPage = null;  
	            //取出之前保存的每页内容  
	            logger.info("导出文件页数：" + pageNo);
	            for (int i = 0; i < pageNo; i++) { 
	                impPage = pdfCopy.getImportedPage(new PdfReader(bos[i]  
	                        .toByteArray()), 1);
	                if(null!= impPage){
	                	pdfCopy.addPage(impPage);  
	                }
	            }  
	            doc.close();//当文件拷贝  记得关闭doc
				out.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch(DocumentException de){
				de.printStackTrace();
				return false;
			} 
        }
		return false;
	}
	
	public boolean uploadFile(CommonsMultipartFile cf,String fileName){
		String fileUrl = GlobalConstant.SYS_MAP.get(GlobalConstant.IMPORT_FILE);
		try {
			if(CreateDirecroty(fileUrl, ftpClient)==UploadStatus.Create_Directory_Fail){  
			    return false;  
			}
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			fileName = new String(fileName.getBytes("GBK"),"iso-8859-1");
			return ftpClient.storeFile(fileUrl + fileName, cf.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public DownloadStatus downLoadFile(String localPath,String fileUrl){
		String filePath = fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
		try {
			if(CreateDirecroty(filePath, ftpClient)==UploadStatus.Create_Directory_Fail){  
			    return DownloadStatus.Create_Directory_Fail;  
			}
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			FTPFile[] file = ftpClient.listFiles();
			for(int i = 0 ;i < file.length; i++){
				logger.info("文件名称：" + file[i].getName());
				if(file[i].getName().contains(fileName)){
					File localFile = new File(localPath + "/" + fileName);
                    OutputStream is = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(new String(fileName.getBytes("GBK"),"iso-8859-1"), is);
                    is.close();
                    return DownloadStatus.Download_New_Success;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return DownloadStatus.Download_New_Failed;
			
		}
		return DownloadStatus.Remote_File_Noexist;
	}
	
	public boolean reNameFile(String fileUrl,String newName){
		String filePath = fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
		try {
			if(CreateDirecroty(filePath, ftpClient)==UploadStatus.Create_Directory_Fail){  
			    return false;  
			}
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			FTPFile[] file = ftpClient.listFiles();
			for(int i = 0 ;i < file.length; i++){
				logger.info("文件名称：" + file[i].getName());
				if(file[i].getName().contains(fileName)){
					//注意中文的名称要转换编码
					return ftpClient.rename(new String(fileName.getBytes("GBK"),"iso-8859-1"),
							 new String(newName.getBytes("GBK"),"iso-8859-1"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
      
    public static void main(String[] args) {  
        ContinueFTP ftp = new ContinueFTP();
        String fileUrl = "/2015/c1/2cmstemp/10e36170b24fca56cbae5fbc5d6238bbb.ts";
        
        try {  
        	boolean connect = ftp.connect("183.252.164.142", 21, "mgtvftp", "mgtvftp1234");
        	boolean tarconnect = ftp.connectTarget("223.112.4.232", 21, "sfw", "sfw201103");
        	
//        	boolean connect = ftp.connect("183.252.164.142", 21, "mgtv", "sfw201103");
//          boolean connect = ftp.connect("183.252.164.142", 21, "mgtvftp", "mgtvftp1234");
            if(connect && tarconnect)
            {
            	System.out.println("FTP已经连接===");
            	ftp.downloadFromRemotetoRemote("", "", fileUrl);
//            	if(ftp.isFileExists(fileUrl))
//            	{
//                	logger.info("***************");
//            	}
            	
            	ftp.disconnect();
            	logger.info("FTP已经关闭===");
            }
        } catch (IOException e) {  
            System.err.println("连接FTP出错："+e.getMessage());  
        } finally {
        	try {
				ftp.disconnect();
				ftp.disconnectTarget();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }  
}
