/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.anss.query;

import gov.usgs.anss.seed.MiniSeed;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
public class EdgeQueryClientTest {

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
			// args line,	filenames...
			// Note the -t NULL
			{"-b \"2009/01/01 00:00:00\" -s \"NZWLGT.BTZ40\" -d 600 -t NULL", new String[] {"test/NZWLGT_BTZ40-09-01-01-00-00-00.ms"}},
		});
    }

	private EdgeQueryOptions options;
	private String[] filenames;

    public EdgeQueryClientTest(String queryLine, String[] filenames) {
        this.options = new EdgeQueryOptions(queryLine);
		this.filenames = filenames;
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
		for(String filename : filenames) {
			File ms = new File(filename);
			long fileSize = ms.length();
			ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>((int) (fileSize / 512));

			byte[] buf = new byte[512];
			FileInputStream in = new FileInputStream(ms);
			for (long pos = 0; pos < fileSize; pos += 512) {
				if (in.read(buf) == -1) break;
				blks.add(new MiniSeed(buf));
			}
			expResult.add(blks);
		}
		
		// Run the query
		ArrayList<ArrayList<MiniSeed>> result = EdgeQueryClient.query(options);
		assertCollectionEquals("entire collection", expResult.get(0), result.get(0));
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
			assertEquals(message + ": comparison inequality", 0, i1.next().compareTo(i2.next()));
		}
		
	}
}