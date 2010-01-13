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
public class EdgeQueryOptionsParseAndValidateTest {

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
			// args line,	expected valid,		failure message
			{new EdgeQueryOptions("-f file.txt"), true, "Batch file mode should pass."},
			{new EdgeQueryOptions("-f \"file with spaces.txt\""), true, "Batch file mode (quoted with spaces) should pass."},
			{new EdgeQueryOptions("-f file.txt -ls"), false, "Batch file mode should fail with extra args."},

			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\""), true, "seedname and begin options should be enough."},
			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -sacpz um"), true, "sacpz should have nm or um units."},
			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -sacpz fail"), false, "sacpz should have nm or um units."},
			{new EdgeQueryOptions("-s \"NZWLGT\""), false, "should fail without begin time."},
			{new EdgeQueryOptions("-b \"2009/01/01 00:00:00\""), false, "seedname is not optional."},

			{new EdgeQueryOptions("-s \"NZWLGT\" -event 3212109"), true, "seedname and event options should be enough."},

			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -t NULL"), true, "type null is legal."},

			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -t sac -o \"blah_%s\""), false, "Sac file names must include at least channel."},
			{new EdgeQueryOptions("-s \"NZWLGT\" -b \"2009/01/01 00:00:00\" -t msz -o \"blah_%s\""), false, "msz file names must include at least channel."},
		});
    }

	private EdgeQueryOptions options;
	private boolean valid;
	private String message;

    public EdgeQueryOptionsParseAndValidateTest(EdgeQueryOptions options, boolean valid, String message) {
        this.options = options;
        this.valid = valid;
		this.message = message;
    }

    @Test
    public void testParseAndValidate() {
		assertEquals(message, valid, options.isValid());
    }

}