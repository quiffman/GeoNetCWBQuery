package gov.usgs.anss.query.metadata;

import gov.usgs.anss.util.StaSrv;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author geoffc
 */
public class MetaDataServer {

    private StaSrv stasrv;
    private static final String pzunit = "nm";
    protected static final Logger logger = Logger.getLogger(MetaDataServer.class.getName());

    // This is used to format the query to the meta data server,
    // not the CWB server.  It looks like the format is different
    // so we will format it ourselves here instead of creating
    //  a new instance of options.
    private static String beginFormat = "YYYY/MM/dd-HH:mm:ss";
    private static DateTimeFormatter parseBeginFormat = DateTimeFormat.forPattern(beginFormat).withZone(DateTimeZone.forID("UTC"));

    static {
        logger.fine("$Id$");
    }

    /**
     *
     * @param metaDataServerHost
     * @param metaDataServerPort
     */
    public MetaDataServer(String metaDataServerHost, int metaDataServerPort) {
        stasrv = new StaSrv(metaDataServerHost, metaDataServerPort);
    }

    // TODO - this is really the component meta data - should probably
    // rename it.
    /**
     *
     * @param network
     * @param code 
     * @param component
     * @param location
     * @param date
     * @return
     */
    public ChannelMetaData getChannelMetaData(
            String network,
            String code,
            String component,
            String location,
            DateTime date) {
        System.out.println(parseBeginFormat.withZone(DateTimeZone.UTC).print(date));
        String s = getResponseData(network, code, component, location, date, pzunit);

        ChannelMetaData md = new ChannelMetaData(network, code, component, location);

        try {
            BufferedReader in = new BufferedReader(new StringReader(s));
            String line = "";
            while ((line = in.readLine()) != null) {
                if (line.indexOf("LAT-SEED") > 0) {
                    md.setLatitude(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("LONG-SEED") > 0) {
                    md.setLongitude(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("ELEV-SEED") > 0) {
                    md.setElevation(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("AZIMUTH") > 0) {
                    md.setAzimuth(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("DIP") > 0) {
                    md.setDip(Double.parseDouble(line.substring(15)));
                } else if (line.indexOf("DEPTH") > 0) {
                    md.setDepth(Double.parseDouble(line.substring(15)));
                }
            }

        } catch (IOException e) {
            logger.severe("Error parsing metadata " + e.getMessage());
        }

        if (md.getLatitude() == Double.MIN_VALUE && md.getLongitude() == Double.MIN_VALUE) {
            logger.warning("      ***** " + network + code + component + location +
                    " Did not get station location.  Server is down or missing meta data?");
        }

        if (md.getAzimuth() == Double.MIN_VALUE && md.getDip() == Double.MIN_VALUE) {
            logger.warning("      ***** " + network + code + component + location +
                    " Did not get component orientation.  Server is down or missing meta data?");
        }

        return md;
    }

    private String getResponseData(
            String network,
            String code,
            String component,
            String location,
            DateTime date,
            String units) {
        // TODO - should be able to use NSCL object here eventually.
        String s = stasrv.getSACResponse(network.toUpperCase() + code.toUpperCase() + component.toUpperCase() + location, 
                parseBeginFormat.withZone(DateTimeZone.UTC).print(date),
                units);

        // Its not at all clear how this will get triggered
        // there has to be message come back from the server for
        // this to work...
        int loop = 0;
        while (s.indexOf("MetaDataServer not up") >= 0) {
            logger.warning("MetaDataServer is not up - waiting for connection");
            if (loop++ % 15 == 1) {
                logger.warning("MetaDataServer is not up - waiting for connection");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            s = stasrv.getSACResponse(network.toUpperCase() + code.toUpperCase() + component.toUpperCase() + location, parseBeginFormat.withZone(DateTimeZone.UTC).print(date), pzunit);
        }
        return s;
    }

    /**
     *
     * @param network
     * @param code
     * @param component
     * @param location
     * @param date 
     * @param units
     * @param filename
     */
    public void getSACResponse(
            String network,
            String code,
            String component,
            String location,
            DateTime date,
            String units,
            String filename) {
        String s = getResponseData(network, code, component, location, date, pzunit);

        try {
            PrintWriter fout = new PrintWriter(filename);
            fout.write(s);
            fout.close();
        } catch (IOException e) {
            logger.severe("OUtput error writing sac response file " + filename + ".resp e=" + e.getMessage());
        }
    }
}
