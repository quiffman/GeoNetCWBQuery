/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.formatter;

import gov.usgs.anss.query.NSCL;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Provides methods that return strings that are suitably formatted for querying
 * a CWB server.
 *
 * @author geoffc
 */
public class CWBQueryFormatter {

    private static String beginFormat = "YYYY/MM/dd HH:mm:ss";
    private static DateTimeFormatter parseBeginFormat = DateTimeFormat.forPattern(beginFormat).withZone(DateTimeZone.forID("UTC"));

    /**
     * String for listing available channels.
     *
     * @param begin
     * @param duration
     * @return
     */
    public static String listChannels(DateTime begin, Double duration) {
        return "'-b' '" + parseBeginFormat.withZone(DateTimeZone.UTC).print(begin) + "' " + "'-d' '" + duration + "' " + "'-lsc'\n";
    }

    public static String miniSEED(DateTime begin, Double duration, NSCL nscl) {
//        '-b' '2009/01/01 00:00:00' '-s' 'NZMRZ..HHZ10' '-d' '1800'\n
        return "'-b' '" + parseBeginFormat.withZone(DateTimeZone.UTC).print(begin) + "' '-s' '" +  nscl.toString() + "' '-d' '" + duration + "'\n";
    }

        public static String miniSEED(DateTime begin, Double duration, String nsclSelectString) {
//        '-b' '2009/01/01 00:00:00' '-s' 'NZMRZ..HHZ10' '-d' '1800'\n
        return "'-b' '" + parseBeginFormat.withZone(DateTimeZone.UTC).print(begin) + "' '-s' '" +  nsclSelectString + "' '-d' '" + duration + "'\n";
    }
}
