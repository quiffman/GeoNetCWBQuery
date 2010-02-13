/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.NSCL;
import gov.usgs.anss.query.ZeroFilledSpan;
import gov.usgs.anss.query.cwb.data.CWBDataServer;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import gov.usgs.anss.query.metadata.MetaDataQuery;
import gov.usgs.anss.query.metadata.MetaDataServer;
import gov.usgs.anss.seed.MiniSeed;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Logger;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class SacFileFactory {

    private static final Logger logger = Logger.getLogger(SacFileFactory.class.getName());
    private CWBDataServer cwbServer;
    private MetaDataServer metaDataServer;


    static {
        logger.fine("$Id$");
    }

    public void setCWBDataServer(CWBDataServer cwbServer) {
        this.cwbServer = cwbServer;
    }

    public void setMetaDataServer(MetaDataServer metaDataServer) {
        this.metaDataServer = metaDataServer;
    }

    public void makeFiles(DateTime begin, double duration, String nsclSelectString, String mask, Integer fill, boolean gaps, boolean trim, Quakeml quakeml) {
        cwbServer.query(begin, duration, nsclSelectString);
        while (cwbServer.hasNext()) {
            SacTimeSeries sac = makeTimeSeries(cwbServer.getNext(), begin, duration, fill, gaps, trim, quakeml);
            outputFile(sac);
        }
    }

    public SacTimeSeries makeTimeSeries(TreeSet<MiniSeed> miniSeed, DateTime begin, double duration, Integer fill, boolean gaps, boolean trim, Quakeml quakeml) {
        // This logic isn't strictly the same as SacOutputter.
        if (!gaps && fill == null) {
            fill = 2147000000;
        }

        NSCL nscl = NSCL.stringToNSCL(miniSeed.first().getSeedName());

        // Use the span to populate a sac file

        TimeZone tz = TimeZone.getTimeZone("GMT+0");
        TimeZone.setDefault(tz);
        GregorianCalendar start = new GregorianCalendar();
        start.setTimeInMillis(begin.getMillis());

        // build the zero filled area (either with exact limits or with all blocks)
        ZeroFilledSpan span = new ZeroFilledSpan(new ArrayList(miniSeed), start, duration, fill);
        if (span.getRate() <= 0.00) {
            return null;         // There is no real data to put in SAC
        }


        logger.fine("ZeroSpan=" + span.toString());

        int noval = span.getNMissingData();

        if (!gaps && span.hasGapsBeforeEnd()) {
            logger.warning("  ** " + nscl.toString() + " has gaps - discarded # missing =" + noval);
            return null;
        }

        //ZeroFilledSpan span = new ZeroFilledSpan(blks);

        SacTimeSeries sac = new SacTimeSeries();
        sac.npts = span.getNsamp();

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

        sac.knetwk = nscl.getNetwork().replaceAll("_", "").trim();
        sac.kstnm = nscl.getStation().replaceAll("_", "").trim();
        sac.kcmpnm = nscl.getChannel().replaceAll("_", "").trim();
        sac.khole = nscl.getLocation().replaceAll("_", "").trim();

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
        if (nodata > 0) {
            logger.finest("#No data points = " + nodata + " fill=" + fill + " npts=" + sac.npts);
        }
        if (trim) {
            int trimmed = sac.trimNodataEnd(fill);
            if (trimmed > 0) {
                logger.info(trimmed + " data points trimmed from end containing no data");
            }
        }

        if (metaDataServer != null) {
            MetaDataQuery mdq = new MetaDataQuery(metaDataServer);
            ChannelMetaData md = mdq.getChannelMetaData(nscl, begin);
            sac = SacHeaders.setChannelHeader(sac, md);
            }

        if (quakeml != null) {
 //           setEventHeader(sac, quakeml);
        }

        return sac;
    }

    protected SacTimeSeries setEventHeader(SacTimeSeries timeSeries, Quakeml quakeml) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void outputFile(SacTimeSeries timeSeries) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
