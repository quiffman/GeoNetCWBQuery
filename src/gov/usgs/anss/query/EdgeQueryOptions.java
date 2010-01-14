/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlFactory;
import nz.org.geonet.quakeml.v1_0_1.client.QuakemlUtils;
import nz.org.geonet.quakeml.v1_0_1.domain.Quakeml;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * An attempt to encapsulate (read isolate) EdgeQueryClient command line args.
 * 
 * @author richardg
 */
public class EdgeQueryOptions {

	private static final Logger logger = Logger.getLogger(EdgeQueryOptions.class.getName());

	static {
		logger.fine("$Id$");
	}
	
	public enum OutputType {
		ms,
		msz,
		sac,
		dcc,
		dcc512,
		HOLD,
		text,
		NULL;
	}


    private static String beginFormat = "YYYY/MM/dd HH:mm:ss";
    private static String beginFormatDoy = "YYYY,DDD-HH:mm:ss";
    private static DateTimeFormatter parseBeginFormat = DateTimeFormat.forPattern(beginFormat).withZone(DateTimeZone.forID("UTC"));
    private static DateTimeFormatter parseBeginFormatDoy = DateTimeFormat.forPattern(beginFormatDoy).withZone(DateTimeZone.forID("UTC"));

	String host = QueryProperties.getGeoNetCwbIP();
	int port = QueryProperties.getGeoNetCwbPort();

	public String[] args;
	public String[] extraArgs;
	private double duration = 300.;
	private String seedname = null;
	public String begin = null;
	private OutputType type = OutputType.sac;
	public boolean dbg = false;
	public boolean lsoption = false;
	public boolean lschannels = false;
	public int julian = 0;
	public String filenamein = null;
	public int blocksize = 512;        // only used for msz type
	public String filemask = "%N";
	public boolean quiet = false;
	public boolean gapsonly = false;
	// Make a pass for the command line args for either mode!
	public String exclude = null;
	public boolean nosort = false;
	public String durationString = null;
	public boolean holdingMode = false;
	public String holdingIP = QueryProperties.getGeoNetCwbIP();
	public int holdingPort = QueryProperties.getGeoNetCwbPort();
	public String holdingType = "CWB";
	public boolean showIllegals = false;
	public boolean perfMonitor = false;
	public boolean chkDups = false;
	public boolean sacpz = false;
	public SacPZ stasrv = null;
	public String pzunit = "nm";
	public String stahost = QueryProperties.getNeicMetadataServerIP();
	public String eventId;
	public long offset = 0;
	private Date beg = null;

	/**
	 * Parses known args into object fields. Does some argument validation and
	 * potentially System.exit(0).
	 * TODO: move any/all validation to a validateArgs method.
	 * TODO: return a String array of unused/unparsed args to be used for
	 * outputter customisation.
	 * @param args the arguments to parse
	 * @return unused args (unmodified order)
	 */
	public String[] parse(String[] args) {
		
//		List<String> argList = Arrays.asList(args);
		ArrayList<String> argList = new ArrayList(Arrays.asList(args));
		int pos = argList.indexOf("-f");
		if (pos != -1) {
			argList.remove(pos);
			filenamein = argList.remove(pos);
			quiet = argList.remove("-q");
			dbg = argList.remove("-dbg");
			if (argList.isEmpty()) {
				return null;
			}

			return argList.toArray(new String[0]);
		}
		
		ArrayList<String> extraArgsList = new ArrayList(args.length);

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f")) {  // Documented functionality.
				filenamein = args[i + 1];
				i++;
			} else if (args[i].equals("-t")) {  // Documented functionality.
				setType(args[i + 1]);
				i++;
			} else if (args[i].equals("-msb")) {   // Documented functionality.
				blocksize = Integer.parseInt(args[i + 1]);
				i++;
			} else if (args[i].equals("-o")) { // Documented functionality.
				filemask = args[i + 1];
				i++;
			} else if (args[i].equals("-e")) {
				exclude = "exclude.txt";
			} else if (args[i].equals("-el")) {
				exclude = args[i + 1];
				i++;
			} else if (args[i].equals("-ls")) { // Documented functionality.
				lsoption = true;
			} else if (args[i].equals("-lsc")) { // Documented functionality.
				lschannels = true;
			} else if (args[i].equals("-b")) { // Documented functionality.
				begin = args[i + 1];
				i++;
			} else if (args[i].equals("-s")) { // Documented functionality.
				setSeedname(args[i + 1]);
				i++;
			} else if (args[i].equals("-d")) { // Documented functionality.
				durationString = args[i + 1];
				i++;
			} else if (args[i].equals("-q")) { // Documented functionality.
				quiet = true;
			} else if (args[i].equals("-nosort")) { // Documented functionality.
				nosort = true;
			} else if (args[i].equals("-nogaps")); // legal for sac and zero MS
			else if (args[i].equals("-nodups")) {
				chkDups = true;
			} else if (args[i].equals("-sactrim")); // legal for sac and zero MS
			else if (args[i].equals("-gaps")) {
				gapsonly = true;     // legal for zero MS
			} else if (args[i].equals("-msgaps")); // legal for zero ms
			else if (args[i].equals("-udphold")) {
				gapsonly = true;  // legal for zero MS
			} else if (args[i].equals("-chk")); // valid only for -t dcc
			else if (args[i].equals("-dccdbg")); // valid only for -t dcc & -t dcc512
			else if (args[i].equals("-perf")) {
				perfMonitor = true;
			} else if (args[i].equals("-nometa")); else if (args[i].equals("-fill")) {
				i++;
			} else if (args[i].equals("-sacpz")) {
				sacpz = true;
				pzunit = args[i + 1];
				stasrv = new SacPZ(stahost, pzunit);
				i++;
			} else if (args[i].equals("-si")) {
				showIllegals = true;
			} else if (args[i].indexOf("-hold") == 0) {
				holdingMode = true;
				gapsonly = true;
				setType(OutputType.HOLD);
				logger.config("Holdings server=" + holdingIP + "/" + holdingPort + " type=" + holdingType);
			} else if (args[i].equals("-event")) {
				eventId = args[i + 1];
				i++;
			} else if (args[i].equals("-offset")) {
				offset = Long.parseLong(args[i + 1]);
				i++;
			}
			else {
				logger.info("Unknown CWB Query argument=" + args[i]);
				extraArgsList.add(args[i]);
			}

		}
		return extraArgsList.toArray(new String[extraArgsList.size()]);
	}

	/**
	 * Return true if the arguments specified a batch file.
	 * @return
	 */
	public boolean isFileMode() {
		return filenamein != null;
	}

	/**
	 * Return true if a list query -ls or -lsc was defined.
	 * @return
	 */
	public boolean isListQuery() {
		return (lsoption || lschannels);
	}

	/**
	 * Validate parsed args. This should (initially at least) mimic the dodgy
	 * args validation of the current client.
	 * TODO: clean up later for context driven help???
	 * @return boolean representing whether the args are valid
	 */
	public boolean isValid() {
		if (isFileMode()) {
			return (extraArgs == null);
		}

		if (isListQuery()) {
			// No args checking done here.
			return true;
		}

		if (blocksize != 512 && blocksize != 4096) {
			logger.severe("-msb must be 512 or 4096 and is only meaningful for msz type");
			return false;
		}

		if (getBeg() == null) {
			logger.severe("You must enter a beginning time");
			return false;
		}
		
		if (getSeedname() == null) {
			logger.severe("-s SCNL is not optional, you must specify a seedname.");
			return false;
		}

		if (stahost == null || stahost.equals("")) {
			logger.warning("no metadata server set.");
			return false;
		}

		if (sacpz) {
			if (!pzunit.equalsIgnoreCase("nm") && !pzunit.equalsIgnoreCase("um")) {
				logger.warning("   ****** -sacpz units must be either um or nm switch values is " + pzunit);
				return false;
			}
		}

		if (getType() == OutputType.sac && getCompareLength() < 10) {
			logger.severe("\n    ***** Sac files must have names including the channel! *****");
			return false;
		}
		if (getType() == OutputType.msz && getCompareLength() < 10) {
			logger.severe("\n    ***** msz files must have names including the channel! *****");
			return false;
		}

		// TODO more checking to come.
		return true;
	}
	
	public Outputer getOutputter() {
		switch (getType()) {
			case ms:
				return new MSOutputer(nosort);
			case sac:
				return new SacOutputer();
			case msz:
				return new MSZOutputer(blocksize);
			case dcc:
				return new DCCOutputer();
			case dcc512:
				return new DCC512Outputer();
			case HOLD:
				return new HoldingOutputer();
			case text:
				return new TextOutputer();
		}
		return null;
	}

	public int getCompareLength() {
		// The length at which our compare for changes depends on the output file mask
		int compareLength = 12;
		if (filemask.indexOf("%n") >= 0) {
			compareLength = 2;
		}
		if (filemask.indexOf("%s") >= 0) {
			compareLength = 7;
		}
		if (filemask.indexOf("%c") >= 0) {
			compareLength = 10;
		}
		if (filemask.indexOf("%l") >= 0) {
			compareLength = 12;
		}
		if (filemask.indexOf("%N") >= 0) {
			compareLength = 12;
		}
		return compareLength;
	}

	/**
	 * Generate some (context?) appropriate help text.
	 * @return the help string.
	 */
	public String getHelp() {
		return QueryProperties.getUsage();
	}

	/**
	 * Generate some (context?) appropriate usage text.
	 * @return the usage string.
	 */
	public String getUsage() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * Creates an EdgeQueryOptions object from a set of command line args.
	 * @param args
	 */
	public EdgeQueryOptions(String[] args) {
		this.args = args;
		this.extraArgs = parse(this.args);
		this.process();
	}

	/**
	 * Creates an EdgeQueryOptions object from a single command line string.
	 * TODO: Attempt to understand and sanitise this method.
	 * @param line
	 */
	public EdgeQueryOptions(String line) {
		boolean on = false;

		// Spaces and quoting...?
		char[] linechar = line.toCharArray();
		for (int i = 0; i < line.length(); i++) {
			if (linechar[i] == '"') {
				on = !on;
			} else if (linechar[i] == ' ') {
				if (on) {
					linechar[i] = '@';
				}
			}
		}

		line = new String(linechar);
		line = line.replaceAll("\"", " ");
		line = line.replaceAll("  ", " ");
		line = line.replaceAll("  ", " ");
		line = line.replaceAll("  ", " ");

		this.args = line.split(" ");
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replaceAll("@", " ");
		}
		this.extraArgs = parse(this.args);
		this.process();
	}

	/**
	 * Returns a FileReader if filename has been specified, or a StringReader if
	 * it just contains command line args.
	 * TODO: replace this with a command line iterator...?
	 * @return
	 * @throws FileNotFoundException
	 */
	public Reader getAsReader() throws FileNotFoundException {

        // if not -f mode, read in more command line parameters for the run
        if (this.filenamein != null) {
			return new FileReader(this.filenamein);
		}

		String line = "";
        for (int i = 0; i < this.args.length; i++) {
			line += this.args[i].replaceAll(" ", "@") + " ";
		}
		return new StringReader(line);

	}

	/**
	 * Puts the command line args in single quotes, to be sent to the server.
	 * TODO: This should be constructed from the fields.
	 * @return the command line args, single quoted.
	 */
	public String getSingleQuotedCommand() {
		// put command line in single quotes.
		String line = "";
		for (int i = 0; i < this.args.length; i++) {
			if (!this.args[i].equals("")) {
				line += "'" + this.args[i].replaceAll("@", " ") + "' ";
			}
		}
		return line.trim() + "\t";
	}

    /**
     * Parses the begin time.  This tries to match
     * the documentation for CWBClient but does not
     * match the Util.stringToDate2 method which attempted
     * to allow for milliseconds.
     *
     * @param beginTime
     * @return java.util.Date parsed from the being time.
     * @throws java.lang.IllegalArgumentException
     */
    protected static java.util.Date parseBegin(String beginTime, long offset) throws IllegalArgumentException {
        DateTime begin = null;

        try {
            begin = parseBeginFormat.parseDateTime(beginTime);
        } catch (Exception e) {
        }

        if (begin == null) {
            try {
                begin = parseBeginFormatDoy.parseDateTime(beginTime);
            } catch (Exception e) {
            }
        }

        // TODO Would be ideal if this error contained any range errors from
        // parseDateTime but this is hard with the two attempts at parsing.
        if (begin == null) {
            throw new IllegalArgumentException("Error parsing begin time.  Allowable formats " +
                    "are: " + beginFormat + " or " + beginFormatDoy);
        }

        return new Date(begin.plus(offset).getMillis());
    }

	/**
	 * Process the more complex args - time etc.
	 */
	private void process() {

		// This logic allows a user to set an eventId for phases, picks etc. but
		// still manually define the begin time for a query.
		if (begin != null) {
			try {
				setBeg(parseBegin(begin, offset));
			} catch (IllegalArgumentException illegalArgumentException) {
				logger.severe("the -b field date [" + begin +
						"] did not parse correctly." + illegalArgumentException);
			}
		}
		else if (eventId != null) {
			Quakeml event = new QuakemlFactory().getQuakemlByEventReference(eventId, null, null);
			if (event != null) {
				DateTime jDate = QuakemlUtils.getOriginTime(QuakemlUtils.getPreferredOrigin(QuakemlUtils.getFirstEvent(event)));
				jDate.plus(offset);
				setBeg(jDate.toDate());
				begin = parseBeginFormat.withZone(DateTimeZone.UTC).print(jDate);
				logger.config("Using begin time " + begin + " from event " + eventId);
				// TODO: Fix this when fixing the command line single quotes.
				args = Arrays.copyOf(args, args.length + 2);
				args[args.length - 2] = "-b";
				args[args.length - 1] = begin;
			} else {
				logger.severe("failed to retrieve details for event id " + eventId);
			}
		}

		if (durationString != null) {
			if (durationString.endsWith("d") || durationString.endsWith("D")) {
				setDuration(Double.parseDouble(durationString.substring(0, durationString.length() - 1)) * 86400.);
			} else {
				setDuration(Double.parseDouble(durationString));
			}
		}
	}

	/**
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}

	/**
	 * @return the seedname
	 */
	public String getSeedname() {
		return seedname;
	}

	/**
	 * @param seedname the seedname to set
	 */
	public void setSeedname(String seedname) {
		// Append wildcards to end of seedname
		if (seedname != null && seedname.length() < 12) {
			this.seedname = (seedname + ".............").substring(0, 12);
		}
		else {
			this.seedname = seedname;
		}
	}

	/**
	 * @return the type
	 */
	public OutputType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(OutputType type) {
		this.type = type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = OutputType.valueOf(type);
	}

	/**
	 * @return the beg
	 */
	public Date getBeg() {
		return beg;
	}

	/**
	 * @param beg the beg to set
	 */
	public void setBeg(Date beg) {
		this.beg = beg;
	}
}
