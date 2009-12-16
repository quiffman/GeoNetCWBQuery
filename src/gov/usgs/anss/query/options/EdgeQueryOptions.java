package gov.usgs.anss.query.options;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

public class EdgeQueryOptions {

	private static Switch help = new Switch("help", 'h', "help", "Show this help message."); //context sensitive?
	private static Switch quiet = new Switch("quiet", 'q', "quiet",
			"Run in quiet mode (no progress or file status reporting).");
	private static Switch debug = new Switch("debug", JSAP.NO_SHORTFLAG, "debug",
			"Turn on debugging output to stdout.");
	private static FlaggedOption file = new FlaggedOption("file", FileStringParser.getParser(),
			JSAP.NO_DEFAULT, JSAP.REQUIRED, 'f', "file",
			"File mode or batch mode:\n" +
			// TODO: use query mode usage here!!!
			"Create a file with one line per query with lines like [QUERY SWITCHES][OUTPUT SWITCHES]:\n" +
			"example line :   '-s NSCL -b yyyy/mm/dd hh:mm:ss -d duration -t sac -o %N_%y_%j'\n" +
			"Then run CWB Query with  '-f filename' filename with list of SNCL start times and durations.");
	private static Switch lsoption = new Switch("lsoption", JSAP.NO_SHORTFLAG, "ls",
			"List the data files available for queries (not generally useful to users).");
	private static Switch lschannels = new Switch("lschannels", JSAP.NO_SHORTFLAG, "lsc",
			"List every channel its days of availability from begin time through duration (default last 15 days). Use the -b and -d options to set limits on time interval of the channel list. This option can be cpu intensive if you ask for a long interval, so use only as needed.");
	private static Switch hold = new Switch("holdingMode", JSAP.NO_SHORTFLAG, "hold",
			"sends the blocks to the TCP/IP based holdings server.  The user can override the gacqdb/7996:CW if necessary.");
	private static FlaggedOption seedname = new FlaggedOption("seedname", JSAP.STRING_PARSER,
			JSAP.NO_DEFAULT, JSAP.REQUIRED, 's', "seedname",
			"NSCL or REGEXP  (note: on many shells its best to put this argument in double quotes)\n" +
			"NNSSSSSCCCLL to specify a seed channel name. If < 12 characters, match any seednames starting\n" +
			"with those characters.  Example : '-s IUA' would return all IU stations starting with 'A' (ADK,AFI,ANMO,ANTO)\n" +
			"OR\n" +
			"REGEXP :'.' matches any character, [ABC] means one character which is A, or B or C The regexp is right padded with '.'\n" +
			"Example: '-s IUANMO.LH[ZN]..' returns the vertical and north component for LH channels at station(s) starting with 'ANMO'\n" +
			"'.*' matchs zero or more following characters (include in quotes as * has many meanings!\n" +
			"'AA|BB' means matches pattern AA or BB e.g.  'US.*|IU.*' matches the US and IU networks");
	private static FlaggedOption type = new FlaggedOption("type",
			EnumeratedStringParser.getParser("ms; msz; sac; dcc; dcc512; text; null"),
			"sac", JSAP.REQUIRED, 't', "type",
			"Select the output type.\n" +
			"ms is raw blocks with gaps/overlaps (ext='.ms')\n" +
			"msz = is data output as continuous mini-seed with filling use -fill to set other fill values (ext='.msz') can also be output as gappy miniseed with -msgaps NOTE: msz rounds times to nearest millsecond\n" +
			"sac = is Seismic Analysis Code format (see -fill for info on nodata code) (ext='.sac')\n" +
			"dcc = best effort reconciliation to 4096 byte mini-seed form.  Overlaps are eliminated. (ext='.msd')\n" +
			"dcc512 = best effort reconciliation to 512 byte mini-seed form.  Overlaps are eliminated. (ext='.msd')\n" +
			"text = simple text format with space separated values, one row per sample: 'Epoch milliseconds' 'value' This can be extremely verbose.\n" +
			"null = do not create data file, return blocks to caller (for use from a user program)");
	private static FlaggedOption begin = new FlaggedOption("begin", JSAP.STRING_PARSER, //TODO extend the JSAP parser to tranparently enforce joda time here.
			JSAP.NO_DEFAULT, JSAP.REQUIRED, 'b', "being",
			"yyyy/mm/dd hh:mm:ss The time to start the query.");
	private static FlaggedOption duration = new FlaggedOption("duration", JSAP.STRING_PARSER, //TODO extend DoubleStringParser to work with days...?
			"300", JSAP.NOT_REQUIRED, 'd', "duration",
			"nnnn[d] seconds of duration end with 'd' to indicate nnnn is in days.");
	private static FlaggedOption output = new FlaggedOption("output", JSAP.STRING_PARSER,
			"%N", JSAP.NOT_REQUIRED, 'o', "output",
			"mask Put the output in the given filename described by the mask/tokens\n" +
			"Tokens: (Any non-tokens will be literally in the file name)\n" +
			"%N the whole seedname NNSSSSSCCCLL\n" +
			"%n the two letter SEED network          %s the 5 character SEED station code\n" +
			"%c the 3 character SEED channel         %l the two character location\n" +
			"%y Year as 4 digits                     %Y 2 character Year\n" +
			"%j Day of year (1-366)                  %J Julian day (since 1572)\n" +
			"%M 2 digit month                        %D 2 digit day of month\n" +
			"%h 2 digit hour                         %m 2 digit minute\n" +
			"%S 2 digit second                       %z zap all underscores from name");

	public static JSAPResult parseArgs(String[] args) {
		return null;
	}

	public static void main(String[] args) throws JSAPException {
		JSAP globalJsap = new JSAP();
		globalJsap.registerParameter(help);
		globalJsap.registerParameter(debug);
		globalJsap.registerParameter(quiet);

		JSAP fileJsap = new JSAP();
		fileJsap.registerParameter(file);

		JSAP lsoptionJsap = new JSAP();
		lsoptionJsap.registerParameter(lsoption);
		lsoptionJsap.registerParameter(begin.setRequired(false));
		lsoptionJsap.registerParameter(duration.setRequired(false));

		JSAP lschannelsJsap = new JSAP();
		lschannelsJsap.registerParameter(lschannels);
		lschannelsJsap.registerParameter(begin);
		lschannelsJsap.registerParameter(duration);

		JSAP queryJsap = new JSAP();
		queryJsap.registerParameter(seedname);
		queryJsap.registerParameter(type);

		JSAPResult config = globalJsap.parse(args);
		if (config.success() && config.getBoolean("help")) {

			System.err.println("\n" +
					"Usage: java -jar GeoNetCWBQuery.jar " + globalJsap.getUsage() +
					"\nand one of:            " + fileJsap.getUsage() +
					"\nor:                    " + lsoptionJsap.getUsage() +
					"\nor:                    " + lschannelsJsap.getUsage() +
					"\nor:                    " + queryJsap.getUsage());

			fileJsap.unregisterParameter(help);
			fileJsap.unregisterParameter(debug);
			fileJsap.unregisterParameter(quiet);

			lsoptionJsap.unregisterParameter(help);
			lsoptionJsap.unregisterParameter(debug);
			lsoptionJsap.unregisterParameter(quiet);

			lschannelsJsap.unregisterParameter(help);
			lschannelsJsap.unregisterParameter(debug);
			lschannelsJsap.unregisterParameter(quiet);

			queryJsap.unregisterParameter(help);
			queryJsap.unregisterParameter(debug);
			queryJsap.unregisterParameter(quiet);

			System.err.println("\n" +
					globalJsap.getHelp() + "\n" +
					fileJsap.getHelp() + "\n" +
					lsoptionJsap.getHelp() + "\n" +
					lschannelsJsap.getHelp() + "\n" +
					queryJsap.getHelp());

			System.exit(1);
		}

	}
}
