package com.springmvc.framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class FileUtil {
	
	public static String read(File file) throws IOException {
		return read(file, "UTF-8");
	}

	public static String read(File file, String charset) {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(new FileInputStream(file), charset);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			StringBuffer sb = new StringBuffer("");
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\r\n");
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				isr.close();
			} catch (Exception e2) {
			}
		}
	}

	public static boolean write(File file, List<String> values) {
		return write(file, "UTF-8", values);
	}

	public static boolean write(File file, String charset, List<String> values) {
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(file), charset);
			if (values != null) {
				for (String value : values) {
					osw.write(value);
					osw.write("\n");
				}
			}
			osw.flush();
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				osw.close();
			} catch (Exception e2) {
			}
		}
	}
	
	public static boolean move(File srcFile, String destPath) {
		File dir = new File(destPath);
		return srcFile.renameTo(new File(dir, srcFile.getName()));
	}

	public static boolean move(String srcFile, String destPath) {
		File file = new File(srcFile);
		File dir = new File(destPath);

		return file.renameTo(new File(dir, file.getName()));
	}

	public static int copy(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
				}
				fs.close();
				inStream.close();
			}
			return bytesum;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int copy(File oldfile, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldfile);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
			return bytesum;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean delete(File file) {
		try {
			return file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean delete(String fileUrl) {
		try {
			return new File(fileUrl).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
