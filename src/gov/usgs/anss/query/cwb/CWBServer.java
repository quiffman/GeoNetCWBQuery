/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query.cwb;

import gov.usgs.anss.query.EdgeQueryOptions;
import gov.usgs.anss.seed.MiniSeed;
import java.util.ArrayList;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public interface CWBServer {

    /**
     *
     * @param begin
     * @param duration
     * @return
     */
    String listChannels(DateTime begin, Double duration);

    public ArrayList<ArrayList<MiniSeed>> query(EdgeQueryOptions options);

}
