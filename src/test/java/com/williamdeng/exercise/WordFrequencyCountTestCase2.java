package com.williamdeng.exercise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

public class WordFrequencyCountTestCase2 extends TestCase {

	WordFrequencyCount wfc = new WordFrequencyCount();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		wfc.records.add(Arrays.asList(new String("red,yellow,green,black").split(",")));
		wfc.records.add(Arrays.asList(new String("red,green,blue,black").split(",")));
		wfc.records.add(Arrays.asList(new String("yellow,green,blue").split(",")));
		wfc.records.add(Arrays.asList(new String("yellow,blue,black").split(",")));
	}


	@Test
	public void testGetWordFrequency() {
		List<String> queryWordsList = new ArrayList<String>();
		queryWordsList.add("black");
		queryWordsList.add("green");
		// {"blue": 1, "red": 2, "yellow": 1}
		List<String> expected = Arrays.asList("blue", "red", "red", "yellow");

		List<String> returned = wfc.getWordFrequency(queryWordsList);
		Collections.sort(expected);
		Collections.sort(returned);
		assertEquals(expected, returned);
	}

	@Test
	public void testFormatoutput() {
		String returned = wfc.formatOutput(Arrays.asList("blue", "red", "red", "yellow"));
		String expected = "{\"blue\": 1, \"red\": 2, \"yellow\": 1}";
		assertEquals(expected, returned);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

