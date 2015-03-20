package com.illidan.framework.core;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
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
	//可以解析jarFile的jar文件，并将jar中的文件过滤为class返回
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
				result.add(clazz.replace("/", ".").replace(".class", ""));
			}
		}
		
		return result;
	}
	
	//加载jar文件
	//TODO 只需要加载jar文件即可，不需要new出来，然后调用dozer测试框架的class，进行测试
	public void loadClass(String jarPath) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		//解析jar文件，获得class
		List<String> clazzs = parseJar(jarPath);
		
		//手动加载jar
		URL url = new URL("file:" + jarPath);
		URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url});
		
		//newInstance
		for(String clazz : clazzs)
		{
			Class class1 = urlClassLoader.loadClass(clazz);
			if(!class1.isInterface())
			{
				class1.newInstance();
			}
		}
	}
	
	public static void main(String[] args)
	{
		ParseJarFile parseJarFile = new ParseJarFile();
		try
		{
			List<String> clazzs = parseJarFile.parseJar("../core/src/main/resources/jarFile/ultron-dal.jar");
			URL url = new URL("file:../core/src/main/resources/jarFile/ultron-dal.jar");
			URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url});
			for(String clazz : clazzs)
			{
				Class class1 = urlClassLoader.loadClass(clazz);
				if(!class1.isInterface())
				{
					class1.newInstance();
				}
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
