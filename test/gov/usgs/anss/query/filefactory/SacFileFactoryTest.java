/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import edu.sc.seis.TauP.SacTimeSeriesTestUtil;
import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.query.filefactory.CWBDataServerMSEEDMock;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import gov.usgs.anss.query.metadata.MetaDataQuery;
import gov.usgs.anss.seed.MiniSeed;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeSet;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlFactory;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
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
public class SacFileFactoryTest {

    public SacFileFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of makeTimeSeries method, of class SacFileFactory.
     * Load the MiniSeed treeset from a miniseed file in the test data jar.
     * Load the SacTimeSeries object from the corrosponding sac file in the test data jar.
     * Compare data.
     */
    @Test
    public void testMakeTimeSeries() throws Exception {
        System.out.println("makeTimeSeries");

        CWBDataServerMSEEDMock cwbServer = new CWBDataServerMSEEDMock("dummy", 666);
        cwbServer.loadMSEEDFiles(new String[]{"/miniseed-data/test-one/NZMRZ__HHN10.ms"});

          //-b "2009/01/01 00:00:00" -d 1800
        DateTime begin = new DateTime(2009, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
        double duration = 1800;
        Integer fill = -12345;
        boolean gaps = true;
        boolean trim = true;
        SacFileFactory instance = new SacFileFactory();

       SacTimeSeries expResult = SacTimeSeriesTestUtil.loadSacTimeSeriesFromClasspath("/sac-data/test-one/NZMRZ__HHN10.sac");

        SacTimeSeries result = instance.makeTimeSeries(cwbServer.getNext(), begin, duration, fill, gaps, trim);

        for (int i = 0; i < result.y.length; i++) {
            assertEquals("data " + i, result.y[i], expResult.y[i], 0.0);
        }
        assertEquals("year", result.nzyear, expResult.nzyear);
    // other header guff we have set here
    }

    @Test
    public void testSetChannelHeader() {
        ChannelMetaData metaData = new ChannelMetaData("test", "test", "test", "test");
        metaData.setLatitude(-41.28576d);
        metaData.setLongitude(174.76802d);
        metaData.setElevation(0.0d);
        metaData.setDepth(0.0d);
        metaData.setAzimuth(0.0d);
        metaData.setDip(-90.0d);

        SacFileFactory instance = new SacFileFactory();

        SacTimeSeries sac = new SacTimeSeries();
        sac.stla = -41.28576d;
        sac.stlo = 174.76802d;
        sac.stel = 138.0d;
        sac.stdp = 0.0;
        sac.cmpaz = 0.0;
        sac.cmpinc = 0.0;

        SacTimeSeries result = instance.setChannelHeader(sac, metaData);

        assertEquals("File", sac, result);
        assertEquals("Lat", sac.stla, result.stla, 0.0);
        assertEquals("Lon", sac.stlo, result.stlo, 0.0);
        assertEquals("Elev", sac.stel, result.stel, 0.0);
        assertEquals("Depth", sac.stdp, result.stdp, 0.0);
        assertEquals("Azimuth", sac.cmpaz, result.cmpaz, 0.0);
        assertEquals("Inc", sac.cmpinc, result.cmpinc, 0.0);
    }

    @Test
    public void testSetEventHeader() throws Exception {
        System.out.println("setEventHeader");
        SacTimeSeries sac = new SacTimeSeries();
        //2007-05-12T07:41:04.874Z - 15.874s
        GregorianCalendar start = new GregorianCalendar(2007, 5, 12, 7, 41, 4);
        sac.nzyear = start.get(Calendar.YEAR);
        sac.nzjday = start.get(Calendar.DAY_OF_YEAR);
        sac.nzhour = start.get(Calendar.HOUR_OF_DAY);
        sac.nzmin = start.get(Calendar.MINUTE);
        sac.nzsec = start.get(Calendar.SECOND);
        sac.nzmsec = start.get(Calendar.MILLISECOND);

        Quakeml quakeml = new QuakemlFactory().getQuakeml(getClass().getResourceAsStream("/quakeml-data/test-one/quakeml_2732452.xml"));
        SacFileFactory instance = new SacFileFactory();

        SacTimeSeries result = instance.setEventHeader(sac, quakeml);
        assertEquals("o", 15.874, result.o);
        assertEquals("ko", "2732452g", result.ko);
    }

    @Test
    public void testOutputFile() {
        System.out.println("outputFile");
        SacTimeSeries timeSeries = null;
        SacFileFactory instance = new SacFileFactory();
        instance.outputFile(timeSeries);
        fail("The test case is a prototype.");
    }
}