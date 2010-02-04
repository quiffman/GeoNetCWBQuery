/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb;

import gov.usgs.anss.edge.IllegalSeednameException;
import gov.usgs.anss.query.EdgeQueryOptions;
import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.seed.MiniSeed;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingDeque;
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
public class CWBServerMSEED {

    static DecimalFormat df6 = new DecimalFormat("000000");
    private static final Logger logger = Logger.getLogger(CWBServerMSEED.class.getName());
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
    private boolean eof;
    private NSCL newNSCL = null;
    private NSCL lastNSCL = null;

    public CWBServerMSEED(String host, int port, DateTime begin, Double duration, NSCL nscl) {
        this.host = host;
        this.port = port;

        // particularly for the DCC we want this program to not error out if we cannot connect to the server
        // So make sure we can connect and print messages

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
            Logger.getLogger(CWBServerMSEED.class.getName()).log(Level.SEVERE, null, ex);
        }

        incomingMiniSEED = new LinkedBlockingQueue<MiniSeed>();
    }

    public ArrayList<MiniSeed> query(EdgeQueryOptions options) {

        //  This could be a TreeSet but we need to test the
        // comparator interface in MiniSeed
        ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>(100);

        byte[] b = new byte[4096];
        try {
            read:
            while (read(inStream, b, 0, options.gapsonly ? 64 : 512)) {
                MiniSeed ms = null;
                // It doens't look like the GeoNet CWB server actually does this
                // but I'm going to leave this in anyway.
                if (b[0] == '<' && b[1] == 'E' && b[2] == 'O' && b[3] == 'R' && b[4] == '>') {
                    eof = true;
                    logger.fine("EOR found");
                } else {

                    try {
                        ms = new MiniSeed(b);
                    } catch (IllegalSeednameException ex) {
                        Logger.getLogger(CWBServerMSEED.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // Need Rich to tell me if we still need this.
                    // It looks like it clobbers ms of this ever occurs.
                    if (!options.gapsonly && ms.getBlockSize() != 512) {
                        read(inStream, b, 512, ms.getBlockSize() - 512);

                        try {
                            ms = new MiniSeed(b);
                        } catch (IllegalSeednameException ex) {
                            Logger.getLogger(CWBServerMSEED.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }

                if (ms != null) {

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
            Logger.getLogger(CWBServerMSEED.class.getName()).log(Level.SEVERE, null, ex);
        }

        // This is triggered from the last channel off the stream
        // There is probably a way to handle this in the logic above.
        if (blks.isEmpty()) {
            incomingMiniSEED.drainTo(blks);
        }

        Collections.sort(blks);

        return blks;
    }

	public boolean hasNext() {
		if (lastNSCL == null) {
			return true;
		}
		return !incomingMiniSEED.isEmpty();
	}


//      public ArrayList<MiniSeed> query(EdgeQueryOptions options) {
//
//      boolean endOfRecord = false;
//      blks = new ArrayList<MiniSeed>(100);
//
//        byte[] b = new byte[4096];
//
//        try {
//
//            // The length at which our compare for changes depends on the output file mask
//            Comparator nsclComparator = options.getNsclComparator();
//
//            long maxTime = 0;
//            int ndups = 0;
//            try {
//                int iblk = 0;
//                NSCL nsclComp = NSCL.stringToNSCL("            ");
//                boolean eof = false;
//                MiniSeed ms = null;
//                int npur = 0;
//
//                if (blks.size() > 0) {
//                    blks.clear();
//                    System.gc();        // Lots of memory just abandoned.  Try garbage collector
//                }
//
//
//                while (!eof) {
//                    try {
//                        // Try to read a mini-seed, if it failes mark eof
//                        if (read(in, b, 0, (options.gapsonly ? 64 : 512))) {
//
//                            if (b[0] == '<' && b[1] == 'E' && b[2] == 'O' && b[3] == 'R' && b[4] == '>') {
//                                eof = true;
//                                ms = null;
//
//                                logger.fine("EOR found");
//
//                            } else {
//                                ms = new MiniSeed(b);
//                                logger.finest("" + ms);
//                                if (!options.gapsonly && ms.getBlockSize() != 512) {
//                                    read(in, b, 512, ms.getBlockSize() - 512);
//                                    ms = new MiniSeed(b);
//                                }
//                                iblk++;
//                            }
//                        } else {
//                            eof = true;         // still need to process this last channel THIS SHOULD NEVER  HAPPEN unless socket is lost
//                            ms = null;
//                            logger.warning("   *** Unexpected EOF Found");
//                        }
//                        logger.finest(iblk + " " + ms);
//                        if (!options.quiet && iblk % 1000 == 0 && iblk > 0) {
//                            // This is a user-feedback counter.
//                            System.out.print("\r            \r" + iblk + "...");
//                        }
//
//                        if (eof || (nsclComp != null &&
//                                (ms == null ? true : nsclComparator.compare(nsclComp, NSCL.stringToNSCL(ms.getSeedName())) != 0)))
//                        {
//                            if (!options.quiet) {
//                                // TODO could go into a helper method
//                                int nsgot = 0;
//                                if (blks.size() > 0) {
//                                    Collections.sort(blks);
//                                    logger.finer(blks.size() + " " + iblk);
//                                    for (int i = 0; i < blks.size(); i++) {
//                                        nsgot += (blks.get(i)).getNsamp();
//                                    }
//                                    logger.finest("" + (MiniSeed) blks.get(blks.size() - 1));
//                                    System.out.print('\r');
//                                    DateTime dt = new DateTime().withZone(DateTimeZone.forID("UTC"));
//
//
//                                    logger.info(hmsFormat.print(dt.getMillis()) + " Query on " + nsclComp + " " +
//                                            df6.format(blks.size()) + " mini-seed blks " +
//                                            (blks.get(0) == null ? "Null" : ((MiniSeed) blks.get(0)).getTimeString()) + " " +
//                                            (blks.get((blks.size() - 1)) == null ? "Null" : (blks.get(blks.size() - 1)).getEndTimeString()) + " " +
//                                            " ns=" + nsgot);
//                                } else {
//                                    System.out.print('\r');
//                                    logger.info("Query on " + options.getSeedname() + " returned 0 blocks!");
//                                }
//
//
//                            }
//
//                            endOfRecord = true;
//                            maxTime = 0;
//
//                        }
//
//
//
//                        boolean isDuplicate = false;
//                        if (ms != null) {
//                            if (ms.getTimeInMillis() <= maxTime) {    // No need to check duplicates if this is newest seen
//                                if (!options.gapsonly) {
//                                    if (blks.size() >= 1) {
//                                        for (int i = blks.size() - 1; i >= 0; i--) {
//                                            if (ms.isDuplicate(blks.get(i))) {
//                                                isDuplicate = true;
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                if (!isDuplicate && ms.getIndicator().compareTo("D ") >= 0) {
//                                    blks.add(ms);
//                                } else {
//                                    ndups++;
//                                }
//                            } else {
//                                if (ms.getIndicator().compareTo("D ") >= 0) {
//                                    blks.add(ms); // If its not D or better, its been zapped!
//                                }
//                                maxTime = ms.getTimeInMillis();
//                            }
//                            nsclComp = NSCL.stringToNSCL(ms.getSeedName());
//                        }
//                    } catch (IllegalSeednameException e) {
//                        logger.severe("Seedname exception making a seed record e=" + e.getMessage());
//                    }
//                }   // while(!eof)
//                if (!options.quiet && iblk > 0) {
//                    logger.info(iblk + " blocks transferred.");
//                }
//                // TODO convert this to return blks, not blksAll and have a hasNext method to
//                // allow multiple calls to this method.
//                blks.trimToSize();
//
//                return blks;
//            } catch (UnknownHostException e) {
//                logger.severe("EQC main: Host is unknown=" + this.host + "/" + this.port);
//                return null;
//            } catch (IOException e) {
//                if (e.getMessage().equalsIgnoreCase("Connection refused")) {
//                    logger.severe("The connection was refused.  Server is likely down or is blocked. This should never happen.");
//                    return null;
//                } else {
//                    logger.severe(e + " EQC main: IO error opening/reading socket=" + this.host + "/" + this.port);
//                }
//            }
//            outtcp.write("\n".getBytes());      // Send end of request marker to query
//
//            // TODO - is this necessary?
//            // push ds to member variable
////            if (ds.isClosed()) {
////                try {
////                    ds.close();
////                } catch (IOException e) {
////                }
////            }
//
//            return null;
//        } catch (IOException e) {
//            logger.severe(e + " IOError reading input lines.");
//        }
//        return null;
//    }
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
}
