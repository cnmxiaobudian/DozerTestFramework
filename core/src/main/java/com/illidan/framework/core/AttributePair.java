package com.illidan.framework.core;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * dozer测试框架：辅助类型
 * @author yuhui.sl
 *
 */
public class AttributePair
{
	private String attributeOne;
	private String attributeTwo;

	public AttributePair(String attributeOne, String attributeTwo)
	{
		this.attributeOne = attributeOne;
		this.attributeTwo = attributeTwo;
	}

	public String getAttributeOne()
	{
		return attributeOne;
	}

	public void setAttributeOne(String attributeOne)
	{
		this.attributeOne = attributeOne;
	}

	public String getAttributeTwo()
	{
		return attributeTwo;
	}

	public void setAttributeTwo(String attributeTwo)
	{
		this.attributeTwo = attributeTwo;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
	}

}
