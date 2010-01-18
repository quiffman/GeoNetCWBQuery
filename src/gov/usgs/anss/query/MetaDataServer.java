/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.util.StaSrv;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

/**
 *
 * @author geoffc
 */
public class MetaDataServer {

    private StaSrv stasrv;
    // Hard coded - this is the units that the
    // response is returned in.  We aren't going to
    // work with the response and Rich said this was ok...
    private String pzunit = "nm";
    protected static final Logger logger = Logger.getLogger(SacPZ.class.getName());


    static {
        logger.fine("$Id$");
    }

    public MetaDataServer(String metaDataServerHost, int metaDataServerPort) {
        stasrv = new StaSrv(metaDataServerHost, metaDataServerPort);
    }

    /**
     *
     * @param stationCode - the station code.  Must be padded to 5 char e.g., 'WEL  '.
     * @param network
     * @param component
     * @param time - the time to retrieve the response at yyyy,ddd-hh:mm:ss or yyyy/mm/dd-hh:mm:ss
     * @return
     */
    public StationMetaData getStationMetaData(
            String network,
            String code,
            String component,
            String location,
            String time) {
        // TODO pad the station code to 5 char left justified
        String s = stasrv.getSACResponse(network.toUpperCase() + code.toUpperCase() + component.toUpperCase() + location, time, pzunit);
        int loop = 0;
        while (s.indexOf("MetaDataServer not up") >= 0) {
            if (loop++ % 15 == 1) {
                logger.warning("MetaDataServer is not up - waiting for connection");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            s = stasrv.getSACResponse(network.toUpperCase() + code.toUpperCase() + component.toUpperCase() + location, time, pzunit);
        }

        StationMetaData md = new StationMetaData(network, code, component, location);

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

        return md;
    }
}
