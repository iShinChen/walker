<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>视频播放</title>

    <link href="<%=basePath%>/jsp/video-js/video-js.css" rel="stylesheet" type="text/css">
    
    <script src="<%=basePath%>/jsp/video-js/video.js"></script>
    <script>
    	videojs.options.flash.swf = "../jsp/video-js/video-js.swf";
  	</script>
  	
</head>
<body>

  <video id="example_video_1" class="video-js vjs-default-skin" controls preload="none" width="640" height="264"
      poster="http://video-js.zencoder.com/oceans-clip.png"
      data-setup="{}">
    <source src="D:/ftp/import/video/201707/06.mp4" type='video/mp4' />
    <source src="D:/ftp/import/video/201707/06.webm" type='video/webm' />
    <source src="D:/ftp/import/video/201707/06.ogg" type='video/ogg' />
    <track kind="captions" src="demo.captions.vtt" srclang="en" label="English"></track><!-- Tracks need an ending tag thanks to IE9 -->
    <track kind="subtitles" src="demo.captions.vtt" srclang="en" label="English"></track><!-- Tracks need an ending tag thanks to IE9 -->
    <p class="vjs-no-js">播放错误：<a href="http://videojs.com/html5-video-support/" target="_blank">supports HTML5 video</a></p>
  </video>

</body>
</html>