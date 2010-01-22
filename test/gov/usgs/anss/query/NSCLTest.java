/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richardg
 */
public class NSCLTest {

    public NSCLTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}


	/**
	 * Test of getNetwork method, of class NSCL.
	 */
	@Test
	public void testGetNetwork() {
		System.out.println("getNetwork");
		NSCL instance = null;
		String expResult = "";
		String result = instance.getNetwork();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setNetwork method, of class NSCL.
	 */
	@Test
	public void testSetNetwork() {
		System.out.println("setNetwork");
		String network = "";
		NSCL instance = null;
		instance.setNetwork(network);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getStation method, of class NSCL.
	 */
	@Test
	public void testGetStation() {
		System.out.println("getStation");
		NSCL instance = null;
		String expResult = "";
		String result = instance.getStation();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setStation method, of class NSCL.
	 */
	@Test
	public void testSetStation() {
		System.out.println("setStation");
		String station = "";
		NSCL instance = null;
		instance.setStation(station);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getChannel method, of class NSCL.
	 */
	@Test
	public void testGetChannel() {
		System.out.println("getChannel");
		NSCL instance = null;
		String expResult = "";
		String result = instance.getChannel();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setChannel method, of class NSCL.
	 */
	@Test
	public void testSetChannel() {
		System.out.println("setChannel");
		String channel = "";
		NSCL instance = null;
		instance.setChannel(channel);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getLocation method, of class NSCL.
	 */
	@Test
	public void testGetLocation() {
		System.out.println("getLocation");
		NSCL instance = null;
		String expResult = "";
		String result = instance.getLocation();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setLocation method, of class NSCL.
	 */
	@Test
	public void testSetLocation() {
		System.out.println("setLocation");
		String location = "";
		NSCL instance = null;
		instance.setLocation(location);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of stringToNSCL method, of class NSCL.
	 */
	@Test
	public void testNsclStringToSeedName() {
		System.out.println("nsclStringToSeedName");
		String input = "NZWEL  BHZ10";
		String n="NZ", s="WEL  ", c="BHZ", l="10";
		NSCL expResult = new NSCL(n,s,c,l);
		NSCL result = NSCL.stringToNSCL(input);
		assertEquals(expResult, result);
	}

	/**
	 * Test of equals method, of class NSCL.
	 */
	@Test
	public void testEquals() {
		System.out.println("equals");
		Object obj = null;
		NSCL instance = new NSCL("NZ", "WEL  ", "BHZ", "10");
		boolean expResult = false;
		boolean result = instance.equals(obj);
		assertEquals("in-equal when other SeedName is null.", expResult, result);

		obj = new NSCL("NZ", "WEL  ", "BHZ", "10");
		expResult = true;
		result = instance.equals(obj);
		assertEquals("equal when all NSCL components are equal.", expResult, result);

		obj = new NSCL("", "WEL  ", "BHZ", "10");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching network.", expResult, result);

		obj = new NSCL("NZ", "", "BHZ", "10");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching station.", expResult, result);

		obj = new NSCL("NZ", "WEL  ", "", "10");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching channel.", expResult, result);

		obj = new NSCL("", "WEL  ", "BHZ", "");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching location.", expResult, result);
	}

	/**
	 * Test of toString method, of class NSCL.
	 */
	@Test
	public void testToString() {
		System.out.println("toString");
		NSCL instance = null;
		String expResult = "";
		String result = instance.toString();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

}