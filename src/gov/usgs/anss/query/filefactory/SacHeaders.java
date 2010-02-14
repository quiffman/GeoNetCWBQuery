/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlUtils;
import nz.org.geonet.quakeml.v1_0_1.domain.Arrival;
import nz.org.geonet.quakeml.v1_0_1.domain.Event;
import nz.org.geonet.quakeml.v1_0_1.domain.Magnitude;
import nz.org.geonet.quakeml.v1_0_1.domain.Origin;
import nz.org.geonet.quakeml.v1_0_1.domain.Pick;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author geoffc
 */
public class SacHeaders {

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
            double eventLat,
            double eventLon,
            double eventDepth,
            double eventMag,
            int sacMagType,
            int sacEventType) {

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

        Event event = QuakemlUtils.getFirstEvent(quakeml);
        Origin origin = QuakemlUtils.getPreferredOrigin(event);

        DateTime eventTime = new DateTime(origin.getTime().getValue().toGregorianCalendar().getTimeInMillis(),
                DateTimeZone.forOffsetMillis(origin.getTime().getValue().getTimezone() * 60 * 1000));

        Magnitude mag = QuakemlUtils.getPreferredMagnitude(event);

        return SacHeaders.setEventHeader(sac, eventTime, // eventLat, eventLon, eventDepth, eventMag, sacMagType)
                origin.getLatitude().getValue(),
                origin.getLongitude().getValue(),
                origin.getDepth().getValue() * 1000.0d, // assume meters.
                mag.getMag().getValue(),
                sacMagType(mag.getType()),
                sacEventType(event.getType().value()));
    }

    // Would be more useful in JQuakeml?
    public static HashMap<String, Double> getPhasePicks(Quakeml quakeml, String networkCode, String stationCode, String channelCode) {
        HashMap<String, Double> phasePicks = new HashMap();

        Event event = QuakemlUtils.getFirstEvent(quakeml);
        Origin origin = QuakemlUtils.getPreferredOrigin(event);

        long eventTime = origin.getTime().getValue().toGregorianCalendar().getTimeInMillis();
        List<Pick> picks = event.getPick();

        List<Arrival> arrivals = origin.getArrival();

        for (Arrival arrival : arrivals) {
            Pick pick = QuakemlUtils.getPickAssociatedWithArrival(picks, arrival);

            if (pick.getWaveformID().getNetworkCode().equals(networkCode) &&
                    pick.getWaveformID().getStationCode().equals(stationCode) &&
                    pick.getWaveformID().getChannelCode().equals(channelCode)) {
                double arrivalTime = (pick.getTime().getValue().toGregorianCalendar().getTimeInMillis() - eventTime) / 1000.0;
                phasePicks.put(arrival.getPhase().getValue(), arrivalTime);
            }
        }

        return phasePicks;
    }

    public static SacTimeSeries setPhasePicks(SacTimeSeries sac, Quakeml quakeml) {
        HashMap<String, Double> phasePicks = SacHeaders.getPhasePicks(quakeml, sac.knetwk.trim(), sac.kstnm.trim(), sac.kcmpnm);
        return SacHeaders.setPhasePicks(sac, phasePicks);
    }

    public static SacTimeSeries setPhasePicks(SacTimeSeries sac, HashMap<String, Double> phasePicks) {

        // Sort the picks by time so that if there are more than 10
        // (very unlikey) we plot the 'first' 10 first.
        List list = new LinkedList(phasePicks.entrySet());
        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Iterator<Map.Entry<String, Double>> iter = list.iterator();

        // There has to be a better way?
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt0 = (String) phase.getKey();
            sac.t0 = (Double) phase.getValue();
        }
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt1 = (String) phase.getKey();
            sac.t1 = (Double) phase.getValue();
        }
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt2 = (String) phase.getKey();
            sac.t2 = (Double) phase.getValue();
        }
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt3 = (String) phase.getKey();
            sac.t3 = (Double) phase.getValue();
        }
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt4 = (String) phase.getKey();
            sac.t4 = (Double) phase.getValue();
        }
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt5 = (String) phase.getKey();
            sac.t5 = (Double) phase.getValue();
        }
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt6 = (String) phase.getKey();
            sac.t6 = (Double) phase.getValue();
        }
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt7 = (String) phase.getKey();
            sac.t7 = (Double) phase.getValue();
        }
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt8 = (String) phase.getKey();
            sac.t8 = (Double) phase.getValue();
        }
        if (iter.hasNext()) {
            Map.Entry phase = iter.next();
            sac.kt9 = (String) phase.getKey();
            sac.t9 = (Double) phase.getValue();
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
