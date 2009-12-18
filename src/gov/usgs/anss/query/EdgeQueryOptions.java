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
	
	public double duration = 300.;
	public String seedname = "";
	public String begin = "";
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
	public String exclude = "";
	public boolean nosort = false;
	public String durationString = "";
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

	public void parseArgs(String[] args) {

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
				lsoption = true;
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
				if (i + 1 > args.length) {
					logger.warning(" ***** -sacpz units must be either um or nm and is required!");
					System.exit(0);
				}

				pzunit = args[i + 1];
				if (stahost == null || stahost.equals("")) {
					logger.warning("no metadata server set.  Exiting.");
					System.exit(0);
				}
				if (!args[i + 1].equalsIgnoreCase("nm") && !args[i + 1].equalsIgnoreCase("um")) {
					logger.warning("   ****** -sacpz units must be either um or nm switch values is " + args[i + 1]);
					System.exit(0);
				}
				stasrv = new SacPZ(stahost, pzunit);
				i++;
			} else if (args[i].equals("-si")) {
				showIllegals = true;
			} else if (args[i].indexOf("-hold") == 0) {
				holdingMode = true;
				gapsonly = true;
				type = "HOLD";
				logger.config("Holdings server=" + holdingIP + "/" + holdingPort + " type=" + holdingType);
			} else {
				logger.warning("Unknown CWB Query argument=" + args[i]);
			}

		}
	}

	public EdgeQueryOptions(String[] args) {
		this.args = args;
		this.parseArgs(this.args);
	}

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
		this.parseArgs(this.args);
	}

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
