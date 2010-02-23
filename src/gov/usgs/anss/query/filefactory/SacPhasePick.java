/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query.filefactory;

/**
 *
 * @author geoffc
 */
public class SacPhasePick implements Comparable<SacPhasePick> {

    protected String phaseName;

    /**
     * Get the value of phaseName
     *
     * @return the value of phaseName
     */
    public String getPhaseName() {
        return phaseName;
    }

    /**
     * Set the value of phaseName
     *
     * @param phaseName new value of phaseName
     */
    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }
    protected double timeAfterOriginInSeconds;

    /**
     * Get the value of timeAfterOriginInSeconds
     *
     * @return the value of timeAfterOriginInSeconds
     */
    public double getTimeAfterOriginInSeconds() {
        return timeAfterOriginInSeconds;
    }

    /**
     * Set the value of timeAfterOriginInSeconds
     *
     * @param timeAfterOriginInSeconds new value of timeAfterOriginInSeconds
     */
    public void setTimeAfterOriginInSeconds(double timeAfterOriginInSeconds) {
        this.timeAfterOriginInSeconds = timeAfterOriginInSeconds;
    }


    public SacPhasePick(String phaseName, double timeAfterOriginInSeconds) {
        this.phaseName = phaseName;
        this.timeAfterOriginInSeconds = timeAfterOriginInSeconds;
    }


    public int compareTo(SacPhasePick o) {
        return (int) (this.timeAfterOriginInSeconds - o.getTimeAfterOriginInSeconds());
    }
}
