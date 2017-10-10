package com.springmvc.walker.util;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;


public class ImageUtil {

	/**
	 * 指定比例压缩
	 * 
	 * @param oldFile
	 *            要压缩的文件路径
	 * @param newFile
	 *            压缩后的文件路径
	 * @param scale
	 *            压缩比例
	 * @param quality
	 *            压缩质量
	 * @return 是否成功
	 */
	public static boolean compress(String oldFile, String newFile, float scale,
			float quality) {
		if (oldFile == null || newFile == null || scale <= 0) {
			System.err.println("参数错误!");
			return false;
		}
		if (quality <= 0) {
			System.out.println("压缩质量" + quality + "无效,使用默认值1!");
		}

		FileOutputStream out = null;
		try {
			File srcFile = new java.io.File(oldFile);
			Image src = ImageIO.read(srcFile);
			int srcHeight = src.getHeight(null);
			int srcWidth = src.getWidth(null);
			int deskHeight = (int) (srcHeight * scale);// 缩略图高
			int deskWidth = (int) (srcWidth * scale);// 缩略图宽

			BufferedImage tag = new BufferedImage(deskWidth, deskHeight,
					BufferedImage.TYPE_3BYTE_BGR);
			tag.getGraphics().drawImage(src, 0, 0, deskWidth, deskHeight, null); // 绘制缩小后的图

			out = new FileOutputStream(newFile); // 输出到文件流
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
			jep.setQuality(quality, true);
			encoder.encode(tag, jep); // 近JPEG编码

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 指定比例压缩
	 * 
	 * @param oldFile
	 *            要压缩的文件路径
	 * @param newFile
	 *            压缩后的文件路径
	 * @param scale
	 *            压缩比例
	 * @return 是否成功
	 */
	public static boolean compress(String oldFile, String newFile, float scale) {
		return compress(oldFile, newFile, scale, 1f);
	}

	/**
	 * 指定宽高压缩
	 * 
	 * @param oldFile
	 *            要压缩的文件路径
	 * @param newFile
	 *            压缩后的文件路径
	 * @param width
	 *            压缩后的宽度
	 * @param height
	 *            压缩后的高度
	 * @param quality
	 *            压缩质量
	 * @return 是否成功
	 */
	public static boolean compress(String oldFile, String newFile, int width,
			int height, float quality) {
		if (oldFile == null || newFile == null || width <= 0 || height <= 0) {
			System.err.println("参数错误!");
			return false;
		}

		if (quality <= 0) {
			System.out.println("压缩质量" + quality + "无效,使用默认值1!");
		}
		FileOutputStream out = null;
		try {
			Image srcFile = ImageIO.read(new File(oldFile));
			/** 宽,高设定 */
			BufferedImage tag = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);

			/** 压缩之后临时存放位置 */
			out = new FileOutputStream(newFile);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
			/** 压缩质量 */
			jep.setQuality(quality, true);
			encoder.encode(tag, jep);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 指定宽高压缩
	 * 
	 * @param oldFile
	 *            要压缩的文件路径
	 * @param newFile
	 *            压缩后的文件路径
	 * @param width
	 *            压缩后的宽度
	 * @param height
	 *            压缩后的高度
	 * @return 是否成功
	 */
	public static boolean compress(String oldFile, String newFile, int width,
			int height) {
		return compress(oldFile, newFile, width, height, 1f);
	}

	/**
	 * 指定宽高压缩(是否等比例)
	 * 
	 * @param oldFile
	 *            要压缩的文件路径
	 * @param newFile
	 *            压缩后的文件路径
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param rate
	 *            是否等比例
	 * @return 是否成功
	 */
	public static boolean compress(String oldFile, String newFile, int width,
			int height, boolean keepRate) {
		if (keepRate) {
			try {
				File srcFile = new java.io.File(oldFile);
				Image src = ImageIO.read(srcFile);
				int srcHeight = src.getHeight(null);
				int srcWidth = src.getWidth(null);

				float wRate = 1f * width / srcWidth;
				float hRate = 1f * height / srcHeight;
				if (wRate > hRate) {
					height = (int) (wRate * srcHeight);
				} else {
					width = (int) (hRate * srcWidth);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return compress(oldFile, newFile, width, height);
	}

	public static boolean cutCenter(String src, String dest, int w, int h) {
		try {
			Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("jpg");
			ImageReader reader = (ImageReader) iterator.next();
			InputStream in = new FileInputStream(src);
			ImageInputStream iis = ImageIO.createImageInputStream(in);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			int imageIndex = 0;
			Rectangle rect = new Rectangle(
					(reader.getWidth(imageIndex) - w) / 2, (reader
							.getHeight(imageIndex) - h) / 2, w, h);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			ImageIO.write(bi, "jpg", new File(dest));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获取图片文件的分辨率
	 * @param file 图片文件地址
	 * @return width*height
	 */
	public static String resloution(String file) {
		FileOutputStream out = null;
		try {
			File srcFile = new java.io.File(file);
			
			Image src = ImageIO.read(srcFile);
			int srcHeight = src.getHeight(null);
			int srcWidth = src.getWidth(null);

			return srcWidth + "*" + srcHeight;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return null;
	}
	
	/**
	 * 获取图片文件的大小(单位：B)
	 * @param file 图片文件地址
	 * @return B
	 */
	public static Long size(String file) {
		try {
			File srcFile = new java.io.File(file);
			
			return (srcFile.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}