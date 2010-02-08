/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.data;

import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.seed.MiniSeed;
import java.util.TreeSet;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public interface CWBDataServer {

    void query(DateTime begin, Double duration, NSCL nscl);

    TreeSet<MiniSeed> getNext();

    boolean hasNext();
    
}
