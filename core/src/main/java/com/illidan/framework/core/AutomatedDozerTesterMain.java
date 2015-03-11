package com.illidan.framework.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.dozer.DozerBeanMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * dozer测试框架:对外暴露的主功能函数
 * TODO：框架待完成的功能模块包括
 * 1、自定义的get-set支持
 * 2、自定义的converter支持
 * @author yuhui.sl
 *
 */
public class AutomatedDozerTesterMain
{
	//默认文件夹名称
	public static final String DEFALUT_DOZER_FILE_PATH = "../ultron-biz/src/test/resources/dozermap";
	
	//dozer对象
	private DozerBeanMapper dozerBeanMapper;
	
	//是否需要双向匹配模式
	private Boolean isTwoWayMapping = false;
	
	//是否需要默认属性name匹配校验
	private Boolean isDefaultMapping = false;
	
	//文件夹名称
	private String filePath = DEFALUT_DOZER_FILE_PATH;
	
	//自定义的元类型特殊值
	private Map srcFixedProperties;
	
	public AutomatedDozerTesterMain(DozerBeanMapper beanMapper, Boolean isTwoWayMapping, Boolean isDefaultMapping, Map srcFixedProperties)
	{
		this.dozerBeanMapper = beanMapper;
		this.isTwoWayMapping = isTwoWayMapping;
		this.isDefaultMapping = isDefaultMapping;
		this.srcFixedProperties = srcFixedProperties;
	}
	
	public AutomatedDozerTesterMain(DozerBeanMapper beanMapper, Boolean isTwoWayMapping, String filePath, Boolean isDefaultMapping, Map srcFixedProperties)
	{
		this.dozerBeanMapper = beanMapper;
		this.isTwoWayMapping = isTwoWayMapping;
		this.filePath = filePath;
		this.isDefaultMapping = isDefaultMapping;
		this.srcFixedProperties = srcFixedProperties;
	}
	
	/**
	 * 主功能函数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public List<AutomatedDozerTestReport> startAutomatedDozerTest() throws Exception
	{
		List<AutomatedDozerTestReport> reports = new ArrayList<AutomatedDozerTestReport>();

		File path = new File(filePath);
		if(path.isDirectory())
		{
			String[] filelist = path.list();
			
            for (int i = 0; i < filelist.length; i++) 
            {
                File dozerFile = new File(filePath + "\\" + filelist[i]);
                
                //解析xml，获得map-id
                getMapIdAndSetReports(dozerFile, reports, filelist[i]);
            }
		}
		else
		{
			getMapIdAndSetReports(path, reports, path.getName());
		}
		
		return reports;
	}
	
	/**
	 * 解析XML获得map-id，并且添加自动测试报告
	 * @param dozerFile
	 * @param reports
	 * @param fileName
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private void getMapIdAndSetReports(File dozerFile, List<AutomatedDozerTestReport> reports, String fileName) throws Exception
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(dozerFile);
		
		NodeList mappingEntries = doc.getElementsByTagName("mapping");
		for(int j = 0; j < mappingEntries.getLength(); j++)
		{
			Element mappingElement = (Element) mappingEntries.item(j);
			String classA = DozerMappingFileParser.getTextValue(mappingElement, "class-a");
			String classB = DozerMappingFileParser.getTextValue(mappingElement, "class-b");
			String mapId = mappingElement.getAttribute("map-id");

			reports.add(new AutomatedDozerTester(dozerBeanMapper, fileName, isTwoWayMapping).
					testClassesWithMappingId(Class.forName(classA.trim()), Class.forName(classB.trim()), dozerBeanMapper, mapId, isDefaultMapping, srcFixedProperties));
		}
	}
	
	public DozerBeanMapper getDozerBeanMapper()
	{
		return dozerBeanMapper;
	}

	public void setDozerBeanMapper(DozerBeanMapper dozerBeanMapper)
	{
		this.dozerBeanMapper = dozerBeanMapper;
	}

	public Boolean getIsTwoWayMapping()
	{
		return isTwoWayMapping;
	}

	public void setIsTwoWayMapping(Boolean isTwoWayMapping)
	{
		this.isTwoWayMapping = isTwoWayMapping;
	}

	public String getFileName()
	{
		return filePath;
	}

	public void setFileName(String fileName)
	{
		this.filePath = fileName;
	}

	public Boolean getIsDefaultMapping()
	{
		return isDefaultMapping;
	}

	public void setIsDefaultMapping(Boolean isDefaultMapping)
	{
		this.isDefaultMapping = isDefaultMapping;
	}

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	public Map getSrcFixedProperties()
	{
		return srcFixedProperties;
	}

	public void setSrcFixedProperties(Map srcFixedProperties)
	{
		this.srcFixedProperties = srcFixedProperties;
	}
}
