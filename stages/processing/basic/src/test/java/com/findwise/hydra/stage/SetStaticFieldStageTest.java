package com.findwise.hydra.stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.findwise.hydra.local.LocalDocument;
import com.findwise.hydra.stage.SetStaticFieldStage.Policy;

public class SetStaticFieldStageTest {

	private SetStaticFieldStage stage;

	private LocalDocument doc;

	@Before
	public void setUp() {
		stage = new SetStaticFieldStage();
		doc = new LocalDocument();
		doc.putContentField("string", "string");
		List<Object> list = new ArrayList<Object>();
		list.addAll(Arrays.asList(new Object[] { 1, "x" }));
		doc.putContentField("list", list);

		doc.putContentField("int", 10);
	}

	@Test
	public void testAdd() throws Exception {
		init(Policy.ADD);

		stage.process(doc);

		Assert.assertEquals("value", doc.getContentField("empty"));
		Assert.assertTrue(doc.getContentField("string") instanceof List);
		Assert.assertTrue(((List<?>) doc.getContentField("string")).contains(1));
		Assert.assertTrue(((List<?>) doc.getContentField("string")).contains("string"));
		Assert.assertTrue(((List<?>) doc.getContentField("list")).contains("x"));
		Assert.assertTrue(((List<?>) doc.getContentField("list")).contains(1));
		Assert.assertTrue(((List<?>) doc.getContentField("list")).contains("value"));
	}
	
	@Test
	public void testOverwrite() throws Exception {
		init(Policy.OVERWRITE);

		stage.process(doc);

		Assert.assertEquals("value", doc.getContentField("empty"));
		Assert.assertEquals(1, doc.getContentField("string"));
		Assert.assertEquals("value", doc.getContentField("list"));
	}
	
	@Test
	public void testSkip() throws Exception {
		init(Policy.SKIP);

		stage.process(doc);

		Assert.assertEquals("value", doc.getContentField("empty"));
		Assert.assertEquals("string", doc.getContentField("string"));
		Assert.assertTrue(((List<?>) doc.getContentField("list")).contains("x"));
		Assert.assertTrue(((List<?>) doc.getContentField("list")).contains(1));
	}
	

	@Test
	public void testThrowPolicyNoCollision() throws Exception {
		init(Policy.THROW);

		LocalDocument doc = new LocalDocument();
		stage.process(doc);
		Assert.assertEquals("value", doc.getContentField("empty"));
		Assert.assertEquals(1, doc.getContentField("string"));
		Assert.assertEquals("value", doc.getContentField("list"));
	}	
	
	@Test(expected = ProcessException.class)
	public void testThrowPolicyWithCollision() throws Exception {
		init(Policy.THROW);

		stage.process(doc);
	}

	public void init(Policy policy) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("overwritePolicy", policy);
		HashMap<String, Object> fieldValueMap = new HashMap<String, Object>();
		fieldValueMap.put("empty", "value");
		fieldValueMap.put("string", 1);
		fieldValueMap.put("list", "value");
		map.put("fieldValueMap", fieldValueMap);
		stage.setParameters(map);

		stage.init();
	}
}
