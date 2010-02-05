/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query.cwb.messages;

import gov.usgs.anss.seed.MiniSeed;
import java.util.Collection;
import java.util.Iterator;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class MessageFormatter {

    public static String miniSeedSummary(DateTime now, Collection<MiniSeed> miniSeed ) {

        // 02:07:45.992Z Query on NZAPZ  HHZ10 000431 mini-seed blks 2009 001:00:00:00.0083 2009 001:00:30:00.438  ns=180044

        int numBlks = miniSeed.size();

        Iterator<MiniSeed> iter = miniSeed.iterator();

        MiniSeed ms = iter.next();

        String seedName = ms.getSeedName();
        int numSamples = ms.getNsamp();

        while (iter.hasNext()) {
            ms = iter.next();
            numSamples += ms.getNsamp();
        }

        String summary = null +
                "Query on " +
                seedName + " " + " " + numBlks + " " + numSamples
                ;




        return summary;
    }

}
