/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.util.StaSrv;
import java.util.logging.Logger;

/**
 *
 * @author geoffc
 */
public class MetaDataServer {

    private StaSrv metaDataServer;
    // Hard coded - this is the units that the
    // response is returned in.  We aren't going to
    // work with the response and Rich said this was ok...
    private String pzunit = "nm";
    protected static final Logger logger = Logger.getLogger(SacPZ.class.getName());


    static {
        logger.fine("$Id$");
    }

    public MetaDataServer(String metaDataServerHost, int metaDataServerPort) {
        metaDataServer = new StaSrv(metaDataServerHost, metaDataServerPort);
    }


    /**
     *
     * @param stationCode - the station code.  Must be padded to 5 char e.g., 'WEL  '.
     * @param network
     * @param component
     * @param time - the time to retrieve the response at yyyy,ddd-hh:mm:ss or yyyy/mm/dd-hh:mm:ss
     * @return
     */
    protected String getSACResponse(
            String network,
            String stationCode,
            String component,
            String location,
            String time
            ) {
        // TODO don't know if this is case sensitive for code network etc.
        String s = metaDataServer.getSACResponse(network.toUpperCase() + stationCode.toUpperCase() + component.toUpperCase() + location, time, pzunit);
        int loop = 0;
        while (s.indexOf("MetaDataServer not up") >= 0) {
            if (loop++ % 15 == 1) {
                logger.warning("MetaDataServer is not up - waiting for connection");
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            s = metaDataServer.getSACResponse(network.toUpperCase() + stationCode.toUpperCase() + component.toUpperCase() + location, time, pzunit);
        }
        return s;
    }
}
