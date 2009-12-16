package gov.usgs.anss.query.options;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

public class BatchFileOptionsTest {


	private AbstractOptions fileOptions;

    public BatchFileOptionsTest() throws JSAPException {
        fileOptions = new BatchFileOptions();
    }

	@Test
	public void testHeirachy() {
		JSAPResult config = fileOptions.parseArgs(new String[]{"--file","build.xml","-h","--debug","-q"});
		assertNotNull("config was null", config);
		assertTrue("config did not succeed", config.success());
		assertTrue("no file",config.contains(BatchFileOptions.FILE));
	}

	@Test
	public void testFile() {
		JSAPResult config = fileOptions.parseArgs(new String[]{"-f","build.xml"});
		assertNotNull("config was null", config);
		assertTrue("config did not succeed", config.success());
		assertTrue("no file",config.contains(BatchFileOptions.FILE));
	}

	@Test
	public void testNonExistentFile() {
		JSAPResult config = fileOptions.parseArgs(new String[]{"-f","nonexistentfile"});
		assertNotNull("config was null", config);
		assertFalse("config should not succeed", config.success());
		assertFalse("should be no file",config.contains(BatchFileOptions.FILE));
	}

	@Test
	public void testDirectory() {
		JSAPResult config = fileOptions.parseArgs(new String[]{"-f","/tmp"});
		assertNotNull("config was null", config);
		assertFalse("config should not succeed", config.success());
		assertFalse("should be no file",config.contains(BatchFileOptions.FILE));
	}

//    @Test
//    public void testParseBegin() {
//        beg.setTimeZone(tz);
//        result.setTime(BatchFileOptionsTest.parseBegin(beginTime));
//        beg.set(GregorianCalendar.MILLISECOND, 0);
//        assertEquals(beg, result);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testParseBeginError() {
//        result.setTime(BatchFileOptionsTest.parseBegin(beginTime + "junk"));
//    }
}