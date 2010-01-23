/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query.cwb;

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

}
