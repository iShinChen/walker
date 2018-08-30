package com.springmvc.walker.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class JaxbUtil {
	private final static Map<String, JAXBContext> jaxbContextMap = new HashMap<String, JAXBContext>();

	/**
	 * java实体类转xml
	 */
	public static String toXML(Object obj, String encode, boolean format,
			boolean fragment) {
		try {
			JAXBContext jaxbContext = jaxbContextMap.get(obj.getClass()
					.getName());
			if (jaxbContext == null) {
				// 如果每次都调用JAXBContext.newInstance方法，会导致性能急剧下降
				jaxbContext = JAXBContext.newInstance(obj.getClass());
				jaxbContextMap.put(obj.getClass().getName(), jaxbContext);
			}

			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encode); // 编码格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, format); // 是否格式化生成的xml串
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, fragment); // 是否省略xm头声明信息
			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * xml转java实体类
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXML(String xml, Class<T> valueType) {
		try {
			JAXBContext jaxbContext = jaxbContextMap.get(valueType.getName());
			if (jaxbContext == null) {
				jaxbContext = JAXBContext.newInstance(valueType);
				jaxbContextMap.put(valueType.getName(), jaxbContext);
			}
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			return (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
