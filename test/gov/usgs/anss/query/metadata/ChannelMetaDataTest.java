/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.metadata;

import gov.usgs.anss.query.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
public class ChannelMetaDataTest {

    private static MetaDataServer metaDataServer;
    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    public ChannelMetaDataTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

        metaDataServer = new MetaDataServer(
                QueryProperties.getNeicMetadataServerIP(),
                QueryProperties.getNeicMetadataServerPort());

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

    // TODO this test is brittle, it depends on the meta data server.
    // Can we stub the server?
    @Test
    public void testGetStationMetaData() {
        ChannelMetaData md = metaDataServer.getChannelMetaData(new SeedName("NZ", "WEL  ", "HHZ", "10"),
                new DateTime(2009, 1, 1, 11, 11, 11, 0, tz));
        assertTrue("Network ", md.getNetwork().equals("NZ"));
        assertTrue("Code ", md.getCode().equals("WEL  "));
        assertTrue("component", md.getComponent().equals("HHZ"));
        assertTrue("location ", md.getLocation().equals("10"));
        assertEquals("longitude", 174.76802d, md.getLongitude(), 0);
        assertEquals("latitude", -41.28576d, md.getLatitude(), 0);
        assertEquals("elevation", 138.0d, md.getElevation(), 0);
        assertEquals("dip", -90.0d, md.getDip(), 0);
        assertEquals("azimuth", 0.0d, md.getAzimuth(), 0);
    }

    // TODO this test is brittle, it depends on the meta data server.
    // Can we stub the server?
    @Test
    public void testGetStationMetaDataNull() {
        ChannelMetaData md = metaDataServer.getChannelMetaData(new SeedName("NZ", "ZZZZZ", "HHZ", "10"),
                new DateTime(2009, 1, 1, 11, 11, 11, 0, tz));
        assertTrue("Network ", md.getNetwork().equals("NZ"));
        assertTrue("Code ", md.getCode().equals("ZZZZZ"));
        assertTrue("component", md.getComponent().equals("HHZ"));
        assertTrue("location ", md.getLocation().equals("10"));
        assertEquals("longitude", Double.MIN_VALUE, md.getLongitude(), 0);
        assertEquals("latitude", Double.MIN_VALUE, md.getLatitude(), 0);
        assertEquals("elevation", Double.MIN_VALUE, md.getElevation(), 0);
        assertEquals("dip", Double.MIN_VALUE, md.getDip(), 0);
        assertEquals("azimuth", Double.MIN_VALUE, md.getAzimuth(), 0);
    }
}