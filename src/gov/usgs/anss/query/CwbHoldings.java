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

    private Socket ds;
    private InputStream in;
    private OutputStream out;
    protected static final Logger logger = Logger.getLogger(CwbHoldings.class.getName());

    public CwbHoldings(String CwbHost, int CwbPort) {
        try {
            ds = new Socket(CwbHost, CwbPort);
            ds.setReceiveBufferSize(512000);
            in = ds.getInputStream();        // Get input and output streams
            out = ds.getOutputStream();

        } catch (UnknownHostException ex) {
            Logger.getLogger(CwbHoldings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CwbHoldings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String listChannels(DateTime begin, Double duration) {
        EdgeQueryOptions options = new EdgeQueryOptions();
        options.lschannels = true;
        options.setBegin(begin);
        options.setDuration(duration);
        String s = listQuery(options);

        BufferedReader sr = new BufferedReader(new StringReader(s));

        String nextLine = null;
        try {
            while ((nextLine = sr.readLine()) != null) {
                
                if (nextLine.startsWith("There are") || nextLine.isEmpty()) {
                    continue;
                }
                System.out.println("bob: " + lscToNscl(nextLine));
            }
        } catch (IOException ex) {
            Logger.getLogger(CwbHoldings.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listQuery(options);
    }

public static String lscToNscl (String lsc) {
    return lsc.substring(0, 12);
}


    // Basically cut and paste out of EdgeQueryClient
    private String listQuery(EdgeQueryOptions options) {

        byte[] b = new byte[4096];
        String line = "";
        //ds.setTcpNoDelay(true);

        if (options.exclude != null) {
            line = "'-el' '" + options.exclude + "' ";
        } else {
            line = "";
        }
        if (options.getBegin() != null) {
            line += "'-b' '" + options.getBeginAsString() + "' ";
        }
        if (options.getDuration() != null) {
            line += "'-d' '" + options.getDuration() + "' ";
        }
        if (options.lschannels) {
            if (options.showIllegals) {
                line += "'-si' ";
            }
            line += "'-lsc'\n";
        } else {
            line += "'-ls'\n";
        }

        logger.config("line=" + line + ":");
        try {
            out.write(line.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(CwbHoldings.class.getName()).log(Level.SEVERE, null, ex);
        }

        StringBuffer sb = new StringBuffer(100000);
        int len = 0;
        try {
            while ((len = in.read(b, 0, 512)) > 0) {
                sb.append(new String(b, 0, len));
            }
        } catch (IOException ex) {
            Logger.getLogger(CwbHoldings.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
}
