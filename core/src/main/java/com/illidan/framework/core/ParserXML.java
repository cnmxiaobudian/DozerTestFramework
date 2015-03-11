package com.illidan.framework.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 解析XML测试函数
 * @author yuhui.sl
 *
 */
public class ParserXML
{
	public Document getDocument(String mapping) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(this.getClass().getClassLoader().getResourceAsStream(mapping));
		return doc;
	}
	
	private static String getTextValue(Element ele, String tagName)
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
	
	public static void main(String[] args)
	{
		ParserXML parserXML = new ParserXML();
		List<CustomClassMapping> customClassMappings = new ArrayList<CustomClassMapping>();
		Document document;
		try
		{
			document = parserXML.getDocument("dozermap/dozer-campaign-mapping-test.xml");
			NodeList mappingEntries = document.getElementsByTagName("mapping");
			mappingEntries.toString();
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
			System.out.println(customClassMappings);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
