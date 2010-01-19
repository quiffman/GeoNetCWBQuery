/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
public class CwbHoldingsTest {

    private static CwbHoldings cwb;
       private static DateTimeZone tz = DateTimeZone.forID("UTC");

    public CwbHoldingsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        cwb = new CwbHoldings(QueryProperties.getGeoNetCwbIP(), QueryProperties.getGeoNetCwbPort());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testLscToNsc() {
        assertTrue("lsc 1", CwbHoldings.lscToNscl("NZTRVZ VKI01 #days=1    09_001").equals("NZTRVZ VKI01"));
        assertTrue("lsc 2", CwbHoldings.lscToNscl("NZTSZ  ACE01 #days=1    09_001").equals("NZTSZ  ACE01"));
        assertTrue("lsc 3", CwbHoldings.lscToNscl("NZTSZ  HHE10 #days=1    09_001").equals("NZTSZ  HHE10"));
        assertTrue("lsc 4", CwbHoldings.lscToNscl("NZTSZ  HHN10 #days=1    09_001").equals("NZTSZ  HHN10"));
    }

    @Test
    public void testListChannels() {
        System.out.println("listChannels");
        DateTime begin = new DateTime(2009, 1, 1, 11, 11, 11, 0, tz);
        Double duration = 1800.00;
        String expResult = "";
        String result = cwb.listChannels(begin, duration);
        //System.out.println(result);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

}