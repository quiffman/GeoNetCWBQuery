/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

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

	String host = QueryProperties.getGeoNetCwbIP();
	int port = QueryProperties.getGeoNetCwbPort();

	public String[] args;
	public String[] extraArgs;

	public double duration = 300.;
	public String seedname = null;
	public String begin = null;
	public String type = "sac";
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

			return args = argList.toArray(new String[0]);
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f")) {  // Documented functionality.
				filenamein = args[i + 1];
				i++;
			} else if (args[i].equals("-t")) {  // Documented functionality.
				type = args[i + 1];
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
				seedname = args[i + 1];
				i++;
			} else if (args[i].equals("-d")) { // Documented functionality.
				durationString = args[i + 1];
				if (args[i + 1].endsWith("d") || args[i + 1].endsWith("D")) {
					duration = Double.parseDouble(args[i + 1].substring(0, args[i + 1].length() - 1)) * 86400.;
				} else {
					duration = Double.parseDouble(args[i + 1]);
				}
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
				type = "HOLD";
				logger.config("Holdings server=" + holdingIP + "/" + holdingPort + " type=" + holdingType);
			} else if (args[i].equals("-event")) {
				eventId = args[i + 1];
				i++;
			} else if (args[i].equals("-offset")) {
				offset = Long.parseLong(args[i + 1]);
				i++;
			}
				else {
				logger.warning("Unknown CWB Query argument=" + args[i]);
			}

		}
		return args;
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

		throw new UnsupportedOperationException("Not yet implemented");
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
}
