package com.illidan.framework.core;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 调用spring方法，扫描全包文件
 * @author yuhui.sl
 *
 */
public class ParsePath
{
	public static void main(String[] args) throws IOException
	{
		//spring扫描文件代码位置：
		//1、ComponentScanBeanDefinitionParser
		//2、ClassPathBeanDefinitionScanner
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		
		Resource[] resources = resolver.getResources("classpath*:*/**/*.class");
		for(int i=0; i<resources.length; i++)
		{
			System.out.println(resources[i]);
		}
	}
}
