package com.illidan.framework.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * dozer测试框架：自定义mapping文件
 * @author yuhui.sl
 */
public class CustomClassMapping
{
	private Class classA;

	private Class classB;

	private Map<String, String> customMapping;

	public Class getClassA()
	{
		return classA;
	}

	public void setClassA(Class classA)
	{
		this.classA = classA;
	}

	public Class getClassB()
	{
		return classB;
	}

	public void setClassB(Class classB)
	{
		this.classB = classB;
	}

	public Map<String, String> getCustomMapping()
	{
		if (customMapping == null)
		{
			customMapping = new HashMap<String, String>();
		}
		return customMapping;
	}

	public void setCustomMapping(Map<String, String> customMapping)
	{
		this.customMapping = customMapping;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
	}
}
