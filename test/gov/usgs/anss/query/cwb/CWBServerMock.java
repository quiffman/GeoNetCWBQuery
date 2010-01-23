/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb;

import org.joda.time.DateTime;

/**
 *
 * @author geoffc
 */
public class CWBServerMock implements CWBServer {

    public CWBServerMock(String host, int port) {
    }
    
    private String channels;

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String listChannels(DateTime begin, Double duration) {
        return this.channels;
    }
}
