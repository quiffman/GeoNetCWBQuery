/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.cwb.data.CWBDataServer;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import gov.usgs.anss.query.metadata.MetaDataServer;
import java.util.TreeSet;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;
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

    @Test
    public void testSetCWBDataServer() {
        System.out.println("setCWBDataServer");
        CWBDataServer cwbServer = null;
        SacFileFactory instance = new SacFileFactory();
        instance.setCWBDataServer(cwbServer);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetMetaDataServer() {
        System.out.println("setMetaDataServer");
        MetaDataServer metaDataServer = null;
        SacFileFactory instance = new SacFileFactory();
        instance.setMetaDataServer(metaDataServer);
        fail("The test case is a prototype.");
    }

    @Test
    public void testMakeFiles() {
        System.out.println("makeFiles");
        DateTime being = null;
        double duration = 0.0;
        String nsclSelectString = "";
        String mask = "";
        Integer fill = null;
        boolean trim = false;
        Quakeml quakeml = null;
        SacFileFactory instance = new SacFileFactory();
        instance.makeFiles(being, duration, nsclSelectString, mask, fill, trim, quakeml);
        fail("The test case is a prototype.");
    }

    @Test
    public void testMakeTimeSeries() {
        System.out.println("makeTimeSeries");
        TreeSet miniSeed = null;
        SacFileFactory instance = new SacFileFactory();
        SacTimeSeries expResult = null;
        SacTimeSeries result = instance.makeTimeSeries(miniSeed);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetChannelHeader() {
        System.out.println("setChannelHeader");
        SacTimeSeries timeSeries = null;
        ChannelMetaData metaData = null;
        SacFileFactory instance = new SacFileFactory();
        SacTimeSeries expResult = null;
        SacTimeSeries result = instance.setChannelHeader(timeSeries, metaData);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetEventHeader() {
        System.out.println("setEventHeader");
        SacTimeSeries timeSeries = null;
        Quakeml quakeml = null;
        SacFileFactory instance = new SacFileFactory();
        SacTimeSeries expResult = null;
        SacTimeSeries result = instance.setEventHeader(timeSeries, quakeml);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
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