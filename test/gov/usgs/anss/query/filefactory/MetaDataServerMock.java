/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.filefactory;

import gov.usgs.anss.query.metadata.*;
import gov.usgs.anss.query.NSCL;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class MetaDataServerMock implements MetaDataServer {

    private String s;

    public MetaDataServerMock(String ip, int port) {
    }

    public void setS(String s) {
        this.s = s;
    }

    public void loadPAZFile(String filename) {

        StringBuilder contents = new StringBuilder();

        InputStream in = MetaDataServerMock.class.getResourceAsStream(filename);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        } catch (IOException x) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(MetaDataServerMock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }


        this.s = contents.toString();

    }

    public String getResponseData(NSCL nscl, DateTime date, String units) {
        return this.s;
    }
}
