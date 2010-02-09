/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import edu.sc.seis.TauP.SacTimeSeries;
import gov.usgs.anss.query.cwb.data.CWBDataServer;
import gov.usgs.anss.query.metadata.ChannelMetaData;
import gov.usgs.anss.query.metadata.MetaDataServer;
import java.util.TreeSet;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class SacFileFactory {

    public void setCWBDataServer(CWBDataServer cwbServer) {
    }

    public void setMetaDataServer(MetaDataServer metaDataServer) {
    }

    public void makeFiles(DateTime being, double duration, String nsclSelectString, String mask, Integer fill, boolean trim, Quakeml quakeml) {
    }

    protected SacTimeSeries makeTimeSeries(TreeSet miniSeed) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected SacTimeSeries setChannelHeader(SacTimeSeries sac, ChannelMetaData md) {
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

        return sac;
    }

    protected SacTimeSeries setEventHeader(SacTimeSeries timeSeries, Quakeml quakeml) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void outputFile(SacTimeSeries timeSeries) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
