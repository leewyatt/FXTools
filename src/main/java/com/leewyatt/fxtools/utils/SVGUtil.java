package com.leewyatt.fxtools.utils;

import com.leewyatt.fxtools.model.SvgXml;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;
/**
 * @author LeeWyatt
 */
public class SVGUtil {

	public static SvgXml getSvgPath(File svgFile) {
		SvgXml svgXml = new SvgXml();
		// 创建一个DocumentBuilderFactory的对象
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// 创建一个DocumentBuilder的对象
		try {
			// 创建DocumentBuilder对象
			DocumentBuilder db = dbf.newDocumentBuilder();
			// 禁止dtd验证,这里很耗费时间
			db.setEntityResolver((publicId, systemId) -> new InputSource(
					new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes())));

			Document document = db.parse(svgFile);
			parseInfo(svgXml, document);
			parsePath(svgXml, document);

		} catch (Exception e) {
			// e.printStackTrace();
			Logger logger = Logger.getLogger("com.leewyatt.fxtools.utils.SVGUtil");
			logger.severe("Exception: parse svg failed.\t"+e);
			return SvgXml.getErrorSvgXml();
		}

		return svgXml;
	}

	// 解析长宽, viewBox等信息
	private static void parseInfo(SvgXml svgXml, Document document) {
		Node svg = document.getElementsByTagName("svg").item(0);
		NamedNodeMap attrs = svg.getAttributes();
		//推荐是viewBox,但是有的文件写的是viewbox
		Node attrVB = attrs.getNamedItem("viewBox");
		Node attrVB2 = attrs.getNamedItem("viewbox");
		if(attrVB==null&&attrVB2!=null){
			attrVB=attrVB2;
		}
		String[] info = attrVB.getNodeValue().trim().split("\\s+");
		svgXml.setTrueWidth(info[2]);
		svgXml.setTrueHeight(info[3]);

		Node attrWidth = attrs.getNamedItem("width");
		if(attrWidth==null){
			svgXml.setWidth(info[2]);
		}else{
			svgXml.setWidth(attrWidth.getNodeValue().toLowerCase(Locale.ROOT).replace("px", ""));
		}

		Node attrHeight = attrs.getNamedItem("height");
		if(attrHeight==null){
			svgXml.setHeight(info[3]);
		}else{
			svgXml.setHeight(attrHeight.getNodeValue().toLowerCase(Locale.ROOT).replace("px", ""));
		}

	}



	// 解析路径条数 和 路径的内容
	private static void parsePath(SvgXml svgXml, Document document) {
		StringBuilder sb = new StringBuilder(50);
		int count = 0;
		// 获取所有path节点的集合
		NodeList paths = document.getElementsByTagName("path");
		for (int i = 0; i < paths.getLength(); i++) {
			// 通过 item(i)方法 获取一个path节点，nodelist的索引值从0开始
			Node book = paths.item(i);
			// 获取path节点的所有属性集合
			NamedNodeMap attrs = book.getAttributes();
			Node attr = attrs.getNamedItem("d");
			sb.append(attr.getNodeValue());
			count++;
		}
		svgXml.setSvgPath(sb.toString());
		svgXml.setPathCount(count + "");
	}
}
