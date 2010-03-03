/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query.cwb.data;

import gov.usgs.anss.seed.MiniSeed;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/**
 *
 * @author richardg
 */
@RunWith(Parameterized.class)
public class CWBDataServerMSEEDTest {

    private static CWBDataServerMSEED cwbServer;
    private static DateTimeZone tz = DateTimeZone.forID("UTC");

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                    {"-b \"2009/01/01 00:00:00\" -s \"NZWLGT.BTZ40\" -d 600",
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, tz),
                        600d,
                        "NZWLGT.BTZ40",
                        new String[]{"build/NZWLGT_BTZ40-2009-01-01-00-00-00.ms"}
                    },
                    {"-b \"2009/06/01 12:00:00\" -s \"NZ.....BTZ..\" -d 600",
                        new DateTime(2009, 6, 1, 12, 0, 0, 0, tz),
                        600d,
                        "NZ.....BTZ..",
                        new String[]{"build/NZAUCT_BTZ40-2009-06-01-11-59-51.ms", "build/NZAUCT_BTZ41-2009-06-01-11-59-17.ms", "build/NZCHIT_BTZ40-2009-06-01-11-59-27.ms", "build/NZCHIT_BTZ41-2009-06-01-11-59-33.ms", "build/NZGIST_BTZ40-2009-06-01-11-59-50.ms", "build/NZGIST_BTZ41-2009-06-01-11-59-38.ms", "build/NZLOTT_BTZ40-2009-06-01-11-59-58.ms", "build/NZLOTT_BTZ41-2009-06-01-11-59-37.ms", "build/NZNAPT_BTZ40-2009-06-01-11-59-40.ms", "build/NZNAPT_BTZ41-2009-06-01-11-59-32.ms", "build/NZNCPT_BTZ40-2009-06-01-11-59-58.ms", "build/NZNCPT_BTZ41-2009-06-01-11-59-50.ms", "build/NZRBCT_BTZ40-2009-06-01-11-59-40.ms", "build/NZRBCT_BTZ41-2009-06-01-11-59-43.ms", "build/NZRFRT_BTZ40-2009-06-01-11-59-50.ms", "build/NZRFRT_BTZ41-2009-06-01-11-59-58.ms", "build/NZTAUT_BTZ40-2009-06-01-11-59-47.ms", "build/NZTAUT_BTZ41-2009-06-01-11-59-21.ms", "build/NZWLGT_BTZ40-2009-06-01-11-59-56.ms", "build/NZWLGT_BTZ41-2009-06-01-11-59-35.ms"}
                    },
                    {"-b \"2009/01/01 00:00:00\" -s \"NZMRZ..HHZ10\" -d 1800",
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, tz),
                        1800d,
                        "NZMRZ..HHZ10",
                        new String[]{"build/NZMRZ__HHZ10-2009-01-01-00-00-00.ms"}
                    },
                    {"-b \"2009/01/01 00:00:00\" -s \"XXARSE\"",
                        new DateTime(2009, 1, 1, 0, 0, 0, 0, tz),
                        300d,
                        "XXARSE",
                        new String[]{}
                    },
					{"-b \"2009/01/01 00:00:00\" -s \"NZWEL..HH[ENZ]..|NZBFZ..HHZ..|.*WAZ.*20\" -d 300",
						new DateTime(2009, 1, 1, 0, 0, 0, 0, tz),
						300d,
						"NZWEL..HH[ENZ]..|NZBFZ..HHZ..|.*WAZ.*20",
						new String[]{"build/NZBFZ__HHZ10-2009-01-01-00-00-00.ms", "build/NZWAZ__LNE20-2009-01-01-00-00-00.ms", "build/NZWAZ__LNZ20-2009-01-01-00-00-00.ms", "build/NZWEL__HHE10-2009-01-01-00-00-00.ms", "build/NZWEL__HHN10-2009-01-01-00-00-00.ms", "build/NZWEL__HHZ10-2009-01-01-00-00-00.ms"}
					},
                });
    }
    private String queryLine;
    private DateTime begin;
    private Double duration;
    private String nsclSelectString;
    private String[] filenames;

    public CWBDataServerMSEEDTest(String queryLine, DateTime begin, Double duration, String nscl, String[] filenames) {
        // Note the -t NULL
        this.queryLine = queryLine;
        this.filenames = filenames;
        this.begin = begin;
        this.duration = duration;
        this.nsclSelectString = nscl;
    }

    @Before
    public void getMSUsingOldClient() throws Exception {
        System.out.println("get ms using old client");
        try {
            Process getMS =
                    Runtime.getRuntime().exec(
                    "java -jar lib-ivy/external/GeoNetCWBQuery-2.0.0-RC1.jar " + queryLine + " -t ms -o build/%N-%y-%M-%D-%h-%m-%S.ms");
            getMS.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(CWBDataServerMSEEDTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * To make netbeans work with junit tests incorporating System.out.print calls.
     */
    @After
    public void tearDown() {
        System.out.println();
    }

    /**
     * Test of query method, of class EdgeQueryClient.
     * -b "2009/01/01 00:00:00" -s "NZWLGT.BTZ40" -d 600 -t ms -o "%N-%Y-%M-%D-%h-%m-%S"
     * generates NZWLGT_BTZ40-09-01-01-00-00-00
     *
     * TODO: Change to parameterized test of query lines and list of file names.
     */
    @Test
    public void testQuery() throws Exception {
        System.out.println("query");

        // Load the blocks
        ArrayList<ArrayList<MiniSeed>> expResult = new ArrayList<ArrayList<MiniSeed>>();
        for (String filename : filenames) {
            File ms = new File(filename);
            long fileSize = ms.length();
            ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>((int) (fileSize / 512));

            byte[] buf = new byte[512];
            FileInputStream in = new FileInputStream(ms);
            for (long pos = 0; pos < fileSize; pos += 512) {
                if (in.read(buf) == -1) {
                    break;
                }
                blks.add(new MiniSeed(buf));
            }
            expResult.add(blks);
        }


        ArrayList<TreeSet<MiniSeed>> result = new ArrayList<TreeSet<MiniSeed>>();
        cwbServer = new CWBDataServerMSEED("cwb.geonet.org.nz", 80);
        cwbServer.query(begin, duration, nsclSelectString);

        while (cwbServer.hasNext()) {
            result.add(cwbServer.getNext());
        }

        assertEquals("collection lengths", expResult.size(), result.size());

        for (int i = 0; i < result.size(); i++) {
            assertCollectionEquals("entire collection", expResult.get(i), result.get(i));
        }
    }

    private static void assertCollectionEquals(
            Collection<? extends Comparable> c1,
            Collection<? extends Comparable> c2) {
        assertCollectionEquals(null, c1, c2);
    }

    private static void assertCollectionEquals(String message,
            Collection<? extends Comparable> c1,
            Collection<? extends Comparable> c2) {

        assertEquals(message + ": size mismatch", c1.size(), c2.size());

        Iterator<? extends Comparable> i1 = c1.iterator();
        Iterator<? extends Comparable> i2 = c2.iterator();

        while (i1.hasNext() && i2.hasNext()) {
			Comparable o1 = i1.next();
			Comparable o2 = i2.next();
            assertEquals(message + ": comparison inequality (" + o1.toString() +
					"," + o2.toString() + ")", 0, o1.compareTo(o2));
        }

    }
}