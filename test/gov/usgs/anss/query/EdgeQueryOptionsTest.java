/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query;

import java.io.Reader;
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
		String[] args = null;
		EdgeQueryOptions instance = null;
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
		EdgeQueryOptions instance = null;
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
		EdgeQueryOptions instance = null;
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
		EdgeQueryOptions instance = null;
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
		EdgeQueryOptions instance = null;
		String expResult = "";
		String result = instance.getSingleQuotedCommand();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

}