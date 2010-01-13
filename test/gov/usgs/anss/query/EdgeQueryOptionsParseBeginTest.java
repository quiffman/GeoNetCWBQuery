/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
@RunWith(Parameterized.class)
public class EdgeQueryOptionsParseBeginTest {

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                    // beginTime         ms        Date
                    {"1970/12/31 23:59:12", 0, new GregorianCalendar(1970, 11, 31, 23, 59, 12)},
                    {"2009/12/31 23:59:12", 0, new GregorianCalendar(2009, 11, 31, 23, 59, 12)},
                    {"1970/12/31 03:59:12", 0, new GregorianCalendar(1970, 11, 31, 03, 59, 12)},
                    {"2009/12/31 03:59:12", 0, new GregorianCalendar(2009, 11, 31, 03, 59, 12)},
                    {"2009,022-23:59:12", 0, new GregorianCalendar(2009, 00, 22, 23, 59, 12)},
                    {"2009,022-03:59:12", 0, new GregorianCalendar(2009, 00, 22, 03, 59, 12)},
                    {"2009,095-23:59:12", 0, new GregorianCalendar(2009, 03, 05, 23, 59, 12)},
                    {"2008,095-23:59:12", 0, new GregorianCalendar(2008, 03, 04, 23, 59, 12)},

                    {"2009/12/31 23:59:12", 48000, new GregorianCalendar(2010, 0, 1, 0, 0, 0)},
                    {"1970/12/31 03:59:12", 1000, new GregorianCalendar(1970, 11, 31, 03, 59, 13)},
                    {"2009/12/31 03:59:12", -4000, new GregorianCalendar(2009, 11, 31, 03, 59, 8)},
		});
    }
    private String beginTime;
	private long offset;
    private GregorianCalendar beg;
    private TimeZone tz = TimeZone.getTimeZone("GMT+0");
    private GregorianCalendar result = new GregorianCalendar(tz);

    public EdgeQueryOptionsParseBeginTest(String beginTime, long offset, GregorianCalendar beg) {
        this.beginTime = beginTime;
		this.offset = offset;
        this.beg = beg;
    }

    @Test
    public void testParseBegin() {
        beg.setTimeZone(tz);
        result.setTime(EdgeQueryOptions.parseBegin(beginTime, offset));
        beg.set(GregorianCalendar.MILLISECOND, 0);
        assertEquals(beg, result);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseBeginError() {
        result.setTime(EdgeQueryOptions.parseBegin(beginTime + "junk", offset));
    }
}