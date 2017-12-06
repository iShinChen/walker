package com.springmvc.walker.controller.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springmvc.framework.entity.PageResultBean;
import com.springmvc.framework.entity.ResultBean;
import com.springmvc.framework.util.ParamUtil;
import com.springmvc.framework.util.PrintWriterUtil;
import com.springmvc.walker.service.xml.MovieService;
import com.springmvc.walker.xml.entity.MovieEntity;

@Controller
@RequestMapping("/movie") 
public class MovieController {

	private final static Logger logger = Logger.getLogger(MovieController.class);
	
	@Autowired
	private MovieService movieService;
	
	/**
	 * 根据分集ID获取片源信息
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getMovieByProgramId")
	public void getMovieByProgramId(HttpServletRequest request,HttpServletResponse response) {
		PageResultBean result = new PageResultBean();
		try {
			String programId = request.getParameter("programId");
			List<MovieEntity> movies = movieService.getMoviesByProgramId(programId);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for(MovieEntity movie : movies){
				list.add(ParamUtil.convertBeanToMap(movie));
			}
			result.setPageResultBean(list.size(),1, list, true);
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("根据分集ID获取片源信息发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 片源上线
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/onlineMovie")
	public void onlineMovie(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String movieId = request.getParameter("movieId");
			if(movieService.updateMovieStatus(movieId, "01")) {
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("片源上线失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("片源上线发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}

	/**
	 * 片源下线
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/offlineMovie")
	public void offlineMovie(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			String movieId = request.getParameter("movieId");
			if(movieService.updateMovieStatus(movieId, "02")) {
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("片源下线失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("片源下线发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
	
	/**
	 * 片源删除
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/deleteMovie")
	public void deleteMovie(HttpServletRequest request,HttpServletResponse response) {
		ResultBean result = new ResultBean();
		try {
			if(movieService.deleteMovie(request.getParameter("movieId"))) {
				result.setSuccess(true);
			}else{
				result.setSuccess(false);
				result.setErr_msg("片源删除失败。");
			}
		} catch (Exception e) {
			logger.error("程序异常", e);
			result.setSuccess(false);
			result.setErr_msg("片源删除发生异常。");
		}
		PrintWriterUtil.write(response, result);
	}
}
