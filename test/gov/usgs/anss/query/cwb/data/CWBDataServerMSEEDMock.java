/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.data;

import gov.usgs.anss.edge.IllegalSeednameException;
import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.seed.MiniSeed;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class CWBDataServerMSEEDMock implements CWBDataServer {

    private ArrayList<ArrayList<MiniSeed>> expResult;
    private Iterator iter;

    CWBDataServerMSEEDMock(String host, int port) {
    }

    private void loadMSEEDFiles(String[] filenames) throws FileNotFoundException, IOException {
        expResult = new ArrayList<ArrayList<MiniSeed>>();
        for (String filename : filenames) {
            File ms = new File(filename);
            long fileSize = ms.length();
            ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>((int) (fileSize / 512));

            byte[] buf = new byte[512];
            FileInputStream in = new FileInputStream(ms);
            for (long pos = 0; pos < fileSize; pos += 512) {
                if (in.read(buf) == -1) {
                    break;
                }
                try {
                    blks.add(new MiniSeed(buf));
                } catch (IllegalSeednameException ex) {
                    Logger.getLogger(CWBDataServerMSEEDMock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            expResult.add(blks);
        }
        iter = expResult.iterator();
    }

    public void query(DateTime begin, Double duration, NSCL nscl) {
    }

    public TreeSet<MiniSeed> getNext() {
        return new TreeSet((ArrayList<MiniSeed>)iter.next());
    }

    public boolean hasNext() {
        return iter.hasNext();
    }

    public void quiet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
