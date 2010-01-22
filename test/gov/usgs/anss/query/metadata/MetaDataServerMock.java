/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query.metadata;

import gov.usgs.anss.query.NSCL;
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


    public String getResponseData(NSCL nscl, DateTime date, String units) {
        return this.s;
    }
}
