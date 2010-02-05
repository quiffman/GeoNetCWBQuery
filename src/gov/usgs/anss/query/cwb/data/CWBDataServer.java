/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query.cwb.data;

import gov.usgs.anss.seed.MiniSeed;
import java.util.TreeSet;

/**
 *
 * @author geoffc
 */
public interface CWBDataServer {

    TreeSet<MiniSeed> getNext();

    boolean hasNext();

}
