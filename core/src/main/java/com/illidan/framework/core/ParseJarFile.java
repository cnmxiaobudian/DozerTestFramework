package com.illidan.framework.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 解析jar文件
 * @author yuhui.sl
 *
 */
public class ParseJarFile
{
	//可以解析jarFile的jar文件
	public List<String> parseJar(String jarPath) throws IOException
	{
		List<String> result = new ArrayList<String>();
		
		JarFile jarFile = new JarFile(jarPath);
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while(enumeration.hasMoreElements())
		{
			JarEntry jarEntry = (JarEntry)enumeration.nextElement();
			String clazz = jarEntry.getName();
			if(clazz.endsWith("class") && !clazz.contains("Builder"))
			{
				result.add(clazz.replace("/", "."));
			}
		}
		
		return result;
	}
	
	//TODO 有一个非常严重的问题待解决：目前虽然可以解析到JarFile的class类，但是没办法new出来，因为包不再当前工程中。
	//TODO 判断class是一个javaBean
	public static void main(String[] args)
	{
		ParseJarFile parseJarFile = new ParseJarFile();
		try
		{
			//Object obj = Class.forName("com.taobao.ad.ultron.dal.vo.AccountItemVO");
			List<String> clazzs = parseJarFile.parseJar("../core/src/main/resources/jarFile/ultron-dal.jar");
			for(String clazz : clazzs)
			{
				Object object = Class.forName(clazz);
				System.out.println(object);
				System.out.println(clazz);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
