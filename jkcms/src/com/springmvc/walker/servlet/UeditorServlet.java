package com.springmvc.walker.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.springmvc.framework.constant.GlobalConstant;
import com.springmvc.framework.util.FtpUtils;
import com.springmvc.framework.util.StringUtils;
import com.springmvc.framework.util.UUIDUtil;
import com.springmvc.walker.util.UeditorResopnse;

public class UeditorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(UeditorServlet.class);
	private String FILE_WEB_SERVER = GlobalConstant.SYS_MAP.get(GlobalConstant.FILE_WEB_SERVER);

	private String FTP_SERVER_HOST = GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_IP);
	private int FTP_SERVER_PORT = Integer.valueOf(GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PORT));

	private String FTP_SERVER_USERNAME = GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_USER);
	private String FTP_SERVER_PASSWORD = GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_PASSWORD);
	private String FTP_SERVER_ROOTPATH = GlobalConstant.SYS_MAP.get(GlobalConstant.FTP_TARGET_ROOTPATH);
	private DiskFileItemFactory diskFileItemFactory;
	private String uploadPath;
	long sizeMax = 1024 * 1024 * 100;// 设置文件的大小为100M
	private ServletFileUpload servletFileUpload;
	private String configUpload;

	public UeditorServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		uploadPath = config.getServletContext().getRealPath("/upload/");

		File uploadFilePath = new File(uploadPath);
		if (!uploadFilePath.exists()) {
			uploadFilePath.mkdirs();
		}

		diskFileItemFactory = new DiskFileItemFactory();
		diskFileItemFactory.setRepository(uploadFilePath);

		int sizeThreshold = 1024 * 10; // 缓存区大小
		diskFileItemFactory.setSizeThreshold(sizeThreshold);

		servletFileUpload = new ServletFileUpload(diskFileItemFactory);
		servletFileUpload.setSizeMax(sizeMax);

		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/springmvc/walker/servlet/config.json");
		Reader reader = new InputStreamReader(in);
		StringWriter out = new StringWriter();

		char[] buffer = new char[1024];

		int total;

		try {
			while ((total = reader.read(buffer)) > 0) {

				out.write(buffer, 0, total);
			}

			out.flush();

			configUpload = out.toString();

			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(configUpload);
			configUpload = m.replaceAll("");

			Pattern pattern2 = Pattern.compile("/\\*(.*?)\\*/", Pattern.DOTALL);
			Matcher matcher2 = pattern2.matcher(configUpload);
			configUpload = matcher2.replaceAll("");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setHeader("Content-Type", "text/html");
		String action = request.getParameter("action");
		if ("config".equals(action)) {
			response.getWriter().write(configUpload);
		} else {
			response.getWriter().write("------------");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStream inputStream = null;
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (!isMultipart) {
				return;
			}
			FtpUtils ftp = new FtpUtils();
			List<FileItem> fileItems = servletFileUpload.parseRequest(request);

			for (FileItem fileItem : fileItems) {
				String fileName = fileItem.getName();
				if (StringUtils.isNotEmpty(fileName)) {
					inputStream = fileItem.getInputStream();
					PrintWriter out = response.getWriter();
					String newFileName = new Date().getTime() + UUIDUtil.getNextCountDown() + fileName.substring(fileName.lastIndexOf("."));
					String filePath = "/content/";
					String fullPath = FTP_SERVER_ROOTPATH + filePath;

					// 链接FTP
					ftp.connect(FTP_SERVER_HOST, FTP_SERVER_PORT, FTP_SERVER_USERNAME, FTP_SERVER_PASSWORD, false);

					String ftpFileName = fullPath + newFileName;
					// 上传文件
					ftp.uploadUeditor(ftpFileName, inputStream);
					UeditorResopnse ur = new UeditorResopnse(fileName, FILE_WEB_SERVER + ftpFileName.replace(FTP_SERVER_ROOTPATH, ""), newFileName);
					response.getWriter().write(JSON.toJSONString(ur));
					logger.info("Url:"+JSON.toJSONString(ur));
					out.flush();
				}
				fileItem.delete();
			}
		} catch (Exception e) {
			logger.info("UeditorServlet======>doPost======>error" + e);
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				logger.info("UeditorServlet======>IO_Close======>error" + e);
				e.printStackTrace();
			}
		}

	}
}
