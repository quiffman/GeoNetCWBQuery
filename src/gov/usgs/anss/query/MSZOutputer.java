/*
 * MSZOutputer.java
 *
 * Created on April 20, 2006, 4:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.seed.MiniSeed;
import java.util.GregorianCalendar;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import gov.usgs.anss.edge.*;
import gov.usgs.anss.util.*;

/**
 *
 * @author davidketchum
 */
public class MSZOutputer extends Outputer {

    boolean dbg;
    int blocksize;
    DecimalFormat df3;
	static {logger.finest("$Id$");}

    /** Creates a new instance of SacOutputer */
    public MSZOutputer(int blkSize) {
        blocksize = blkSize;
    }

    public void makeFile(String lastComp, String filename, String filemask, ArrayList<MiniSeed> blks,
            java.util.Date beg, double duration, String[] args) throws IOException {

        // Process the args for things that affect us
        boolean gaps = false;       // if true, generate a list of any gaps in the data
        boolean doHoldings = false;
        boolean msgaps = false;
        boolean dbg = false;
        int fill = -12345;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fill")) {
                fill = Integer.parseInt(args[i + 1]);
            }
            if (args[i].equals("-gaps")) {
                fill = 2147000000;
                gaps = true;
            }
            if (args[i].equals("-updhold")) {
                doHoldings = true;
            }
            if (args[i].equals("-dbgz")) {
                dbg = true;
            }
            if (args[i].equals("-msgaps")) {
                msgaps = true;
            }
        }

        GregorianCalendar start = new GregorianCalendar();
        start.setTimeInMillis(beg.getTime());


        //ZeroFilledSpan span = new ZeroFilledSpan(blks);
        int threshold = 1000;
        ArrayList<Run> runs = new ArrayList<Run>(10);
        if (gaps) {
            HoldingSender hs = null;
            // process the gaps
            Collections.sort(blks);
            double rate = 0.;
            int gapThreshold = 25;
            // create some number of runs to put together into a  long one.
            for (int i = 0; i < blks.size(); i++) {
                MiniSeed ms2 = (MiniSeed) blks.get(i);
                if (doHoldings) {
                    if (hs == null) {
                        try {
                            //System.out.println("Open HoldingSender "+"-h 136.177.24.92 -p 7996 -t CW -q 10000 -tcp -quiet -noeto");
                            hs = new HoldingSender("-h 136.177.24.92 -p 7996 -t CW -q 10000 -tcp -quiet -noeto", "");
                        } catch (UnknownHostException e) {
                            System.out.println("Unknown host exception host=136.177.24.92");
                            doHoldings = false;
                        }
                    }
                    //if(hs != null) hs.send(ms2);    // send holdings by run now below
                }
                boolean found = false;
                if (ms2.getRate() > rate) {
                    rate = ms2.getRate();
                    gapThreshold = (int) (1000. / rate + 0.5);
                }
                for (int j = 0; j < runs.size(); j++) {
                    if (runs.get(j).add(ms2)) {
                        found = true;
                        break;
                    }
                }
                if (ms2.getNBlockettes() == 2) {
                    if (ms2.getBlocketteType(0) == 1000 && ms2.getBlocketteType(1) == 1001) {
                        rate = ms2.getRate();
                        gapThreshold = (int) (1000. / rate + 0.5);
                    }
                }
                if (!found) {
                    runs.add(new Run(ms2));
                }
            }
            Collections.sort(runs);
            if (dbg) {
                for (int i = 0; i < runs.size(); i++) {
                    System.out.println(i + " " + runs.get(i));
                }
            }
            long expected = runs.get(0).getMS(0).getTimeInMillis();
            long today = expected;
            if (expected % 86400000L > gapThreshold) {
                System.out.println("Start Day Gap : (" + ((expected % 86400000L) / 1000.) + ")    " + lastComp);
            }
            for (int i = 0; i < runs.size(); i++) {
                if (hs != null) {
                    hs.send(runs.get(i).getMS(0).getSeedName(),
                            runs.get(i).getStart(), // start time of span
                            (double) (runs.get(i).getEnd().getTimeInMillis() - runs.get(i).getStart().getTimeInMillis()) / 1000.);
                }
                // Is the end of this run before the expected - if so skip run
                if (runs.get(i).getMS(runs.get(i).getNBlocks() - 1).getNextExpectedTimeInMillis() < expected) {
                    if (dbg) {
                        System.out.println(i + " not needed. before expected");
                    }
                    continue;
                }
                // does this run span the expected, if yes last block is new expected
                if (runs.get(i).getMS(0).getTimeInMillis() <= expected &&
                        runs.get(i).getMS(runs.get(i).getNBlocks() - 1).getNextExpectedTimeInMillis() > expected) {
                    expected = runs.get(i).getMS(runs.get(i).getNBlocks() - 1).getNextExpectedTimeInMillis();
                    if (dbg) {
                        System.out.println(i + " spanning run new expect=" + runs.get(i).getMS(runs.get(i).getNBlocks() - 1).getEndTimeString());
                    }
                    continue;
                }
                if (runs.get(i).getMS(0).getTimeInMillis() - expected > gapThreshold) {
                    GregorianCalendar msStart = new GregorianCalendar();
                    msStart.setTimeInMillis(expected - gapThreshold + 1);
                    GregorianCalendar msEnd = new GregorianCalendar();
                    msEnd.setTimeInMillis(runs.get(i).getMS(0).getTimeInMillis() - 1);  // start time of run end end time of gap

                    String[] msEndStr = msStart.getTime().toString().split(" ");
                    String[] nextStartStr = msEnd.getTime().toString().split(" ");
                    if (df3 == null) {
                        df3 = new DecimalFormat("0.000");
                    }
                    System.out.println("Gap: " + msEndStr[1] + " " + msEndStr[2] + " " + msEndStr[3] + " " + msEndStr[5] + " " +
                            Integer.toString(msStart.get(Calendar.MILLISECOND)) + " to " +
                            nextStartStr[1] + " " + nextStartStr[2] + " " + nextStartStr[3] + " " + nextStartStr[5] + " " +
                            Integer.toString(msEnd.get(Calendar.MILLISECOND)) +
                            " (" + df3.format((msEnd.getTimeInMillis() - msStart.getTimeInMillis()) / 1000.) + " secs)   " + lastComp);

                }
                expected = runs.get(i).getMS(runs.get(i).getNBlocks() - 1).getNextExpectedTimeInMillis();
            }
            long gp = ((today / 86400000L + 1) * 86400000L) - expected;
            if (gp > gapThreshold) {
                System.out.println("End Day Gap : (" + (gp / 1000.) + ")     " + lastComp);
            }
            System.out.println("expected=" + expected + " bound=" + ((today / 86400000L + 1) * 86400000L) + " gp=" + gp);

            if (hs != null && doHoldings) {
                hs.close();
            }
            return;  // we only want to process the gaps
        }

        // build the zero filled area (either with exact limits or with all blocks)
        ZeroFilledSpan span = new ZeroFilledSpan(blks, start, duration, fill);
        if (dbg) {
            System.out.println("ZeroSpan=" + span.toString());
        }
        if (filemask.equals("%N")) {
            filename += ".ms";
        }
        filename = filename.replaceAll("[__]", "_");
        // Make an output file and link it to the RawToMiniSeed createor
        MiniSeedOutputFile outms = new MiniSeedOutputFile(filename);
        MiniSeed ms = (MiniSeed) blks.get(0);
        GregorianCalendar st = span.getStart();
        RawToMiniSeed rwms = new RawToMiniSeed(lastComp, ms.getRate(),
                blocksize / 64 - 1,
                ms.getYear(), ms.getDay(),
                (int) ((st.getTimeInMillis() % 86400000L) / 1000L),
                (int) ((st.getTimeInMillis() % 1000L) * 1000L),
                700000, null);
        rwms.setOutputHandler(outms);
        int len = 12000;
        int n;
        double secadd;
        int[] d = new int[len];
        int[] d2 = new int[len];
        int year = span.getStart().get(Calendar.YEAR);
        int doy = span.getStart().get(Calendar.DAY_OF_YEAR);
        int sec = (int) ((span.getStart().getTimeInMillis() % 86400000L) / 1000);
        int micros = (int) ((span.getStart().getTimeInMillis() % 1000L) * 1000);
        int jul = SeedUtil.toJulian(year, doy);
        boolean forceout = false;
        for (int off = 0; off < span.getNsamp(); off = off + len) {
            n = span.getData(d, off, len);
            //System.out.println("comp: n="+n+" "+year+" "+doy+" "+RawToMiniSeed.timeFromUSec(sec*1000000L+micros));

            int offstart = -1;
            int end = 0;
            // create miniseed from the data buffer.  Look for fill and compress the data, call forceout at gaps.
            if (msgaps) {
                int bufoff = 0;
                while (bufoff < n) {
                    offstart = -1;
                    for (int i = bufoff; i < n; i++) {
                        if (d[i] != fill) {
                            offstart = i;
                            break;
                        }  // found first non-fill, find next
                    }
                    if (offstart == -1) {
                        break;      // No non fill the rest of the way
                    }
                    end = -1;
                    for (int i = offstart; i < n; i++) {
                        if (d[i] == fill) {
                            end = i;
                            forceout = true;
                            break;
                        }
                    }
                    if (end == -1) {
                        end = n;
                    }
                    System.arraycopy(d, offstart, d2, 0, end - offstart);
                    secadd = offstart / ms.getRate();
                    int secoff = (int) secadd;
                    int microoff = (int) ((secadd - secoff) * 1000000.);
                    rwms.process(d2, end - offstart, year, doy, sec + secoff, micros + microoff, 0, 0, 0, 0, 0);
                    if (forceout) {
                        rwms.forceOut();
                        forceout = false;
                    }
                    bufoff = end;
                }
            } else {
                rwms.process(d, n, year, doy, sec, micros, 0, 0, 0, 0, 0);
            }

            // add to the time and submit somemore data
            secadd = n / ms.getRate();
            sec = sec + (int) secadd;
            micros = micros + (int) ((sec - Math.floor(sec)) * 1000000. + 0.0001);
            while (micros >= 1000000) {
                micros -= 1000000;
                sec++;
            }
            while (sec >= 86400) {
                sec -= 86400;
                jul++;
                int[] ymd = SeedUtil.fromJulian(jul);
                year = ymd[0];
                doy = SeedUtil.doy_from_ymd(ymd);
            }
        }
        rwms.forceOut();
        outms.close();
    }

    /** This class creates a list of contiguous blocks.  A block can be added to it
     *and will be rejected if it is not contiguouse at the end.  The user just attempts
     *to add the next data block in time to each of the known runs, and creates a new run
     *with the block when none of the existing ones accepts it
     */
    class Run implements Comparable<Run> {

        ArrayList<MiniSeed> blks;     // List of sequenctial contiuous Mini-seed blocks
        GregorianCalendar start;      // start time of this run
        GregorianCalendar end;        // current ending time of this run (expected time of next block)

        /** return the start time of the run
         *@return the start time as GregorianCalendar*/
        public GregorianCalendar getStart() {
            return start;
        }

        /** return the end time of the run (Actually the time of the next expected sample)
         *@return the end time as GregorianCalendar*/
        public GregorianCalendar getEnd() {
            return end;
        }

        /** return duration of run in seconds
         *@return The duration of run in seconds*/
        public double getLength() {
            return (end.getTimeInMillis() - start.getTimeInMillis()) / 1000.;
        }

        /** string representation
         *@return a String representation of this run */
        public String toString() {
            return "Run from " + Util.ascdate(start) + " " + Util.asctime2(start) + " to " +
                    Util.ascdate(end) + " " + Util.asctime2(end) + " " + getLength() + " s #blks=" + blks.size();
        }

        /** return the ith miniseed block
         *@param Index of desired Mini-seed block
         *@return the Miniseed block */
        public MiniSeed getMS(int i) {
            return blks.get(i);
        }

        /** retun length of miniseed list for this run
         *@return The length of the miniseed list for this run */
        public int getNBlocks() {
            return blks.size();
        }

        /** clear the list (used mainly to free up associated memory)*/
        public void clear() {
            blks.clear();
            start = null;
            end = null;
        }

        /** implement Comparable
         *@param the Run to compare this to
         *@return -1 if <, 0 if =, 1 if >than */
        public int compareTo(Run r) {
            return start.compareTo(((Run) r).getStart());
        }

        /** create a new run with the given miniseed as initial block
         *@param ms The miniseed block to first include */
        public Run(MiniSeed ms) {
            start = ms.getGregorianCalendar();
            blks = new ArrayList<MiniSeed>(1000);
            blks.add(ms);
            end = ms.getGregorianCalendar();
            end.setTimeInMillis(end.getTimeInMillis() + ((long) (ms.getNsamp() / ms.getRate() * 1000 + 0.49)));

        }

        /** see if this miniseed block will add contiguously to the end of this run
         *@param the miniseed block to consider for contiguousnexx, add it if is is
         *@return true, if block was contiguous and was added to this run, false otherwise*/
        public boolean add(MiniSeed ms) {

            // Is the beginning of this one near the end of the last one!
            if (Math.abs(ms.getGregorianCalendar().getTimeInMillis() - end.getTimeInMillis()) <
                    500. / ms.getRate()) {
                // add this block to the list
                blks.add(ms);
                end = ms.getGregorianCalendar();
                end.setTimeInMillis(end.getTimeInMillis() +
                        ((long) (ms.getNsamp() / ms.getRate() * 1000 + 0.49)));
                return true;
            } else {
                return false;
            }
        }
    }
}
