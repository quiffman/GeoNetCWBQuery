/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb;

import gov.usgs.anss.query.*;
import gov.usgs.anss.query.cwb.CWBQuery;
import java.util.ArrayList;
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

    private static CWBServerMock cwbServer;
    private static CWBQuery cwbQuery;
    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    public CwbHoldingsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        cwbServer = new CWBServerMock("mocked.host", 9999);
        cwbQuery = new CWBQuery(cwbServer);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testLscToNsc() {
        assertEquals("lsc 1", CWBQuery.lscToNscl("NZTRVZ VKI01 #days=1    09_001"), NSCL.stringToNSCL("NZTRVZ VKI01"));
        assertEquals("lsc 2", CWBQuery.lscToNscl("NZTSZ  ACE01 #days=1    09_001"), NSCL.stringToNSCL("NZTSZ  ACE01"));
        assertEquals("lsc 3", CWBQuery.lscToNscl("NZTSZ  HHE10 #days=1    09_001"), NSCL.stringToNSCL("NZTSZ  HHE10"));
        assertEquals("lsc 4", CWBQuery.lscToNscl("NZTSZ  HHN10 #days=1    09_001"), NSCL.stringToNSCL("NZTSZ  HHN10"));
        assertNull("Empty string", CWBQuery.lscToNscl(""));
        assertNull("There are...", CWBQuery.lscToNscl("There are 248 stations"));
    }

    @Test
    public void testListChannels() {
        String channels = "NZBFZ  BNE20 #days=1    09_001\n" +
                "NZBFZ  BNN20 #days=1    09_001\n" +
                "NZBFZ  BNZ20 #days=1    09_001\n" +
                "\n" +
                "There are 1709 channels ncomp=0 # stations=155 today=10_023\n";

        cwbServer.setChannels(channels);

        DateTime begin = new DateTime(2009, 1, 1, 11, 11, 11, 0, tz);
        Double duration = 1800.00;
        ArrayList result = cwbQuery.listChannels(begin, duration);
        assertTrue("Number of results", result.size() == 3);
        assertEquals("First nscl", result.get(0), NSCL.stringToNSCL("NZBFZ  BNE20"));

    }
}