package com.illidan.framework.core;

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
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;

import com.illidan.framework.util.ObjectUtil;

/**
 * dozer测试框架：主函数
 * @author yuhui.sl
 *
 */
public class AutomatedDozerTester
{
	private final Map<String, CustomClassMapping> customMapping = new HashMap<String, CustomClassMapping>();
	
	private final List<CustomConverterMapping> customConverters = new ArrayList<CustomConverterMapping>();

	public AutomatedDozerTester(DozerBeanMapper beanMapper, String mappingFileName, Boolean isTwoWayMapping) throws Exception
	{
		initializeMapping(beanMapper, mappingFileName, isTwoWayMapping);
	}

	@SuppressWarnings("rawtypes")
	public <T, P> AutomatedDozerTestReport<T, P> testClasses(
			Class<T> sourceClass, Class<P> destinationClass,
			DozerBeanMapper beanMapper, Boolean isDefaultMapping, Map srcFixedProperties) throws Exception
	{
		AutomatedDozerTestReport<T, P> testReport = new AutomatedDozerTestReport<T, P>(
				sourceClass, destinationClass);

		T newSrcInstance = ObjectUtil.getInstanceWithValues(sourceClass, srcFixedProperties);

		P destMappedInstance = beanMapper.map(newSrcInstance, destinationClass);

		testReport.setSourceInstance(newSrcInstance);
		testReport.setDestInstance(destMappedInstance);

		matchAndGenerateReport(testReport, newSrcInstance, destMappedInstance,
				newSrcInstance.getClass().getName(), destMappedInstance.getClass().getName(), isDefaultMapping);

		return testReport;
	}
	
	@SuppressWarnings("rawtypes")
	public <T, P> AutomatedDozerTestReport<T, P> testClassesWithMappingId(
			Class<T> sourceClass, Class<P> destinationClass,
			DozerBeanMapper beanMapper, String mappingId, Boolean isDefaultMapping, Map srcFixedProperties) throws Exception
	{
		AutomatedDozerTestReport<T, P> testReport = new AutomatedDozerTestReport<T, P>(
				sourceClass, destinationClass);

		T newSrcInstance = ObjectUtil.getInstanceWithValues(sourceClass, srcFixedProperties);

		P destMappedInstance = beanMapper.map(newSrcInstance, destinationClass, mappingId);

		testReport.setSourceInstance(newSrcInstance);
		testReport.setDestInstance(destMappedInstance);

		matchAndGenerateReport(testReport, newSrcInstance, destMappedInstance,
				newSrcInstance.getClass().getName(), destMappedInstance.getClass().getName(), isDefaultMapping);

		return testReport;
	}

	private <P, T> void addMappingFailure(AutomatedDozerTestReport testReport,
			String srcName, String destName)
	{
		testReport.getMappingFailures().add(
				new AttributePair(srcName, destName));
	}

	private CustomConverter customConvertersMatch(Object srcValue,
			Object destValue)
	{
		CustomConverter converter = null;
		for (CustomConverterMapping customConverter : customConverters)
		{
			if ((customConverter.getClassA().isAssignableFrom(
					srcValue.getClass()) && customConverter.getClassB()
					.isAssignableFrom(destValue.getClass()))
					|| (customConverter.getClassA().isAssignableFrom(
							destValue.getClass()) && customConverter
							.getClassB().isAssignableFrom(srcValue.getClass())))
			{
				converter = customConverter.getConverter();
				break;
			}
		}
		return converter;
	}
	
	private void initializeMapping(DozerBeanMapper beanMapper, String mappingFileName, Boolean isTwoWayMapping) throws Exception
	{
		DozerMappingFileParser dozerMappingFileParser = new DozerMappingFileParser();
		List<CustomClassMapping> customClassMappings = dozerMappingFileParser
				.getCustomClassMappings(beanMapper, mappingFileName);
		for (CustomClassMapping customClassMapping : customClassMappings)
		{
			customMapping.put(customClassMapping.getClassA().getName()
					+ customClassMapping.getClassB().getName(),
					customClassMapping);
			//默认的双向绑定
			if(isTwoWayMapping)
			{
				customMapping.put(customClassMapping.getClassB().getName()
						+ customClassMapping.getClassA().getName(),
						customClassMapping);
			}
		}
		List<CustomConverterMapping> customConverterMappings = dozerMappingFileParser
				.getCustomConverterMappings(beanMapper, mappingFileName);
		customConverters.addAll(customConverterMappings);
	}
	
	/**
	 * 测试报告，测试预期内容
	 * 预期对象与实际对象的比较，如果有失败，加入failed列表
	 * @param testReport
	 * @param newSrcInstance
	 * @param destMappedInstance
	 * @param srcName
	 * @param destName
	 * @throws IllegalAccessException
	 */
	private <P, T> void matchAndGenerateReport(
			AutomatedDozerTestReport testReport, T newSrcInstance,
			P destMappedInstance, String srcName, String destName, Boolean isDefaultMapping)
			throws IllegalAccessException
	{
		if (newSrcInstance != null && destMappedInstance != null)
		{
			if(Map.class.isAssignableFrom(newSrcInstance.getClass()) && Map.class.isAssignableFrom(destMappedInstance.getClass()))
			{
				Map srcMap = (Map) newSrcInstance;
				Map destMap = (Map) destMappedInstance;
				if(srcMap.size() != destMap.size())
				{
					addMappingFailure(testReport, srcName, destName);
				} else 
				{
					for(Object key : srcMap.keySet())
					{
						matchAndGenerateReport(testReport, srcMap.get(key), destMap.get(key), 
								srcName + "." + key, destName + "." + key, isDefaultMapping);
					}
				}
			}
			else if (Collection.class.isAssignableFrom(newSrcInstance.getClass())
					&& Collection.class.isAssignableFrom(destMappedInstance
							.getClass()))
			{
				List srcColl = (List) newSrcInstance;
				List destColl = (List) destMappedInstance;
				if (srcColl.size() != destColl.size())
				{
					addMappingFailure(testReport, srcName, destName);
				} else
				{
					for (int i = 0; i < srcColl.size(); i++)
					{
						matchAndGenerateReport(testReport, srcColl.get(i),
								destColl.get(i), srcName + "." + srcName,
								destName + "." + destName, isDefaultMapping);
					}
				}
			} else if (newSrcInstance.getClass().isAssignableFrom(
					destMappedInstance.getClass()))
			{
				if (!newSrcInstance.equals(destMappedInstance))
				{
					addMappingFailure(testReport, srcName, destName);
				}
			} else if (newSrcInstance.getClass().isEnum()
					&& destMappedInstance.getClass().isEnum())
			{
				if (!(newSrcInstance.getClass().isEnum()
						&& destMappedInstance.getClass().isEnum()
						&& ((Enum) newSrcInstance).name().equals(
								((Enum) destMappedInstance).name())))
				{
					addMappingFailure(testReport, srcName, destName);
				}
			} else if (customConvertersMatch(newSrcInstance, destMappedInstance) != null)
			{
				CustomConverter converter = customConvertersMatch(
						newSrcInstance, destMappedInstance);
				if (!(destMappedInstance.equals(converter.convert(null,
						newSrcInstance, destMappedInstance.getClass(),
						newSrcInstance.getClass()))))
				{
					addMappingFailure(testReport, srcName, destName);
				}
			} else
			{
				Field[] sourceAttributes = newSrcInstance.getClass()
						.getDeclaredFields();
				Field[] destinationAttributes = destMappedInstance.getClass()
						.getDeclaredFields();

				Map<Field, Field> srcFieldVsDestField = new HashMap<Field, Field>();

				CustomClassMapping customClassMapping = customMapping
						.get(newSrcInstance.getClass().getName()
								+ destMappedInstance.getClass().getName());
				for (Field srcField : sourceAttributes)
				{
					Field destinationField = null;

					if (customClassMapping != null
							&& customClassMapping.getCustomMapping().get(
									srcField.getName()) != null)
					{
						String destFieldName = customClassMapping
								.getCustomMapping().get(srcField.getName());

						for (Field destField : destinationAttributes)
						{
							if (destField.getName().equals(destFieldName))
							{
								destinationField = destField;
								break;
							}
						}
					}
					//默认属性的匹配
					else if(isDefaultMapping)
					{
						for (Field destField : destinationAttributes)
						{
							if (destField.getName().equals(srcField.getName()))
							{
								destinationField = destField;
								break;
							}
						}
					}
					if (destinationField == null)
					{
						testReport.getUnmappedSourceAttributes().add(
								srcName + "." + srcField.getName());
					} else
					{
						srcFieldVsDestField.put(srcField, destinationField);
					}
				}
				for (Field srcField : srcFieldVsDestField.keySet())
				{
					Field destField = srcFieldVsDestField.get(srcField);
					srcField.setAccessible(true);
					destField.setAccessible(true);

					Object srcValue = srcField.get(newSrcInstance);
					Object destValue = destField.get(destMappedInstance);

					matchAndGenerateReport(testReport, srcValue, destValue,
							srcName + "." + srcField.getName(), destName + "."
									+ destField.getName(), isDefaultMapping);
				}
			}
		} else
		{
			addMappingFailure(testReport, srcName, destName);
		}
	}
}
