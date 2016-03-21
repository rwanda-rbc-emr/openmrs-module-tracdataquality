package org.openmrs.module.tracdataquality.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class DataQualityServiceTest extends BaseModuleContextSensitiveTest {

	@Test
	public void testIfServiceExists() {
		Assert.assertNotNull(Context.getService(DataQualityService.class));
	}
}
