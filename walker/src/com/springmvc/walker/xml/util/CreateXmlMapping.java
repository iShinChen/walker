package com.springmvc.walker.xml.util;

import java.util.List;

import org.dom4j.Element;

import com.springmvc.walker.constant.MappingConstant;
import com.springmvc.walker.xml.entity.MovieEntity;
import com.springmvc.walker.xml.entity.PictureEntity;
import com.springmvc.walker.xml.entity.ProgramEntity;
import com.springmvc.walker.xml.entity.XmlEntity;

public class CreateXmlMapping {

	/**
	 * 生成合集-分集Mapping对象
	 * @param xml
	 * @param maps
	 * @return
	 */
	public static Element CreateS_P_Mapping(XmlEntity xml,Element maps){
		ProgramEntity program = xml.getProgram();
		if(null == program) return maps;
		Element map = maps.addElement("Mapping").addAttribute("Action", MappingConstant.ACTION_REGIST)
				.addAttribute("ElementType", "Program")
				.addAttribute("ElementID", program.getPROGRAM_ID())
				.addAttribute("ElementCode", program.getPROGRAM_ID())
				.addAttribute("ParentType", "Series")
				.addAttribute("ParentID", program.getSERIES_ID())
				.addAttribute("ParentCode", program.getSERIES_ID());
		map.addElement("Property").addAttribute("Name", "Sequence")
			.addText("".equals(program.getSERIALNO())?"1":program.getSERIALNO());
		return maps;
	}

	/**
	 * 生成分集-片源Mapping对象
	 * @param xml
	 * @param maps
	 * @return
	 */
	public static Element CreateP_M_Mapping(XmlEntity xml,Element maps){
		MovieEntity movie = xml.getMovie();
		if(null == movie) return maps;
		maps.addElement("Mapping").addAttribute("Action", MappingConstant.ACTION_REGIST)
			.addAttribute("ElementType", "Movie")
			.addAttribute("ElementID", movie.getMOVIE_ID())
			.addAttribute("ElementCode", movie.getMOVIE_ID())
			.addAttribute("ParentType", "Program")
			.addAttribute("ParentID", movie.getPROGRAM_ID())
			.addAttribute("ParentCode", movie.getPROGRAM_ID());
		return maps;
	}
	
	/**
	 * 生成合集-图片Mapping对象
	 * @param xml
	 * @param maps
	 * @return
	 */
	public static Element CreateS_Pic_Mapping(XmlEntity xml,Element maps){
		List<PictureEntity> pictures = xml.getPicture();
		if(null ==pictures || pictures.size() == 0) return maps;
		for(PictureEntity picture : pictures){
			Element map = maps.addElement("Mapping").addAttribute("Action", MappingConstant.ACTION_REGIST)
					.addAttribute("ElementType", "Series")
					.addAttribute("ElementID", picture.getSERIES_ID())
					.addAttribute("ElementCode", picture.getSERIES_ID())
					.addAttribute("ParentType", "Picture")
					.addAttribute("ParentID", picture.getPICTURE_ID())
					.addAttribute("ParentCode", picture.getPICTURE_ID());
			map.addElement("Property").addAttribute("Name", "Type")
				.addText(picture.getTYPE()== null?"":picture.getTYPE());
		}
		return maps;
	}
}
