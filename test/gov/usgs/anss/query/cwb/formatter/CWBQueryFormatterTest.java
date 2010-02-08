/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.formatter;

import gov.usgs.anss.query.cwb.formatter.CWBQueryFormatter;
import gov.usgs.anss.query.NSCL;
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
public class CWBQueryFormatterTest {

    public CWBQueryFormatterTest() {
    }
    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testListQuery() {
        System.out.println("listQuery");
        DateTime begin = new DateTime(2009, 1, 1, 11, 11, 11, 0, tz);
        Double duration = 1800d;
        String result = CWBQueryFormatter.listChannels(begin, duration);
        assertEquals("list query 1", "'-b' '2009/01/01 11:11:11' '-d' '1800.0' '-lsc'\n", result);

    }

    @Test
    public void testMiniSEEDQuery() {
        NSCL nscl = NSCL.stringToNSCL("NZMRZ..HHZ10");
        DateTime begin = new DateTime(2009, 1, 1, 0, 0, 0, 0, tz);
        Double duration = 1800d;
        String result = CWBQueryFormatter.miniSEED(begin, duration, nscl);
        assertEquals("data query 1", "'-b' '2009/01/01 00:00:00' '-s' 'NZMRZ..HHZ10' '-d' '1800.0'\n", result);
    }
}