/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.data;

import gov.usgs.anss.query.cwb.formatter.CWBQueryFormatter;
import gov.usgs.anss.edge.IllegalSeednameException;
import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.seed.MiniSeed;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author geoffc
 */
public class CWBDataServerMSEED implements CWBDataServer {

    private static final Logger logger = Logger.getLogger(CWBDataServerMSEED.class.getName());
    private static DateTimeFormatter hmsFormat = ISODateTimeFormat.time().withZone(DateTimeZone.forID("UTC"));


    static {
        logger.fine("$Id: CWBServerImpl.java 1806 2010-02-03 02:59:12Z geoffc $");
    }
    private String host;
    private int port;
    private Socket ds = null;
    private InputStream inStream;
    private OutputStream outStream;
    private LinkedBlockingQueue<MiniSeed> incomingMiniSEED;
    private NSCL newNSCL = null;
    private NSCL lastNSCL = null;

    /**
     * Runs a query against a CWB server and provides methods to retrieve miniSEED data.
     *
     * @param host the CWB server name.
     * @param port the CWB server port.
     * @param begin the start time for the data query.
     * @param duration the duration in seconds to extract data for.
     * @param nscl the network, station, channel, and location data to query for.  These are all possible wild carded.
     */
    public CWBDataServerMSEED(String host, int port, DateTime begin, Double duration, NSCL nscl) {
        this.host = host;
        this.port = port;

        while (ds == null) {
            try {
                ds = new Socket(this.host, this.port);
            } catch (IOException e) {
                ds = null;
                if (e != null) {
                    if (e.getMessage() != null) {
                        if (e.getMessage().indexOf("Connection refused") >= 0) {
                            logger.warning("Got a connection refused. " + this.host + "/" + this.port + "  Is the server up?  Wait 20 and try again");
                        }
                    } else {
                        logger.warning("Got IOError opening socket to server e=" + e);
                    }
                } else {
                    logger.warning("Got IOError opening socket to server e=" + e);
                }
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException ex) {
                    logger.log(Level.FINE, "sleep interrupted.", ex);
                }
            }
        }
        try {
            inStream = ds.getInputStream();
            outStream = ds.getOutputStream();
            outStream.write(CWBQueryFormatter.miniSEED(begin, duration, nscl).getBytes());
        } catch (IOException ex) {
            Logger.getLogger(CWBDataServerMSEED.class.getName()).log(Level.SEVERE, null, ex);
        }

        incomingMiniSEED = new LinkedBlockingQueue<MiniSeed>();
    }

    /**
     * Returns the next data record.  This is equivalent to the data for a fully qualified NSCL.
     *
     * @return
     */
    public TreeSet<MiniSeed> getNext() {

        TreeSet<MiniSeed> blks = new TreeSet<MiniSeed>();

        byte[] b = new byte[4096];
        try {
            read:
            while (read(inStream, b, 0, 512)) {
                MiniSeed ms = null;
                // It doens't look like the GeoNet CWB server actually returns this.
                 if (b[0] == '<' && b[1] == 'E' && b[2] == 'O' && b[3] == 'R' && b[4] == '>') {
                    logger.fine("EOR found");
                    break read;
                } else {

                    try {
                        ms = new MiniSeed(b);
                    } catch (IllegalSeednameException ex) {
                        Logger.getLogger(CWBDataServerMSEED.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (ms.getBlockSize() != 512) {
                        read(inStream, b, 512, ms.getBlockSize() - 512);

                        try {
                            ms = new MiniSeed(b);
                        } catch (IllegalSeednameException ex) {
                            Logger.getLogger(CWBDataServerMSEED.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }

                if (ms != null) {

                    if (ms.getIndicator().compareTo("D ") < 0) {
                        continue read;
                    }

                    // This sets up the NSCL on the very first miniSEED block
                    if (lastNSCL == null) {
                        lastNSCL = NSCL.stringToNSCL(ms.getSeedName());
                    }

                    newNSCL = NSCL.stringToNSCL(ms.getSeedName());

                    if (newNSCL.equals(lastNSCL)) {
                        incomingMiniSEED.add(ms);
                        lastNSCL = newNSCL;
                    } else {
                        incomingMiniSEED.drainTo(blks);
                        incomingMiniSEED.add(ms);
                        lastNSCL = newNSCL;
                        break read;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CWBDataServerMSEED.class.getName()).log(Level.SEVERE, null, ex);
        }

        // This is triggered for the last channel off the stream.
        if (blks.isEmpty()) {
            incomingMiniSEED.drainTo(blks);
        }

        return blks;
    }

    /**
     * Returns true if there are more data records.
     *
     * @return
     */
    public boolean hasNext() {
        if (lastNSCL == null) {
            return true;
        }
        return !incomingMiniSEED.isEmpty();
    }


    public static boolean read(InputStream in, byte[] b, int off, int l)
            throws IOException {
        int len;
        while ((len = in.read(b, off, l)) > 0) {
            off += len;
            l -=
                    len;
            if (l == 0) {
                return true;
            }

        }
        return false;
    }

    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            outStream.write(("\n").getBytes());
        } catch (IOException ex) {
            Logger.getLogger(CWBDataServerMSEED.class.getName()).log(Level.SEVERE, null, ex);
        }
        ds.close();
    }
}
