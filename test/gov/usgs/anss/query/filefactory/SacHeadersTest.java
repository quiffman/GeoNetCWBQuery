/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlFactory;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlUtils;
import nz.org.geonet.quakeml.v1_0_1.domain.Event;
import nz.org.geonet.quakeml.v1_0_1.domain.Magnitude;
import nz.org.geonet.quakeml.v1_0_1.domain.Origin;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
public class SacHeadersTest {

    public SacHeadersTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    // For SAC header refer to http://www.iris.edu/manuals/sac/SAC_Manuals/FileFormatPt2.html
//    @Test
//    public void testSetEventHeader() throws Exception {
//        System.out.println("setEventHeader");
//        SacTimeSeries sac = new SacTimeSeries();
//
//        sac.evla = -40.60804d;
//        sac.evlo = 176.13933d;
//        sac.evdp = 17946.3d; // meters
//        sac.mag = 4.389d;
//        sac.lcalda = 54; // calculate DIST AZ BAZ and GCARC from station and event coords.
////       sac.dist = ;
////       sac.az = ;
////       sac.baz =;
////       sac.gcarc = ;
//
//
//
//
//        Quakeml quakeml = new QuakemlFactory().getQuakeml(SacHeadersTest.class.getResourceAsStream("/quakeml-data/test-one/quakeml_2732452.xml"));
//
//        Event event = QuakemlUtils.getFirstEvent(quakeml);
//        Origin origin = QuakemlUtils.getPreferredOrigin(event);
//        // Paul will add a getPreferredMag()
//        System.out.println("mags " + " " +
//                event.getMagnitude().get(0).getMag().getValue() + " " +
//                event.getMagnitude().get(0).getType());
//
//        SacFileFactory instance = new SacFileFactory();
//
//        SacTimeSeries result = instance.setEventHeader(sac, quakeml);
//        assertEquals("o", 15.874, result.o);
//        assertEquals("ko", "2732452g", result.ko);
//    }
    @Test
    public void testSacEventType() {
        assertEquals("earthquake", 40, SacHeaders.sacEventType("earthquake"));
        assertEquals("explosion", 79, SacHeaders.sacEventType("explosion"));
        assertEquals("quarry blast", 70, SacHeaders.sacEventType("quarry blast"));
        assertEquals("chemical explosion", 43, SacHeaders.sacEventType("chemical explosion"));
        assertEquals("nuclear explosion", 37, SacHeaders.sacEventType("nuclear explosion"));
        assertEquals("landslide", 82, SacHeaders.sacEventType("landslide"));
        assertEquals("debris avalanch", 82, SacHeaders.sacEventType("debris avalanche"));
        assertEquals("rockslide", 82, SacHeaders.sacEventType("rockslide"));
        assertEquals("mine collapse", 82, SacHeaders.sacEventType("mine collapse"));
        assertEquals("volcanic eruption", 82, SacHeaders.sacEventType("volcanic eruption"));
        assertEquals("meteor impact", 82, SacHeaders.sacEventType("meteor impact"));
        assertEquals("plane crash", 82, SacHeaders.sacEventType("plane crash"));
        assertEquals("building collapse", 82, SacHeaders.sacEventType("building collapse"));
        assertEquals("sonic boom", 82, SacHeaders.sacEventType("sonic boom"));
        assertEquals("not existing", 86, SacHeaders.sacEventType("not existing"));
        assertEquals("null", 86, SacHeaders.sacEventType("null"));
        assertEquals("other", 44, SacHeaders.sacEventType("other"));
        assertEquals("somthing made up", 44, SacHeaders.sacEventType("other"));
        assertEquals("quarry blast multiple spaces", 70, SacHeaders.sacEventType(" quarry  blast"));
    }

    @Test
    public void testSacMagType() {
        assertEquals("MB", 52, SacHeaders.sacMagType("MB"));
        assertEquals("MS", 53, SacHeaders.sacMagType("MS"));
        assertEquals("ML", 54, SacHeaders.sacMagType("ML"));
        assertEquals("MW", 55, SacHeaders.sacMagType("MW"));
        assertEquals("MD", 56, SacHeaders.sacMagType("MD"));
        assertEquals("MX", 57, SacHeaders.sacMagType("MX"));
        assertEquals("MB", 57, SacHeaders.sacMagType("something made up"));
    }
}