/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;

/**
 *
 * @author geoffc
 */
public class SacHeaders {


    // QuakeML doens't restrict mag type - these are the
    // values in the GeoNet DB.
    private enum SacMagType {

        MB(52, "IMB (Bodywave Magnitude)"),
        MS(53, "IMS (Surfacewave Magnitude)"),
        ML(54, "IML (Local Magnitude)"),
        MW(55, "IMW (Moment Magnitude)"),
        MD(56, "IMD (Duration Magnitude)"),
        MX(57, "IMX (User Defined Magnitude)");
        private int magNum;
        private String description;

        SacMagType(int magNum, String description) {
            this.magNum = magNum;
            this.description = description;
        }

        public int magNum() {
            return magNum;
        }
    }

    private enum SacEventType {

        EARTHQUAKE(40, "IQUAKE (Earthquake)"),
        EXPLOSION(79, "IEX (Other explosion)"),
        QUARRYBLAST(70, "IQB (Quarry or mine blast confirmed by quarry)"),
        CHEMICALEXPLOSION(43, "ICHEM (Chemical explosion)"),
        NUCLEAREXPLOSION(37, "INUCL (Nuclear event)"),
        LANDSLIDE(82, "IO_ (Other source of known origin)"),
        DEBRISAVALANCHE(82, "IO_ (Other source of known origin)"),
        ROCKSLIDE(82, "IO_ (Other source of known origin)"),
        MINECOLLAPSE(82, "IO_ (Other source of known origin)"),
        VOLCANICERUPTION(82, "IO_ (Other source of known origin)"),
        METEORIMPACT(82, "IO_ (Other source of known origin)"),
        PLANECRASH(82, "IO_ (Other source of known origin)"),
        BUILDINGCOLLAPSE(82, "IO_ (Other source of known origin)"),
        SONICBOOM(82, "IO_ (Other source of known origin)"),
        NOTEXISTING(86, "IU (Undetermined or conflicting information)"),
        NULL(86,"IU (Undetermined or conflicting information)"),
        OTHER(44, "IOTHER (Other)");
        private int eventTypeNum;
        private String description;

        SacEventType(int eventTypeNum, String description) {
            this.eventTypeNum = eventTypeNum;
            this.description = description;
        }

        public int eventTypeNum() {
            return eventTypeNum;
        }
    }

    public static SacTimeSeries setEventHeader(SacTimeSeries timeSeries, Quakeml quakeml) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


        public static SacTimeSeries setChannelHeader(SacTimeSeries sac, ChannelMetaData md) {
        if (md.getLatitude() != Double.MIN_VALUE) {
            sac.stla = md.getLatitude();
        }
        if (md.getLongitude() != Double.MIN_VALUE) {
            sac.stlo = md.getLongitude();
        }
        if (md.getElevation() != Double.MIN_VALUE) {
            sac.stel = md.getElevation();
        }
        if (md.getDepth() != Double.MIN_VALUE) {
            sac.stdp = md.getDepth();
        }
        if (md.getAzimuth() != Double.MIN_VALUE) {
            sac.cmpaz = md.getAzimuth();
        }
        if (md.getDip() != Double.MIN_VALUE) {
            sac.cmpinc = (md.getDip() + 90.);   // seed is down from horiz, sac is down from vertical
        }

        return sac;
    }


    public static int sacMagType(String magType) {
        // Provide a default - this is the closest to unknown for magType
        int num = SacMagType.valueOf("MX").magNum();

        try {
            num =SacMagType.valueOf(magType).magNum();
        } catch (Exception e) {
        }
  
        return num;
    }

    public static int sacEventType(String eventType) {
        int num = SacEventType.valueOf("NULL").eventTypeNum();

        try {
            num = SacEventType.valueOf(eventType.replaceAll("\\s+", "").toUpperCase()).eventTypeNum();
        } catch (Exception e) {
        }

        return num;
    }
}
