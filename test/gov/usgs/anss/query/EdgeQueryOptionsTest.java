/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query;

import gov.usgs.anss.query.EdgeQueryOptions.OutputType;
import java.io.Reader;
import java.util.Date;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic unit testing of EdgeQueryOptions.
 * @author richardg
 */
public class EdgeQueryOptionsTest {

    public EdgeQueryOptionsTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

	/**
	 * Test of parse method, of class EdgeQueryOptions.
	 */
	@Test
	public void testParse() {
		System.out.println("parse");
		String[] args = new String[0];;
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.parse(args);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of isFileMode method, of class EdgeQueryOptions.
	 */
	@Test
	public void testIsFileMode() {
		System.out.println("isFileMode");
		EdgeQueryOptions instance = new EdgeQueryOptions(new String[]{"-f", "file.txt"});
		assertTrue("File mode should return true", instance.isFileMode());
	}

	/**
	 * Test of isListQuery method, of class EdgeQueryOptions.
	 */
	@Test
	public void testIsListQuery() {
		System.out.println("isListQuery");
		EdgeQueryOptions instance = new EdgeQueryOptions(new String[]{"-ls"});
		assertTrue("File mode should return true", instance.isListQuery());
		instance = new EdgeQueryOptions(new String[]{"-lsc"});
		assertTrue("File mode should return true", instance.isListQuery());
	}


	/**
	 * Test of getHelp method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetHelp() {
		System.out.println("getHelp");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		String expResult = "";
		String result = instance.getHelp();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getUsage method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetUsage() {
		System.out.println("getUsage");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		String expResult = "";
		String result = instance.getUsage();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getAsReader method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetAsReader() throws Exception {
		System.out.println("getAsReader");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		Reader expResult = null;
		Reader result = instance.getAsReader();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getSingleQuotedCommand method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetSingleQuotedCommand() {
		System.out.println("getSingleQuotedCommand");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		String expResult = "";
		String result = instance.getSingleQuotedCommand();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of isValid method, of class EdgeQueryOptions.
	 */
	@Test
	public void testIsValid() {
		System.out.println("isValid");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		boolean expResult = false;
		boolean result = instance.isValid();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getOutputter method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetOutputter() {
		System.out.println("getOutputter");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		Outputer expResult = null;
		Outputer result = instance.getOutputter();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getCompareLength method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetCompareLength() {
		System.out.println("getCompareLength");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		int expResult = 0;
		int result = instance.getCompareLength();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setBegin method, of class EdgeQueryOptions.
	 */
	@Test
	public void testSetBegin_String() {
		System.out.println("setBegin");
		String beginTime = "";
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.setBegin(beginTime);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getDuration method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetDuration() {
		System.out.println("getDuration");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		Double expResult = null;
		Double result = instance.getDuration();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setDuration method, of class EdgeQueryOptions.
	 */
	@Test
	public void testSetDuration() {
		System.out.println("setDuration");
		Double duration = null;
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.setDuration(duration);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getSeedname method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetSeedname() {
		System.out.println("getSeedname");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		String expResult = "";
		String result = instance.getSeedname();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setSeedname method, of class EdgeQueryOptions.
	 */
	@Test
	public void testSetSeedname() {
		System.out.println("setSeedname");
		String seedname = "";
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.setSeedname(seedname);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getType method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetType() {
		System.out.println("getType");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		OutputType expResult = null;
		OutputType result = instance.getType();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setType method, of class EdgeQueryOptions.
	 */
	@Test
	public void testSetType_EdgeQueryOptionsOutputType() {
		System.out.println("setType");
		OutputType type = null;
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.setType(type);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setType method, of class EdgeQueryOptions.
	 */
	@Test
	public void testSetType_String() {
		System.out.println("setType");
		String type = "";
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.setType(type);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getOffset method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetOffset() {
		System.out.println("getOffset");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		long expResult = 0L;
		long result = instance.getOffset();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setOffset method, of class EdgeQueryOptions.
	 */
	@Test
	public void testSetOffset() {
		System.out.println("setOffset");
		long offset = 0L;
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.setOffset(offset);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getBegin method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetBegin() {
		System.out.println("getBegin");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		DateTime expResult = null;
		DateTime result = instance.getBegin();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getBeginAsDate method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetBeginAsDate() {
		System.out.println("getBeginAsDate");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		Date expResult = null;
		Date result = instance.getBeginAsDate();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getBeginAsString method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetBeginAsString() {
		System.out.println("getBeginAsString");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		String expResult = "";
		String result = instance.getBeginAsString();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getBeginWithOffset method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetBeginWithOffset() {
		System.out.println("getBeginWithOffset");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		DateTime expResult = null;
		DateTime result = instance.getBeginWithOffset();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getBeginWithOffsetAsDate method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetBeginWithOffsetAsDate() {
		System.out.println("getBeginWithOffsetAsDate");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		Date expResult = null;
		Date result = instance.getBeginWithOffsetAsDate();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getBeginWithOffsetAsString method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetBeginWithOffsetAsString() {
		System.out.println("getBeginWithOffsetAsString");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		String expResult = "";
		String result = instance.getBeginWithOffsetAsString();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setBegin method, of class EdgeQueryOptions.
	 */
	@Test
	public void testSetBegin_DateTime() {
		System.out.println("setBegin");
		DateTime begin = null;
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.setBegin(begin);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getEvent method, of class EdgeQueryOptions.
	 */
	@Test
	public void testGetEvent() {
		System.out.println("getEvent");
		EdgeQueryOptions instance = new EdgeQueryOptions();
		Quakeml expResult = null;
		Quakeml result = instance.getEvent();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setEvent method, of class EdgeQueryOptions.
	 */
	@Test
	public void testSetEvent_Quakeml() {
		System.out.println("setEvent");
		Quakeml event = null;
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.setEvent(event);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setEvent method, of class EdgeQueryOptions.
	 */
	@Test
	public void testSetEvent_String() {
		System.out.println("setEvent");
		String publicId = "";
		EdgeQueryOptions instance = new EdgeQueryOptions();
		instance.setEvent(publicId);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

}
