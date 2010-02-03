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
public class CWBServerImpl implements CWBServer {

    static DecimalFormat df6 = new DecimalFormat("000000");
    private static final Logger logger = Logger.getLogger(CWBServerImpl.class.getName());
    private static DateTimeFormatter hmsFormat = ISODateTimeFormat.time().withZone(DateTimeZone.forID("UTC"));


    static {
        logger.fine("$Id$");
    }
    private String host;
    private int port;

    public CWBServerImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String listChannels(DateTime begin, Double duration) {
        try {

            byte[] b = new byte[4096];
            Socket ds = new Socket(this.host, this.port);
            ds.setReceiveBufferSize(512000);

            InputStream in = ds.getInputStream();
            OutputStream outtcp = ds.getOutputStream();

// This option is not documented in the help so won't implement it for now.
//            if (options.exclude != null) {
//                line = "'-el' '" + options.exclude + "' ";
//            } else {
//                line = "";
//            }
// This option is ont documented in the help so won't implement it for now.
//                if (options.showIllegals) {
//                    line += "'-si' ";
//                }
// This option is stated as not useful for users so not going to implement it.
//            } else {
//                line += "'-ls'\n";
//            }

            String line = CWBQueryFormatter.listChannels(begin, duration);

            logger.config("line=" + line + ":");
            outtcp.write(line.getBytes());
            StringBuffer sb = new StringBuffer(100000);
            int len = 0;
            while ((len = in.read(b, 0, 512)) > 0) {
                sb.append(new String(b, 0, len));
            }
            // TODO - multiple returns, bad form.
            return sb.toString();
        } catch (IOException e) {
            logger.severe(e + " list channels");
            return null;
        }
    }

    public ArrayList<ArrayList<MiniSeed>> query(EdgeQueryOptions options, DateTime begin, Double duration, NSCL nscl) {

        byte[] b = new byte[4096];
        //       Outputer out = null;

        ArrayList<ArrayList<MiniSeed>> blksAll = null;

        // the "in" BufferedReader will give us the command lines we need for the other end
        try {
            // for each line of input, read it, reformat it with single quotes, send to server
            int nline = 0;
            int totblks = 0;
            // particularly for the DCC we want this program to not error out if we cannot connect to the server
            // So make sure we can connect and print messages
            Socket ds = null;
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
                        // This isn't necessarily a major issue, and for the purposes
                        // of sleep, we really don't care.
                        logger.log(Level.FINE, "sleep interrupted.", ex);
                    }
                }
            }
            InputStream in = ds.getInputStream();        // Get input and output streams
            OutputStream outtcp = ds.getOutputStream();

                blksAll = new ArrayList<ArrayList<MiniSeed>>(20);

                // The length at which our compare for changes depends on the output file mask
                Comparator nsclComparator = options.getNsclComparator();

                long maxTime = 0;
                int ndups = 0;
                try {
                     outtcp.write(CWBQueryFormatter.miniSEED(begin, duration, nscl).getBytes());
                    int iblk = 0;
                    NSCL nsclComp = NSCL.stringToNSCL("            ");
                    boolean eof = false;
                    MiniSeed ms = null;
                    int npur = 0;
                    ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>(100);

                    while (!eof) {
                        try {
                            // Try to read a mini-seed, if it failes mark eof
                            if (read(in, b, 0, (options.gapsonly ? 64 : 512))) {

                                if (b[0] == '<' && b[1] == 'E' && b[2] == 'O' && b[3] == 'R' && b[4] == '>') {
                                    eof = true;
                                    ms = null;

                                    logger.fine("EOR found");

                                } else {
                                    ms = new MiniSeed(b);
                                    logger.finest("" + ms);
                                    if (!options.gapsonly && ms.getBlockSize() != 512) {
                                        read(in, b, 512, ms.getBlockSize() - 512);
                                        ms = new MiniSeed(b);
                                    }
                                    iblk++;
                                    totblks++;
                                }
                            } else {
                                eof = true;         // still need to process this last channel THIS SHOULD NEVER  HAPPEN unless socket is lost
                                ms = null;
                                logger.warning("   *** Unexpected EOF Found");
                            }
                            logger.finest(iblk + " " + ms);
                            if (!options.quiet && iblk % 1000 == 0 && iblk > 0) {
                                // This is a user-feedback counter.
                                System.out.print("\r            \r" + iblk + "...");
                            }

                            if (eof || (nsclComp != null &&
                                    (ms == null ? true : nsclComparator.compare(nsclComp, NSCL.stringToNSCL(ms.getSeedName())) != 0))) {
                                if (!options.quiet) {
                                    // TODO could go into a helper method
                                    int nsgot = 0;
                                    if (blks.size() > 0) {
                                        Collections.sort(blks);
                                        logger.finer(blks.size() + " " + iblk);
                                        for (int i = 0; i < blks.size(); i++) {
                                            nsgot += (blks.get(i)).getNsamp();
                                        }
                                        logger.finest("" + (MiniSeed) blks.get(blks.size() - 1));
                                        System.out.print('\r');
                                        DateTime dt = new DateTime().withZone(DateTimeZone.forID("UTC"));


                                        logger.info(hmsFormat.print(dt.getMillis()) + " Query on " + nsclComp + " " +
                                                df6.format(blks.size()) + " mini-seed blks " +
                                                (blks.get(0) == null ? "Null" : ((MiniSeed) blks.get(0)).getTimeString()) + " " +
                                                (blks.get((blks.size() - 1)) == null ? "Null" : (blks.get(blks.size() - 1)).getEndTimeString()) + " " +
                                                " ns=" + nsgot);
                                    } else {
                                        System.out.print('\r');
                                        logger.info("Query on " + options.getSeedname() + " returned 0 blocks!");
                                    }


                                }

                                if (blks.size() > 0) {
                                    MiniSeed ms2 = blks.get(0);
                                    ArrayList<MiniSeed> newBlks = new ArrayList<MiniSeed>(blks.size());
                                    for (int i = 0; i < blks.size(); i++) {
                                        newBlks.add(i, blks.get(i));
                                    }
                                    blksAll.add(newBlks);
                                }
                                maxTime = 0;
                                if (blks.size() > 0) {
                                    blks.clear();
                                    System.gc();        // Lots of memory just abandoned.  Try garbage collector
                                }
                            }

                            // If this block is the first in a new component, clear the blks array
                            //if(!lastComp.substring(0,compareLength).equals(
                            //    ms.getSeedName().substring(0,compareLength))) blks.clear();
                            /* in late 2007 there was some files which were massively duplicated by block.
                             * to prevent this from blowing memory when there are so may we eliminate and duplicate
                             * blocks here.  If it is massively out of order , all of these block checks will slow things
                             * down.
                             **/


                            boolean isDuplicate = false;
                            if (ms != null) {
                                if (ms.getTimeInMillis() <= maxTime) {    // No need to check duplicates if this is newest seen
                                    if (!options.gapsonly) {
                                        if (blks.size() >= 1) {
                                            for (int i = blks.size() - 1; i >= 0; i--) {
                                                if (ms.isDuplicate(blks.get(i))) {
                                                    isDuplicate = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (!isDuplicate && ms.getIndicator().compareTo("D ") >= 0) {
                                        blks.add(ms);
                                    } else {
                                        ndups++;
                                    }
                                } else {
                                    if (ms.getIndicator().compareTo("D ") >= 0) {
                                        blks.add(ms); // If its not D or better, its been zapped!
                                    }
                                    maxTime = ms.getTimeInMillis();
                                }
                                nsclComp = NSCL.stringToNSCL(ms.getSeedName());
                            }
                        } catch (IllegalSeednameException e) {
                            logger.severe("Seedname exception making a seed record e=" + e.getMessage());
                        }
                    }   // while(!eof)
                    if (!options.quiet && iblk > 0) {
                        logger.info(iblk + " blocks transferred.");
                    }
                    blks.clear();
                    return blksAll;      // If called in no file output mode, return the blocks
                } catch (UnknownHostException e) {
                    logger.severe("EQC main: Host is unknown=" + this.host + "/" + this.port);
                    return null;
                } catch (IOException e) {
                    if (e.getMessage().equalsIgnoreCase("Connection refused")) {
                        logger.severe("The connection was refused.  Server is likely down or is blocked. This should never happen.");
                        return null;
                    } else {
                        logger.severe(e + " EQC main: IO error opening/reading socket=" + this.host + "/" + this.port);
                    }
                }
            outtcp.write("\n".getBytes());      // Send end of request marker to query
            if (ds.isClosed()) {
                try {
                    ds.close();
                } catch (IOException e) {
                }
            }
            return null;
        } catch (IOException e) {
            logger.severe(e + " IOError reading input lines.");
        }
        return null;
    }

    public static boolean read(InputStream in, byte[] b, int off, int l)
            throws IOException {
        int len;
        while ((len = in.read(b, off, l)) > 0) {
            off += len;
            l -= len;
            if (l == 0) {
                return true;
            }
        }
        return false;
    }
}
