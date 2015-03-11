package com.illidan.framework.core;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.dozer.CustomConverter;

/**
 * dozer测试框架：自定义dozer converter类
 * 
 * @author yuhui.sl
 * 
 */
public class CustomConverterMapping
{
	private Class classA;
	private Class classB;

	private CustomConverter converter;

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

	public CustomConverter getConverter()
	{
		return converter;
	}

	public void setConverter(CustomConverter converter)
	{
		this.converter = converter;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
	}
}
