package gov.usgs.anss.query.options;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Switch;

public class GlobalOptions extends AbstractOptions {

	public static String HELP = "help";
	public static String QUIET = "quiet";
	public static String DEBUG = "debug";
	
	private static Switch help = new Switch(HELP, 'h', "help", "Show this help message."); //context sensitive?
	private static Switch quiet = new Switch(QUIET, 'q', "quiet",
			"Run in quiet mode (no progress or file status reporting).");
	private static Switch debug = new Switch(DEBUG, JSAP.NO_SHORTFLAG, "debug",
			"Turn on debugging output to stdout.");

	public GlobalOptions() throws JSAPException {
		super();
		this.addOption(help);
		this.addOption(quiet);
		this.addOption(debug);
	}

}