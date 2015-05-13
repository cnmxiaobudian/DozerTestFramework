package com.illidan.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * 测试工具类
 * @author yuhui.sl
 *
 */
public class ObjectUtil
{
	//最大的Collection长度，默认值，可修改
	private static final int MAX_COLLECTION_SIZE = 6;
	
	//随机String串，用于数据初始化，可修改
	private static final List<String> randomStrings = Arrays.asList("NanJing", "ShangHai", "HangZhou", "ChengDu", "ShenZhen", 
			"HongKong", "ChongQing", "BeiJing", "Cario", "XiAn", "YuHui Trip", "Moon", "Wind", "Sea", "Mountain");
	
	/**
	 * 实例化src，递归方法
	 * 目前支持任何复杂类型的随机新建，如果基础类型有新增，需要修改getValueFromFactory方法创建基础类型的随机输出
	 * 目前支持的基础类型包括：Integer，Double，Long，Boolean，Date，XMLGregorianCalendar，String，Enum，BigInteger，BigDecimal
	 * @param inputClass
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T getInstanceWithValues(Class<T> inputClass, Map fixedProperties) throws Exception
	{
		T instance = inputClass.getConstructor().newInstance();
		List<Field> declaredFields= Arrays.asList(inputClass.getDeclaredFields());
		
		List<Field> allFields = new ArrayList<Field>();
		allFields.addAll(declaredFields);
		
		//赋予父类对象的值
		Class father = inputClass.getSuperclass();
		while(null != father && null != father.getDeclaredFields() && father != Object.class)
		{
			List<Field> fatherFields = Arrays.asList(father.getDeclaredFields());
			allFields.addAll(fatherFields);
			
			father = father.getSuperclass();
		}
		
		for (Field field : allFields)
		{
			//排除序列化参数
			if (field.getName().equals("serialVersionUID")
					|| Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			//设置可修改
			field.setAccessible(true);
			//Map中的key和value很有可能不是标准的class，而是List或者Map，需要使用类型判断进行处理，递归处理
			if(Map.class.isAssignableFrom(field.getType()))
			{
				ParameterizedType type = (ParameterizedType) field.getGenericType();
				Map map = new TreeMap();
				for(int i = 0; i < MAX_COLLECTION_SIZE; i++)
				{
					Object keyObject = null;
					Object valueObject = null;
					
					if(type.getActualTypeArguments()[0] instanceof ParameterizedType)
					{
						ParameterizedType mapType = (ParameterizedType) type.getActualTypeArguments()[0];
						keyObject = getFromVector((Class)mapType.getRawType(), field, 0, fixedProperties);
					}
					else if (type.getActualTypeArguments()[0] instanceof Class) 
					{
						Class key = (Class) type.getActualTypeArguments()[0];
						keyObject = getValueFromFactory(key);
						if (keyObject == null)
						{
							keyObject = getInstanceWithValues(key, fixedProperties);
						}
					}
					
					if(type.getActualTypeArguments()[1] instanceof ParameterizedType)
					{
						ParameterizedType mapType = (ParameterizedType) type.getActualTypeArguments()[1];
						valueObject = getFromVector((Class)mapType.getRawType(), field, 1, fixedProperties);
					}
					else if(type.getActualTypeArguments()[1] instanceof Class)
					{
						Class value = (Class) type.getActualTypeArguments()[0];
						valueObject = getValueFromFactory(value);
						if (valueObject == null)
						{
							valueObject = getInstanceWithValues(value, fixedProperties);
						}
					}
					
					map.put(keyObject, valueObject);
				}
				field.set(instance, map);
			}
			else if (Collection.class.isAssignableFrom(field.getType()))
			{
				ParameterizedType type = (ParameterizedType) field.getGenericType();
				Collection coll = new ArrayList();
				
				//重新赋值coll
				if(List.class.isAssignableFrom(field.getType()))
				{
					coll = new ArrayList();
				}
				if(Set.class.isAssignableFrom(field.getType()))
				{
					coll = new HashSet();
				}
				
				for (int i = 0; i < MAX_COLLECTION_SIZE; i++)
				{
					Object object = null;
					
					if(type.getActualTypeArguments()[0] instanceof ParameterizedType)
					{
						ParameterizedType collType = (ParameterizedType) type.getActualTypeArguments()[0];
						object = getFromVector((Class)collType.getRawType(), field, 0, fixedProperties);
					}
					else if (type.getActualTypeArguments()[0] instanceof Class)
					{
						Class typeClass = (Class) type.getActualTypeArguments()[0];
						object = getValueFromFactory(typeClass);
						if (object == null)
						{
							object = getInstanceWithValues(typeClass, fixedProperties);
						}
					}
					
					coll.add(object);
				}
				field.set(instance, coll);
			} else
			{
				Object attributeValue = getValueFromFactory(field.getType());
				if (attributeValue == null)
				{
					attributeValue = getInstanceWithValues(field.getType(), fixedProperties);
				}
				field.set(instance, attributeValue);
			}
		}

        //赋予特定值
        if(null != fixedProperties && !fixedProperties.isEmpty())
        {
            for(Field field : allFields)
            {
                //设置可修改
                field.setAccessible(true);
                Object value = fixedProperties.get(field.getName());
                if(null != value)
                {
                    field.set(instance, value);
                }
            }
        }
        
		return instance;
	}
	
	/**
	 * 工厂制造器，创建模拟方法
	 * TODO 如果有新增的类型，需要在这里创建工厂方法
	 * @param classType
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	private static Object getValueFromFactory(Class classType) throws DatatypeConfigurationException
	{
		Object output = null;
		if (classType == Integer.TYPE || classType == Integer.class)
		{
			output = new Random().nextInt();
		} else if (classType == Double.TYPE || classType == Double.class)
		{
			output = new Random().nextDouble();
		} else if (classType == Long.TYPE || classType == Long.class)
		{
			output = new Random().nextLong();
		} else if (classType == Boolean.TYPE || classType == Boolean.class)
		{
			output = new Random().nextBoolean();
		} else if (classType == Date.class)
		{
			output = new Date(new Random().nextLong());
		} else if (classType == XMLGregorianCalendar.class)
		{
			output = toXmlGregorianCalendar(new Date(new Random().nextLong()));
		} else if (classType == String.class)
		{
			output = randomStrings.get(new Random().nextInt(randomStrings
					.size()));
		} else if (classType.isEnum())
		{
			Object[] enumConstants = classType.getEnumConstants();
			output = enumConstants[new Random().nextInt(enumConstants.length)];
		} else if (classType == BigInteger.class)
		{
			output = new BigInteger("" + new Random().nextInt());
		}else if (classType == BigDecimal.class) 
		{
			output = new BigDecimal(new Random().nextLong());
		}
		
		return output;
	}
	
	private static XMLGregorianCalendar toXmlGregorianCalendar(Date date)
			throws DatatypeConfigurationException
	{
		GregorianCalendar gregCal = new GregorianCalendar();
		gregCal.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
	}
	
	/**
	 * 获得集合中的参数
	 * @param input
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static <T> Object getFromVector(Class<T> input, Field field, Integer keyOrValue, Map fixedProperties) throws Exception
	{
		if(Map.class.isAssignableFrom(input))
		{
			ParameterizedType type = (ParameterizedType) field.getGenericType();
			if(type.getActualTypeArguments()[keyOrValue] instanceof Class)
			{
				Class mapValue = (Class) type.getActualTypeArguments()[keyOrValue];
				Object instanceWithKeys = getValueFromFactory(mapValue);
				if (instanceWithKeys == null)
				{
					instanceWithKeys = getInstanceWithValues(mapValue, fixedProperties);
				}
				return instanceWithKeys;
			}
			else if(type.getActualTypeArguments()[keyOrValue] instanceof ParameterizedType)
			{
				ParameterizedType mapType = (ParameterizedType) type.getActualTypeArguments()[keyOrValue];
				return getFromVector((Class)mapType.getRawType(), field, keyOrValue, fixedProperties);
			}
		}
		else if(Collection.class.isAssignableFrom(input))
		{
			ParameterizedType type = (ParameterizedType) field.getGenericType();
			Collection coll = new ArrayList();
			
			//重新赋值coll
			if(List.class.isAssignableFrom(field.getType()))
			{
				coll = new ArrayList();
			}
			if(Set.class.isAssignableFrom(field.getType()))
			{
				coll = new HashSet();
			}
			
			if(type.getActualTypeArguments()[0] instanceof Class)
			{
				Class typeClass = (Class) type.getActualTypeArguments()[0];
				Object instanceWithValues = getValueFromFactory(typeClass);
				if (instanceWithValues == null)
				{
					instanceWithValues = getInstanceWithValues(typeClass, fixedProperties);
				}
				coll.add(instanceWithValues);
				return coll;
			}
			else if (type.getActualTypeArguments()[0] instanceof ParameterizedType) 
			{
				ParameterizedType collType = (ParameterizedType) type.getActualTypeArguments()[0];
				return getFromVector((Class)collType.getRawType(), field, keyOrValue, fixedProperties);
			}
		}
		return null;
	}
}
