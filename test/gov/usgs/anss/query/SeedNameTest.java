/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query;

import gov.usgs.anss.query.SeedName.Format;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richardg
 */
public class SeedNameTest {

    public SeedNameTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * Test of stringToSeedName method, of class SeedName.
	 */
	@Test
	public void testStringToSeedName() {
		System.out.println("stringToSeedName");
		String input = "NZWEL  BHZ10";
		String n="NZ", s="WEL  ", c="BHZ", l="10";
		SeedName expResult = new SeedName(n,s,c,l);
		SeedName result = SeedName.stringToSeedName(SeedName.Format.NSCL, input);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getNetwork method, of class SeedName.
	 */
	@Test
	public void testGetNetwork() {
		System.out.println("getNetwork");
		SeedName instance = null;
		String expResult = "";
		String result = instance.getNetwork();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setNetwork method, of class SeedName.
	 */
	@Test
	public void testSetNetwork() {
		System.out.println("setNetwork");
		String network = "";
		SeedName instance = null;
		instance.setNetwork(network);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getStation method, of class SeedName.
	 */
	@Test
	public void testGetStation() {
		System.out.println("getStation");
		SeedName instance = null;
		String expResult = "";
		String result = instance.getStation();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setStation method, of class SeedName.
	 */
	@Test
	public void testSetStation() {
		System.out.println("setStation");
		String station = "";
		SeedName instance = null;
		instance.setStation(station);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getChannel method, of class SeedName.
	 */
	@Test
	public void testGetChannel() {
		System.out.println("getChannel");
		SeedName instance = null;
		String expResult = "";
		String result = instance.getChannel();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setChannel method, of class SeedName.
	 */
	@Test
	public void testSetChannel() {
		System.out.println("setChannel");
		String channel = "";
		SeedName instance = null;
		instance.setChannel(channel);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getLocation method, of class SeedName.
	 */
	@Test
	public void testGetLocation() {
		System.out.println("getLocation");
		SeedName instance = null;
		String expResult = "";
		String result = instance.getLocation();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of setLocation method, of class SeedName.
	 */
	@Test
	public void testSetLocation() {
		System.out.println("setLocation");
		String location = "";
		SeedName instance = null;
		instance.setLocation(location);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of nsclStringToSeedName method, of class SeedName.
	 */
	@Test
	public void testNsclStringToSeedName() {
		System.out.println("nsclStringToSeedName");
		String input = "NZWEL  BHZ10";
		String n="NZ", s="WEL  ", c="BHZ", l="10";
		SeedName expResult = new SeedName(n,s,c,l);
		SeedName result = SeedName.nsclStringToSeedName(input);
		assertEquals(expResult, result);
	}

	/**
	 * Test of equals method, of class SeedName.
	 */
	@Test
	public void testEquals() {
		System.out.println("equals");
		Object obj = null;
		SeedName instance = new SeedName("NZ", "WEL  ", "BHZ", "10");
		boolean expResult = false;
		boolean result = instance.equals(obj);
		assertEquals("in-equal when other SeedName is null.", expResult, result);

		obj = new SeedName("NZ", "WEL  ", "BHZ", "10");
		expResult = true;
		result = instance.equals(obj);
		assertEquals("equal when all NSCL components are equal.", expResult, result);

		obj = new SeedName("", "WEL  ", "BHZ", "10");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching network.", expResult, result);

		obj = new SeedName("NZ", "", "BHZ", "10");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching station.", expResult, result);

		obj = new SeedName("NZ", "WEL  ", "", "10");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching channel.", expResult, result);

		obj = new SeedName("", "WEL  ", "BHZ", "");
		expResult = false;
		result = instance.equals(obj);
		assertEquals("unmatching location.", expResult, result);
	}

	/**
	 * Test of toString method, of class SeedName.
	 */
	@Test
	public void testToString() {
		System.out.println("toString");
		SeedName instance = null;
		String expResult = "";
		String result = instance.toString();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

}