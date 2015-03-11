package com.illidan.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.illidan.framework.core.AttributePair;
import com.illidan.framework.core.AutomatedDozerTestReport;
import com.illidan.framework.core.AutomatedDozerTester;
import com.illidan.framework.core.AutomatedDozerTesterMain;
import com.illidan.framework.util.ObjectUtil;

/**
 * Dozer测试框架：测试类
 * @author yuhui.sl
 *
 */
public class DozerTest
{
	@Autowired
	private Mapper mapper;
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testDozer() throws Exception
	{
		//1、单独文件方式
		AutomatedDozerTestReport<TestVO, TestDO> report = new AutomatedDozerTester((DozerBeanMapper)mapper, "dozer-campaign-mapping-test", false).
				testClassesWithMappingId(TestVO.class, TestDO.class, (DozerBeanMapper)mapper, "CampaignVOToCampaignDO", false, null);
		List<AttributePair> result = report.getMappingFailures();
		assertTrue(result.isEmpty());
		
		//2、文件夹方式，可以指定文件夹或者文件
		List<AutomatedDozerTestReport> reportsWithFile = new AutomatedDozerTesterMain((DozerBeanMapper)mapper, false, 
				"../ultron-biz/src/test/resources/dozermap/dozer-campaign-mapping-test.xml", false, null).startAutomatedDozerTest();
		System.out.println(reportsWithFile);
		
		//3、默认文件夹方式
		List<AutomatedDozerTestReport> reportsNoFile = new AutomatedDozerTesterMain((DozerBeanMapper)mapper, false, false, null)
															.startAutomatedDozerTest();
		System.out.println(reportsNoFile);
	}
	
	/*
	 * 测试可自定义的对象生成
	 */
	@Test
	public void testJavaBean() throws Exception
	{
		Map fix = new HashMap();
		fix.put("campaignTitle", "test");
		fix.put("campaignId", 1L);
		TestVO campaignVO = ObjectUtil.getInstanceWithValues(TestVO.class, fix);
		System.out.print(campaignVO);
	}
}
