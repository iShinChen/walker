package com.springmvc.walker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.walker.constant.FfmpegConstant;
import com.springmvc.walker.entity.ProcessBean;
import com.springmvc.walker.entity.ProcessState;

/**
 * FFMPEG_UTIL
 *
 */
public class FfmpegUtil {
	
	private final static Logger logger = Logger.getLogger(FfmpegUtil.class);
	private String taskId;
	private List<String> commend;
	
	public FfmpegUtil(String taskId, List<String> commend) {
		this.taskId = taskId;
		this.commend = commend;
	}
	
	/**
	 * doCommend
	 * @return
	 */
	public JSONObject doCommend() {
		JSONObject json = new JSONObject();
		Process p = null;
		BufferedReader buf = null;
		ProcessBuilder builder = null;
		try {
			builder = new ProcessBuilder(commend);
			builder.command(commend);
			builder.redirectErrorStream(true);
			p = builder.start();
			
			ProcessBean processBean = new ProcessBean();
			processBean.setTaskId(taskId);
			processBean.setProcess(p);
			processBean.setState(ProcessState.RUNNING);
			FfmpegConstant.FFMPEG_PROCESS.put(taskId, processBean);
			
			String line = null;
			buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuffer sb = new StringBuffer();
			while ((line = buf.readLine()) != null) {
				logger.info(line);
				sb.append(line);
				//FfmpegConstant.FFMPEG_LOG.get(taskId).getList().add(line);
				continue;
			}
			
			int exitVal = p.waitFor();
			
			if(exitVal != 0)
			{
				logger.info("ffmpeg执行异常");
				InputStream error = p.getErrorStream();
				InputStreamReader isr = new InputStreamReader(error); 
				buf = new BufferedReader(isr);
				line = null; 
				sb = new StringBuffer();
	            while ((line = buf.readLine()) != null) {  
	                logger.info(line);
	                //FfmpegConstant.FFMPEG_LOG.get(taskId).getList().add(line);
	                sb.append(line);
	            } 
	            if(error != null)
	            {
	            	error.close();
	            }
	            if(isr != null)
	            {
	            	isr.close();
	            }
	            
	            json.put("success", false);
			}else{
				json.put("success", true);
			}
		} catch (IOException e) {
			ProcessBean processBean = FfmpegConstant.FFMPEG_PROCESS.get(taskId);
			if(processBean.getState().equals(ProcessState.CANCEL))
			{
				json.put("success", true);
				//FfmpegConstant.FFMPEG_LOG.get(taskId).getList().add("[" + taskId + "]-执行终止指令：" + processBean.getState());
			} else {
				logger.error("ffmpeg执行失败", e);
				json.put("success", false);
				//FfmpegConstant.FFMPEG_LOG.get(taskId).getList().add("[" + taskId + "]-执行指令失败：" + e);
			}
		} catch (Exception e) {
			logger.error("ffmpeg执行失败", e);
			json.put("success", false);
			//FfmpegConstant.FFMPEG_LOG.get(taskId).getList().add("[" + taskId + "]-执行指令失败：" + e);
		} finally {
			if(buf != null) {
				try {
					buf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FfmpegConstant.FFMPEG_PROCESS.remove(taskId);
		}
		
		return json;
	}
	
	public static Map<String, Object> getVideoDesc(String ffmpegInput, String inputStream) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		PatternCompiler compiler =new Perl5Compiler();
		
		List<String> commend = new ArrayList<String>();
        //转换工具路径
		commend.add(ffmpegInput);
		//添加参数＂-i＂，该参数指定要转换的文件
		commend.add("-i");
		//要转格式视频文件的路径
		commend.add(inputStream);
		
        Process p = null;
		BufferedReader buf = null;
		ProcessBuilder builder = null;
		try {
			String regexDuration ="Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";  
	        String regexVideo ="Video: (.*?), (.*?), (.*?), (.*?),\\s";
	        String regexAudio ="Audio: (.*?), (\\d*) Hz, (.*?), (.*?), (\\d*) kb\\/s";
			
			builder = new ProcessBuilder(commend);
			builder.command(commend);
			builder.redirectErrorStream(true);
			p = builder.start();
			String line = null;
			buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuffer sb = new StringBuffer();
			while ((line = buf.readLine()) != null) {
				logger.info(line);
				sb.append(line);
				continue;
			}
			
			int exitVal = p.waitFor();
			
			Pattern patternDuration = compiler.compile(regexDuration,Perl5Compiler.CASE_INSENSITIVE_MASK);  
		     PatternMatcher matcherDuration = new Perl5Matcher();
			 if(matcherDuration.contains(sb.toString(), patternDuration)) {
	             MatchResult re = matcherDuration.getMatch();  
	             logger.info("提取出播放时间===" +re.group(1));  
	             logger.info("开始时间=====" +re.group(2));  
	             logger.info("bitrate码率 单位 kb==" +re.group(3));  
	             
	             // 将时间换算成秒
	             if(!"".equals(re.group(1)) && re.group(1) != null) {
	            	 String[] durations = re.group(1).split(":");
	            	 String duration = "";
	            	 int hour = Integer.valueOf(durations[0])*3600;
	            	 int minute = Integer.valueOf(durations[1])*60;
	            	 int second = (int)Math.ceil(Double.valueOf(durations[2]));
	            	 duration = (hour + minute + second) + "";
	            	 logger.info("播放时间换算为秒：" + duration);
	            	 map.put("DURATION", duration);
	             }
	             
	             map.put("KBPS", re.group(3));
		     }
			 
			 Pattern patternVideo = compiler.compile(regexVideo,Perl5Compiler.CASE_INSENSITIVE_MASK);  
	         PatternMatcher matcherVideo = new Perl5Matcher();  
	         
	         if(matcherVideo.contains(sb.toString(), patternVideo)){  
	            MatchResult re = matcherVideo.getMatch();
	            
	            //获取视频编码格式
	            String videoType = regexFind(re.group(1), "(.+?)(?= \\()");
	            //获取视频分辨率
	            String videoScal = regexFind(re.group(3), "(.+?)(?= \\[)");
	            
	            logger.info("编码格式===" + videoType);  
	            logger.info("视频格式===" + regexFind(re.group(2), "(.+?)(?=\\()"));  
	            logger.info("分辨率===" + videoScal);  
	            logger.info("帧速率 fbs==" + regexFind(re.group(4), "(.+?)(?= fps)"));
	            
	            map.put("VIDEOTYPE", videoType);
	            if(!"".equals(videoScal) && videoScal != null) {
	            	map.put("RESOLUTION_W", videoScal.split("x")[0]);
	            	map.put("RESOLUTION_H", videoScal.split("x")[1]);
	            }
	         }  
	         
	         Pattern patternAudio = compiler.compile(regexAudio,Perl5Compiler.CASE_INSENSITIVE_MASK);  
	         PatternMatcher matcherAudio = new Perl5Matcher();  
	         
	         if(matcherAudio.contains(sb.toString(), patternAudio)){  
	            MatchResult re = matcherAudio.getMatch();  
	            logger.info("音频编码===" + regexFind(re.group(1), "(.+?)(?= \\()"));  
	            logger.info("音频采样频率===" + re.group(2)); 
	            logger.info("音频声道===" + re.group(3));
	            logger.info("音频码率===" + re.group(5));
	            
	            
	            map.put("AUDIOFORMAT", regexFind(re.group(1), "(.+?)(?= \\()"));
	            map.put("AUDIOTYPE", re.group(3));
	         }
			
			if(exitVal != 0)
			{
				InputStream error = p.getErrorStream();
				InputStreamReader isr = new InputStreamReader(error); 
				buf = new BufferedReader(isr);
				line = null;  
	            while ((line = buf.readLine()) != null) {  
	                logger.error(line);  
	            } 
	            if(error != null)
	            {
	            	error.close();
	            }
	            if(isr != null)
	            {
	            	isr.close();
	            }
			}else{
				 logger.info("****************************媒体文件解析完成****************************");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(buf != null) {
				try {
					buf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return map;
	}
	
	private static String regexFind(String source, String regex) {
		try {
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
			java.util.regex.Matcher matcher = pattern.matcher(source);

			if (matcher.find()) {
				return matcher.group(0);
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
