package com.springmvc.walker.constant;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.springmvc.walker.entity.FfmpegLog;
import com.springmvc.walker.entity.ProcessBean;


public class FfmpegConstant {
	
	public static Map<String, ProcessBean> FFMPEG_PROCESS = new HashMap<String, ProcessBean>();
	
	public static Map<String, FfmpegLog> FFMPEG_LOG = new HashMap<String, FfmpegLog>();
	
	public static Map<String, String> FDFS_CONFIG = new HashMap<String, String>();
	
	/**请求成功*/
	public static final String RETURN_SUCCESS = "0";
	/**请求失败*/
	public static final String RETURN_FAIL = "1";
	
	/**执行成功*/
	public static final String TASK_RESULT_SUCCESS = "0";
	/**执行成功*/
	public static final String TASK_RESULT_FAIL = "1";
	
	/**成功*/
	public static final String ERROR_CODE_SUCCESS = "000";      
	/**获取登记信息错误*/
	public static final String ERROR_CODE_CUSTOMER_ILLEGAL = "001";
	/**指令参数不合法*/
	public static final String ERROR_CODE_PARAM_ILLEGAL = "002";
	/**验签失败*/
	public static final String ERROR_CODE_SIGN_ERROR = "003";
	/**处理失败*/
	public static final String ERROR_CODE_PROCESS_FAIL = "004";
	/**系统异常*/
	public static final String ERROR_CODE_SYS_ERROR = "005";
	/**任务不存在*/
	public static final String ERROR_CODE_TASKNOTEXISTS = "006";
	
	/**切片文件直接上传fastdfs*/
	public static final String UPLOAD_FASTDFS = "1";
	
	/**直接引用ftp路径文件进行切片*/
	public static final String SEGMENT_FROM_REMOTE = "1";
	
	public static void setResultJSON(JSONObject rspJson, String result, String errorCode, String errorMessage, String taskId, String ftpUrl, String httpUrl) {
		rspJson.put("result", result);
		rspJson.put("error_code", errorCode);
		rspJson.put("error_message", errorMessage);
		JSONObject data = new JSONObject();
		data.put("taskId", taskId);
		data.put("ftpUrl", ftpUrl);
		data.put("httpUrl", httpUrl);
		rspJson.put("data", data);
	}
	
}
