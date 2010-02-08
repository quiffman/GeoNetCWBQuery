/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.data;

import java.util.TreeSet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
public class CWBDataServerMSEEDMockTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testServerMock() {

        CWBDataServerMSEEDMock cwbServer = new CWBDataServerMSEEDMock("dummy.net", 666);
        String[] filenames = {
            "/miniseed-data/test-one/NZMRZ__HHE10.ms",
            "/miniseed-data/test-one/NZMRZ__HHN10.ms",
            "/miniseed-data/test-one/NZMRZ__HHZ10.ms"
        };

        cwbServer.loadMSEEDFiles(filenames);
       
        assertEquals("has records", cwbServer.hasNext(), true);
        assertEquals("First record", "NZMRZ  HHE10", cwbServer.getNext().first().getSeedName());
        TreeSet set = cwbServer.getNext();
        set = cwbServer.getNext();
        assertEquals("More records", cwbServer.hasNext(), false);
    }
}
