/*
 * SacOutputer.java
 *
 * Created on April 20, 2006, 4:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.query.metadata.ChannelMetaData;
import gov.usgs.anss.query.metadata.MetaDataServer;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import gov.usgs.anss.util.PNZ;
import gov.usgs.anss.seed.*;
import edu.sc.seis.TauP.SacTimeSeries;

/**
 *
 * @author davidketchum
 */
public class SacOutputer extends Outputer {

    boolean dbg;
    private static MetaDataServer metaDataServer;


    static {
        logger.fine("$Id$");
    }

    /** Creates a new instance of SacOutputer */
    public SacOutputer(EdgeQueryOptions options) {
        this.options = options;
    }

    public void makeFile(SeedName nscl, String filename,
            ArrayList<MiniSeed> blks) throws IOException {

        // TODO - these could be in an argument object?
//        String network = lastComp.substring(0, 2);
//        String code = lastComp.substring(2, 7);
//        String component = lastComp.substring(7, 10);
//        String location = lastComp.substring(10, 12);

              // Process the args for things that affect us
        if (blks.size() == 0) {
            return;    // no data to save
        }
        boolean nogaps = false;       // if true, do not generate a file if it has any gaps!
        int fill = -12345;
        boolean noMeta = false;
        boolean quiet = false;
        boolean sactrim = false;      // return full length padded with no data value
        String pzunit = "nm";

        for (int i = 0; i < options.extraArgs.size(); i++) {
            if (options.extraArgs.get(i).equals("-fill")) {
                fill = Integer.parseInt(options.extraArgs.get(i + 1));
            }
            if (options.extraArgs.get(i).equals("-nogaps")) {
                fill = 2147000000;
                nogaps = true;
            }
            if (options.extraArgs.get(i).equals("-nometa")) {
                noMeta = true;
            }
            if (options.extraArgs.get(i).equals("-sactrim")) {
                sactrim = true;
            }
        }

        // Use the span to populate a sac file
        GregorianCalendar start = new GregorianCalendar();
        start.setTimeInMillis(options.getBeginWithOffset().getMillis());

        // build the zero filled area (either with exact limits or with all blocks)
        ZeroFilledSpan span = new ZeroFilledSpan(blks, start, options.getDuration(), fill);
        if (span.getRate() <= 0.00) {
            return;         // There is no real data to put in SAC
        }

        logger.fine("ZeroSpan=" + span.toString());

        int noval = span.getNMissingData();

        if (nogaps && span.hasGapsBeforeEnd()) {
            logger.warning("  ** " + nscl.toString() + " has gaps - discarded # missing =" + noval);
            return;
        }
        if (options.filemask.equals("%N")) {
            filename += ".sac";
        }
        filename = filename.replaceAll("[__]", "_");

        //ZeroFilledSpan span = new ZeroFilledSpan(blks);

        SacTimeSeries sac = new SacTimeSeries();
        sac.npts = span.getNsamp();
        PNZ pnz = null;

        // Get station meta data and repsonse info (if requested)
        // Give a default if there is noMeta
        ChannelMetaData md = new ChannelMetaData(nscl);
        if (!noMeta) {

            metaDataServer = new MetaDataServer(
                    QueryProperties.getNeicMetadataServerIP(),
                    QueryProperties.getNeicMetadataServerPort());

            String time = blks.get(0).getTimeString();
            time = time.substring(0, 4) + "," + time.substring(5, 8) + "-" + time.substring(9, 17);

            if (options.sacpz) {
                metaDataServer.getSACResponse(nscl, options.getBegin(), options.pzunit, filename + ".pz");
            }

            md = metaDataServer.getChannelMetaData(nscl, options.getBegin());
        }

        // Set the byteOrder based on native architecture and sac statics
        sac.nvhdr = 6;                // Only format supported
        sac.b = 0.;           // beginning time offsed
        sac.e = (span.getNsamp() / span.getRate());
        sac.iftype = SacTimeSeries.ITIME;
        sac.leven = SacTimeSeries.TRUE;
        sac.delta = (1. / span.getRate());
        sac.depmin = span.getMin();
        sac.depmax = span.getMax();
        sac.nzyear = span.getStart().get(Calendar.YEAR);
        sac.nzjday = span.getStart().get(Calendar.DAY_OF_YEAR);
        sac.nzhour = span.getStart().get(Calendar.HOUR_OF_DAY);
        sac.nzmin = span.getStart().get(Calendar.MINUTE);
        sac.nzsec = span.getStart().get(Calendar.SECOND);
        sac.nzmsec = span.getStart().get(Calendar.MILLISECOND);
        sac.iztype = SacTimeSeries.IB;

        sac.knetwk = nscl.getNetwork();
        sac.kstnm = nscl.getStation();
        sac.kcmpnm = nscl.getChannel();
        sac.khole = nscl.getLocation();

        if (md.getLatitude() != Double.MIN_VALUE) {
            sac.stla = md.getLatitude();
        }
        if (md.getLongitude() != Double.MIN_VALUE) {
            sac.stlo = md.getLongitude();
        }
        if (md.getElevation() != Double.MIN_VALUE) {
            sac.stel = md.getElevation();
        }
        if (md.getDepth() != Double.MIN_VALUE) {
            sac.stdp = md.getDepth();
        }
        if (md.getAzimuth() != Double.MIN_VALUE) {
            sac.cmpaz = md.getAzimuth();
        }
        if (md.getDip() != Double.MIN_VALUE) {
            sac.cmpinc = (md.getDip() + 90.);   // seed is down from horiz, sac is down from vertical
        }

        logger.finer("Sac stla=" + sac.stla + " stlo=" + sac.stlo + " stel=" + sac.stel + " cmpaz=" + sac.cmpaz + " cmpinc=" + sac.cmpinc + " stdp=" + sac.stdp);
        sac.y = new double[span.getNsamp()];   // allocate space for data
        int nodata = 0;
        for (int i = 0; i < span.getNsamp(); i++) {
            sac.y[i] = span.getData(i);
            if (sac.y[i] == fill) {
                nodata++;
            //if(nodata <3) logger.finest(i+" nodata len="+span.getNsamp());
            }
        }
        if (nodata > 0 && !quiet) {
            logger.info("#No data points = " + nodata + " fill=" + fill + " npts=" + sac.npts);
        }
        if (sactrim) {
            int trimmed = sac.trimNodataEnd(fill);
            if (trimmed > 0) {
                logger.info(trimmed + " data points trimmed from end containing no data");
            }
        }
        try {
            sac.write(filename);
        } catch (FileNotFoundException e) {
            logger.severe(e + " File Not found writing SAC");
        } catch (IOException e) {
            logger.severe(e + " IO exception writing SAC");
            throw e;
        }

    }
}
