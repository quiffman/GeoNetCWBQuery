/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class CwbHoldings {

//    private Socket ds;
//    private InputStream in;
//    private OutputStream out;

    private String cwbHost;
    private int cwbPort;

    protected static final Logger logger = Logger.getLogger(CwbHoldings.class.getName());

    public CwbHoldings(String cwbHost, int cwbPort) {
        this.cwbHost = cwbHost;
        this.cwbPort = cwbPort;
    }

    public String listChannels(DateTime begin, Double duration) {
        EdgeQueryOptions options = new EdgeQueryOptions();
        options.lschannels = true;
        options.setBegin(begin);
        options.setHost(cwbHost);
        options.setPort(cwbPort);
        options.setDuration(duration);
        String s = EdgeQueryClient.listQuery(options);

        BufferedReader sr = new BufferedReader(new StringReader(s));

        String nextLine = null;
        try {
            while ((nextLine = sr.readLine()) != null) {

                if (lscToNscl(nextLine) != null) {
                    System.out.println("bob: " + lscToNscl(nextLine));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CwbHoldings.class.getName()).log(Level.SEVERE, null, ex);
        }

        return EdgeQueryClient.listQuery(options);
    }

    public static String lscToNscl(String lsc) {
        String r;

        if (lsc.startsWith("There are") || lsc.isEmpty()) {
            r = null;
        } else {
            r = lsc.substring(0, 12);
        }

        return r;
    }


}
