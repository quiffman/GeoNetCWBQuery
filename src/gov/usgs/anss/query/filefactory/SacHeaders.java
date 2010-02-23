/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import nz.org.geonet.quakeml.exception.InvalidQuakemlException;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlUtils;
import nz.org.geonet.quakeml.v1_0_1.domain.Event;
import nz.org.geonet.quakeml.v1_0_1.domain.Magnitude;
import nz.org.geonet.quakeml.v1_0_1.domain.Origin;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import nz.org.geonet.quakeml.v1_0_1.report.ArrivalPick;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author geoffc
 */
public class SacHeaders {

    private static final Logger logger = Logger.getLogger(SacHeaders.class.getName());


    static {
        logger.fine("$Id: SacHeaders.java 1995 2010-02-15 01:10:51Z richardg $");
    }

    // QuakeML doens't restrict mag type - these are the
    // values in the GeoNet DB.
    public enum SacMagType {

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

    public enum SacEventType {

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
        NULL(86, "IU (Undetermined or conflicting information)"),
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

    public static SacTimeSeries setEventHeader(
            SacTimeSeries sac,
            DateTime eventOrigin,
            Double eventLat,
            Double eventLon,
            Double eventDepth,
            Double eventMag,
            int sacMagType,
            int sacEventType) {

        if (eventLat == null) {
            eventLat = -12345.0;
        }

        if (eventLon == null) {
            eventLon = -12345.0;
        }

        if (eventDepth == null) {
            eventDepth = -12345.0;
        }

        if (eventMag == null) {
            eventMag = -12345.0;
        }

        // SAC stores year day (nzjday) but not month and day.  
        DateTime start = new DateTime(sac.nzyear, 1, 1, sac.nzhour, sac.nzmin, sac.nzsec, sac.nzmsec, DateTimeZone.UTC);
        start = start.withDayOfYear(sac.nzjday);

        double timeDiff = (start.getMillis() - eventOrigin.getMillis()) / 1000.0d;

        sac.nzyear = eventOrigin.getYear();
        sac.nzjday = eventOrigin.getDayOfYear();
        sac.nzhour = eventOrigin.getHourOfDay();
        sac.nzmin = eventOrigin.getMinuteOfHour();
        sac.nzsec = eventOrigin.getSecondOfMinute();
        sac.nzmsec = eventOrigin.getMillisOfSecond();

        sac.b = sac.b + timeDiff;
        sac.e = sac.e + timeDiff;

        sac.iztype = SacTimeSeries.IO;

        sac.evla = eventLat;
        sac.evlo = eventLon;
        sac.evdp = eventDepth;
        sac.mag = eventMag;
        sac.imagtyp = sacMagType;
        sac.ievtyp = sacEventType;

        sac.lcalda = 1;

        return sac;
    }

    public static SacTimeSeries setEventHeader(SacTimeSeries sac, Quakeml quakeml) {

        Origin origin = null;
        Double magnitude = null;
        Double latitude = null;
        Double longitude = null;
        Double depth = null;
        int magType = sacMagType("MX");
        int eventType = sacMagType("NULL");
        Magnitude mag = null;

        Event event = QuakemlUtils.getFirstEvent(quakeml);
        try {
            origin = QuakemlUtils.getPreferredOrigin(event);
        } catch (InvalidQuakemlException ex) {
            logger.warning("found no origin information in the QuakeML, will not be able to set event headers");
        }

        try {
            mag = QuakemlUtils.getPreferredMagnitude(event);
        } catch (Exception ex) {
            logger.warning("Found no magnitude definition setting to unknown.");
        }
        if (mag != null) {
            magnitude = mag.getMag().getValue();
            magType = sacMagType(mag.getType());
        } else {
            logger.warning("Found no magnitude definition setting to unknown.");
        }

        try {
            latitude = origin.getLatitude().getValue();
        } catch (Exception ex) {
            logger.warning("Found no latitude definition setting to unknown.");
        }

        try {
            longitude = origin.getLongitude().getValue();
        } catch (Exception ex) {
            logger.warning("Found no longitude definition setting to unknown.");
        }

        try {
            depth = origin.getDepth().getValue();
        } catch (Exception ex) {
            logger.warning("Found no depth definition setting to unknown.");
        }

        if (depth != null) {
            depth = depth * 1000.0d;
        }

        try {
            eventType = sacEventType(event.getType().value());
        } catch (Exception ex) {
            logger.warning("Found no event type definition setting to unknown.");
        }

        DateTime eventTime = null;

        if (origin != null) {
            try {
                eventTime = QuakemlUtils.getOriginTime(origin);
            } catch (InvalidQuakemlException ex) {
                logger.warning("Found no event time definition, not updating header.");
            }
        }

        if (eventTime != null) {
            sac = SacHeaders.setEventHeader(sac, eventTime, // eventLat, eventLon, eventDepth, eventMag, sacMagType)
                    latitude,
                    longitude,
                    depth, // assume meters.
                    magnitude,
                    magType,
                    eventType);
        }

        return sac;
    }

    
    public static SacTimeSeries setPhasePicks(SacTimeSeries sac, Quakeml quakeml) {

        Origin origin = null;
        Event event = QuakemlUtils.getFirstEvent(quakeml);
        try {
            origin = QuakemlUtils.getPreferredOrigin(event);
        } catch (InvalidQuakemlException ex) {
            logger.warning("found no origin information in the QuakeML, will not be able to set picks");
        }

        List<ArrivalPick> picks = null;
        if (origin != null) {
            try {
                picks = QuakemlUtils.getArrivalPicksByStationChannel(event, origin, sac.knetwk.trim(), sac.kstnm.trim(), sac.kcmpnm);
            } catch (Exception ex) {
                logger.warning("unable to read phase picks.");
            }
        }

        List<SacPhasePick> phasePicks = new ArrayList<SacPhasePick>();

        for (ArrivalPick pick : picks) {
            String phaseName = (pick.getArrival().getPhase().getValue() + "      ").substring(0, 6);

            try {
                phaseName += (pick.getPick().getEvaluationMode().value().substring(0, 1) + pick.getPick().getEvaluationStatus().value().substring(0, 1));
            } catch (Exception ex) {
                logger.warning("Found no pick evaluation mode in the quakeml.  Still able to set picks.");
            }

            double arrivalTime = pick.getTimeAfterOriginInMillis() / 1000.0d;

            phasePicks.add(new SacPhasePick(phaseName, arrivalTime));
        }

        return SacHeaders.setPhasePicks(sac, phasePicks);
    }

    
    /**
     * Sets the phase pick fields in the SAC header.  There are ten fields
     * available for picks in the SAC header so the list of SacPhasePick is
     * sorted and the first ten written into the header.
     *
     * For more details on the SAC header see:
     * http://www.iris.edu/manuals/sac/SAC_Manuals/FileFormatPt2.html
     *
     * @param sac
     * @param phasePicks
     * @return
     */
    public static SacTimeSeries setPhasePicks(SacTimeSeries sac, List<SacPhasePick> phasePicks) {

        Collections.sort(phasePicks);

        Iterator<SacPhasePick> iter = phasePicks.iterator();

        // The SAC header has fields kt[0-9] and t[0-9]
        // There is no way to iterate them - they are all
        //  explictly named so see if we have enough data
        // to set each one.
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt0 = pick.getPhaseName();
            sac.t0 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt1 = pick.getPhaseName();
            sac.t1 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt2 = pick.getPhaseName();
            sac.t2 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt3 = pick.getPhaseName();
            sac.t3 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt4 = pick.getPhaseName();
            sac.t4 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt5 = pick.getPhaseName();
            sac.t5 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt6 = pick.getPhaseName();
            sac.t6 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt7 = pick.getPhaseName();
            sac.t7 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt8 = pick.getPhaseName();
            sac.t8 = pick.getTimeAfterOriginInSeconds();
        }
        if (iter.hasNext()) {
            SacPhasePick pick = iter.next();
            sac.kt9 = pick.getPhaseName();
            sac.t9 = pick.getTimeAfterOriginInSeconds();
        }

        return sac;
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
            num = SacMagType.valueOf(magType).magNum();
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
