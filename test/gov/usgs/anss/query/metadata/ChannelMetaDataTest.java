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

    private static MetaDataServerMock metaDataServer;
    private static MetaDataQuery mdq;
    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    public ChannelMetaDataTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

        metaDataServer = new MetaDataServerMock(
                QueryProperties.getNeicMetadataServerIP(),
                QueryProperties.getNeicMetadataServerPort());

        mdq = new MetaDataQuery(metaDataServer);
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

    @Test
    public void testGetStationMetaData() {
        //awk '{print "\""$0"\\n\" +" }' 200901011111.WEL.HHZ.10.NZ.sac.pz | more
        String s = "* CHANNEL(NSCL)NZWEL  HHZ10\n" +
                "* NETWORK      NZ\n" +
                "* STATION      WEL  \n" +
                "* COMPONENT    HHZ\n" +
                "* LOCATION     10\n" +
                "* CREATED      2010/01/22 01:53:48.35\n" +
                "* EFFECTIVE    2008-12-18 01:15:00.0\n" +
                "* ENDDATE      2037-12-31 23:59:59.0\n" +
                "* PARSE FLAGS        \n" +
                "* INPUT UNIT   NM\n" +
                "* OUTPUT UNIT  COUNT\n" +
                "* DESCRIPTION  Wellington\n" +
                "* RATE (HZ)    100.0\n" +
                "* OWNER        New Zealand National Seismograph Network\n" +
                "* COORD(NEIC)  NZ WEL: -41.2858  174.7680  138.0\n" +
                "* COORD(SEED)  NZ WEL: -41.2858  174.7680  138.0\n" +
                "* ORIENTATION  NZ WEL   10 HHZ:     0.0 -90.0    0.0\n" +
                "* LAT-SEED     -41.28576\n" +
                "* LONG-SEED    174.76802\n" +
                "* ELEV-SEED    138.0\n" +
                "* DEPTH        0.0\n" +
                "* DIP          -90.0\n" +
                "* AZIMUTH      0.0\n" +
                "* INSTRMNTTYPE Guralp CMG-3ESP\n" +
                "* INSTRMNTGAIN 2.0000E03\n" +
                "* SENS-SEED    8.3886E08\n" +
                "* SENS-CALC    8.3886E08\n" +
                "* A0-SEED      5.7157E08\n" +
                "* A0-CALC      5.7157E08\n" +
                "* SEEDFLAGS    CG\n" +
                "* ****\n" +
                "CONSTANT              4.7947E+08\n" +
                "ZEROS   3\n" +
                "         0.0000E+00   0.0000E+00\n" +
                "         0.0000E+00   0.0000E+00\n" +
                "         0.0000E+00   0.0000E+00\n" +
                "POLES   5\n" +
                "        -7.4016E-02   7.4016E-02\n" +
                "        -7.4016E-02  -7.4016E-02\n" +
                "        -1.1310E+03   0.0000E+00\n" +
                "        -1.0053E+03   0.0000E+00\n" +
                "        -5.0265E+02   0.0000E+00\n" +
                "* <EOE>\n" +
                "* <EOR>\n";

        metaDataServer.setS(s);

        ChannelMetaData md = mdq.getChannelMetaData(new NSCL("NZ", "WEL  ", "HHZ", "10"),
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
        metaDataServer.setS("");
        ChannelMetaData md = mdq.getChannelMetaData(new NSCL("NZ", "ZZZZZ", "HHZ", "10"),
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
