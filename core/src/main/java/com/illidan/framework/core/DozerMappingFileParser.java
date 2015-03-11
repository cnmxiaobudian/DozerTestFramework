package com.illidan.framework.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * dozer测试框架：解析dozer文件
 * 
 * @author yuhui.sl
 */
public class DozerMappingFileParser
{
	private Document loadMappingDocument(DozerBeanMapper beanMapper, String mappingFileName) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		//输入fileName进行解析
		String mappingFile = new String();
		for(String file : beanMapper.getMappingFiles())
		{
			if(file.contains(mappingFileName))
			{
				mappingFile = file;
				break;
			}
		}
		Assert.assertTrue(null != mappingFile && !mappingFile.isEmpty());
		
		Document doc = docBuilder.parse(this.getClass().getClassLoader()
				.getResourceAsStream(mappingFile));
		return doc;
	}

	public static String getTextValue(Element ele, String tagName)
	{
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0)
		{
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
	
	public List<CustomClassMapping> getCustomClassMappings(DozerBeanMapper dozerBeanMapper, String mappingFileName) throws Exception
	{
		List<CustomClassMapping> customClassMappings = new ArrayList<CustomClassMapping>();
		Document doc = loadMappingDocument(dozerBeanMapper, mappingFileName);

		NodeList mappingEntries = doc.getElementsByTagName("mapping");
		for (int i = 0; i < mappingEntries.getLength(); i++)
		{
			Element mappingElement = (Element) mappingEntries.item(i);
			String classA = getTextValue(mappingElement, "class-a");
			String classB = getTextValue(mappingElement, "class-b");
			CustomClassMapping customClassMapping = new CustomClassMapping();
			customClassMapping.setClassA(Class.forName(classA.trim()));
			customClassMapping.setClassB(Class.forName(classB.trim()));

			NodeList fieldEntries = mappingElement
					.getElementsByTagName("field");
			for (int j = 0; j < fieldEntries.getLength(); j++)
			{
				Element fieldElement = (Element) fieldEntries.item(j);
				if (fieldElement == null)
				{
					continue;
				}
				String fieldA = getTextValue(fieldElement, "a");
				String fieldB = getTextValue(fieldElement, "b");
				customClassMapping.getCustomMapping().put(fieldA, fieldB);
			}

			customClassMappings.add(customClassMapping);
		}

		return customClassMappings;
	}

	public List<CustomConverterMapping> getCustomConverterMappings(DozerBeanMapper beanMapper, String mappingFileName) throws Exception
	{
		List<CustomConverterMapping> converterMappings = new ArrayList<CustomConverterMapping>();
		Document doc = loadMappingDocument(beanMapper, mappingFileName);
		NodeList converterEntries = doc.getElementsByTagName("converter");
		for (int i = 0; i < converterEntries.getLength(); i++)
		{
			CustomConverterMapping customConverterMapping = new CustomConverterMapping();

			Element converterElement = (Element) converterEntries.item(i);
			if (converterElement == null)
			{
				continue;
			}

			String converterClass = converterElement.getAttribute("type");
			customConverterMapping.setConverter((CustomConverter) Class
					.forName(converterClass).getConstructor().newInstance());
			String classA = getTextValue(converterElement, "class-a");
			String classB = getTextValue(converterElement, "class-b");
			customConverterMapping.setClassA(Class.forName(classA.trim()));
			customConverterMapping.setClassB(Class.forName(classB.trim()));

			converterMappings.add(customConverterMapping);
		}
		return converterMappings;
	}
}
