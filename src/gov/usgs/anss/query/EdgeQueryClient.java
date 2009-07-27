/*
 * EdgeQueryClient.java
 *
 * Created on February 9, 2006, 1:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import gov.usgs.anss.edge.*;
import gov.usgs.anss.seed.MiniSeed;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Collections;

import gov.usgs.anss.util.SeedUtil;
import gov.usgs.anss.util.Util;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/** This class is the main class for CWBQuery which allows the user to make queries
 * against all files on a CWB or Edge computer.  The program has two modes :
 * command line, and command file.
 *
 *In command line mode the user can specify all of the options and get data from
 *a single seedname mask.
 *
 *In command file mode some of the command line args come from the command line
 * while the seedname mask, start time and duration come from the command file
 *
 *The command line arguments are :
 * -s seedmask  Set the seedmask as a regular expression (.=anychar, [] set of matches).
 *          The 12 character seedname is NNSSSSSCCCLL N=network, S=station code, 
 *          C= channel code L=location code.
 *          The regular expression can be useful in that IMPD03.[BS]H... would return
 *          all components with network IM, the first 4 chars of station code are PD03,
 *          for all BH or SH components and any location code.
 * -b yyyy/mm/dd hh:mm:ss The time to start the query.
 * -d secs  The number of seconds of data (the duration).
 *
 * -t type Legal types are "sac", "ms", "msz", dcc512, and "dcc" where 
 *         sac = sac binary format. Data will be zero filled and start at next sample
 *              at or after the -b time with exactly the duration
 *         ms = mini-seed raw format.  The mini-seed blocks are returned in sorted order
 *              but they might overlap or be duplicated.
 *         msz = mini-seed but zero-filled and recompressed.  No blk 1000, 1001 are 
 *              preserved.  Data will start at sample follow -b time and there will be
 *              a full duration of the data.  If -msb is specified, use that block length.
 *        dcc = blocks processed to 4096 to best effort Mini-seed eliminating overlaps, etc.
 *        dcc512 = blocks processed to 512 best effort Mini-seed eliminating overlaps, etc.
 * -f filename Use the file command mode.  The list of -s -b -d are in the file one per line
 * -h host    The host of the server computer as a name or dotted IP address
 * -p port    The port on which the service is running
 * -msb  blocksize Set the blocksize for msz output
 *-dbg Turn on the debug flag
 *
 * @author davidketchum
 */
public class EdgeQueryClient {

    static DecimalFormat df2;
    static DecimalFormat df4;
    static DecimalFormat df6;
    private static ResourceBundle props;
	private static final Logger logger = Logger.getLogger(EdgeQueryClient.class.getName());
	static {logger.finest("$Id$");}


    // TODO the two following methods need to go into their own class
    // and be unit tested.  They should also use Joda time.
    public static String makeFilename(String mask, String seedname, MiniSeed ms) {
        StringBuffer sb = new StringBuffer(100);
        if (df2 == null) {
            df2 = new DecimalFormat("00");
        }
        if (df4 == null) {
            df4 = new DecimalFormat("0000");
        }
        if (df6 == null) {
            df6 = new DecimalFormat("000000");
        }
        seedname = seedname.replaceAll(" ", "_");
        int[] ymd = SeedUtil.fromJulian(ms.getJulian());
        String name = mask;
        name = name.replaceAll("%N", seedname.substring(0, 12));
        name = name.replaceAll("%n", seedname.substring(0, 2));
        name = name.replaceAll("%s", seedname.substring(2, 7));
        name = name.replaceAll("%c", seedname.substring(7, 10));
        name = name.replaceAll("%l", seedname.substring(10, 12));
        name = name.replaceAll("%y", "" + ms.getYear());
        name = name.replaceAll("%Y", ("" + ms.getYear()).substring(2, 4));
        name = name.replaceAll("%j", df4.format(ms.getDay()).substring(1, 4));
        name = name.replaceAll("%J", "" + ms.getJulian());
        name = name.replaceAll("%d", df2.format(ymd[2]));
        name = name.replaceAll("%D", df2.format(ymd[2]));
        name = name.replaceAll("%M", df2.format(ymd[1]));
        name = name.replaceAll("%h", df2.format(ms.getHour()));
        name = name.replaceAll("%m", df2.format(ms.getMinute()));
        name = name.replaceAll("%S", df2.format(ms.getSeconds()));
        name = name.replaceAll("%h", df2.format(ms.getHour()));
        if (name.indexOf("%z") >= 0) {
            name = name.replaceAll("%z", "");
            name = name.replaceAll("_", "");
        }
        return name;

    }

    public static String makeFilename(String mask, String seedname, java.util.Date beg) {
        GregorianCalendar g = new GregorianCalendar();
        g.setTimeInMillis(beg.getTime());

        StringBuffer sb = new StringBuffer(100);
        if (df2 == null) {
            df2 = new DecimalFormat("00");
        }
        if (df4 == null) {
            df4 = new DecimalFormat("0000");
        }
        if (df6 == null) {
            df6 = new DecimalFormat("000000");
        }
        seedname = seedname.replaceAll(" ", "_");
        String name = mask;
        name = name.replaceAll("%N", seedname.substring(0, 12));
        name = name.replaceAll("%n", seedname.substring(0, 2));
        name = name.replaceAll("%s", seedname.substring(2, 7));
        name = name.replaceAll("%c", seedname.substring(7, 10));
        name = name.replaceAll("%l", seedname.substring(10, 12));
        name = name.replaceAll("%y", "" + g.get(Calendar.YEAR));
        name = name.replaceAll("%Y", ("" + g.get(Calendar.YEAR)).substring(2, 4));
        name = name.replaceAll("%j", df4.format(g.get(Calendar.DAY_OF_YEAR)).substring(1, 4));

        name = name.replaceAll("%J", "" + SeedUtil.toJulian(g));
        name = name.replaceAll("%d", df2.format(g.get(Calendar.DAY_OF_MONTH)));
        name = name.replaceAll("%D", df2.format(g.get(Calendar.DAY_OF_MONTH)));
        name = name.replaceAll("%M", df2.format(g.get(Calendar.MONTH) + 1));
        name = name.replaceAll("%h", df2.format(g.get(Calendar.HOUR_OF_DAY)));
        name = name.replaceAll("%m", df2.format(g.get(Calendar.MINUTE)));
        name = name.replaceAll("%S", df2.format(g.get(Calendar.SECOND)));
        name = name.replaceAll("%h", df2.format(g.get(Calendar.HOUR_OF_DAY)));
        if (name.indexOf("%z") >= 0) {
            name = name.replaceAll("%z", "");
            name = name.replaceAll("_", "");
        }
        return name;

    }

    /** Creates a new instance of EdgeQueryClient */
    public EdgeQueryClient() {
    }

    /** do a query from a command string, break it into a command args list and call query
     *@param line The command line string
     *@return ArrayList<ArrayList<MiniSeed>> with each channels data on each array list
     */
    static public ArrayList<ArrayList<MiniSeed>> query(String line) {

        // TODO This code should go into a class and be tested
        // I think this is building the string that gets sent to
        // CWP:port to make the query.  Would be useful to have a
        // Class that handled this (if there isn't one in the NB.tar
        // already.
        String[] arg = line.split(" ");
        System.out.println("line=" + line);
        for (int i = 0; i < arg.length; i++) {
            arg[i] = "";
        }
        int narg = 0;
        boolean inQuote = false;
        int beg = 0;
        int end = 0;
        while (end < line.length()) {
            if (inQuote) {
                if (line.charAt(end) == '"' || line.charAt(end) == '\'') {
                    arg[narg++] = line.substring(beg, end).trim();
                    if (arg[narg - 1].equals("")) {
                        narg--;
                    }
                    inQuote = false;
                    beg = end + 1;
                }
            } else {
                if (line.charAt(end) == '"' || line.charAt(end) == '\'') {
                    inQuote = true;
                    beg = end + 1;
                } else if (line.charAt(end) == ' ') {
                    arg[narg++] = line.substring(beg, end).trim();
                    if (arg[narg - 1].equals("")) {
                        narg--;
                    }
                    beg = end + 1;
                }
            }
            end++;
        }
        if (inQuote) {
            System.out.println("Query argument list has open quotes!");
            return new ArrayList<ArrayList<MiniSeed>>(1);
        }
        arg[narg++] = line.substring(beg, end).trim();
        int n = 0;
        for (int i = 0; i < narg; i++) {
            if (!arg[i].equals("")) {
                n++;
            }
        }
        String[] args = new String[n];
        n = 0;
        for (int i = 0; i < narg; i++) {
            if (!arg[i].equals("")) {
                if (line.indexOf("dbg") >= 0) {
                    System.out.println(n + "=" + arg[i] + "|");
                }
                args[n++] = arg[i];
            }
        }
        return query(args);
    }

    /** do a query.  The command line arguments are passed in as they are for the query tool
     * a files is created unless -t null is specified.  In that case the return is an ArrayList
     * containing ArrayLists<MiniSeed> for each channel returned
     *@param args The String array with args per the documentation
     *@return The ArrayList with ArrayLists of miniseed one for each channel returned.
     */
    static public ArrayList<ArrayList<MiniSeed>> query(String[] args) {


        String line = "";

        props = ResourceBundle.getBundle("resources.geonetCwbQuery");

        if (args.length == 0) {
            System.out.println(props.getString("usage"));
            return null;
        }


        // TODO should check that these are set and exit if not.
        // TODO if they are set log a message and continue
        String host = props.getString("cwbip");
        int port = Integer.parseInt(props.getString("queryport"));

        long msSetup = 0;
        long msConnect = 0;
        long msTransfer = 0;
        long msOutput = 0;
        long msCommand = 0;
        long startTime = System.currentTimeMillis();
        long startPhase = startTime;

        byte[] b = new byte[4096];
        Outputer out = null;
        if (df6 == null) {
            df6 = new DecimalFormat("000000");
        }
        GregorianCalendar jan_01_2007 = new GregorianCalendar(2007, 0, 1);

        ArrayList<ArrayList<MiniSeed>> blksAll = null;
        double duration = 300.;
        String seedname = "";
        String begin = "";
        String type = "sac";
        boolean dbg = false;
        boolean lsoption = false;
        boolean lschannels = false;
        java.util.Date beg = null;
        int julian = 0;
        String filenamein = " ";
        String filename = "";
        int blocksize = 512;        // only used for msz type
        BufferedReader infile = null;
        String filemask = "%N";
        boolean reset = false;
        boolean quiet = false;
        boolean gapsonly = false;
        boolean dbgdup = false;
        // Make a pass for the command line args for either mode!
        String exclude = "";
        boolean nosort = false;
        String durationString = "";
        boolean holdingMode = false;
        String holdingIP = "";
        int holdingPort = 0;
        String holdingType = "";
        boolean showIllegals = false;
        boolean perfMonitor = false;
        boolean chkDups = false;
        boolean sacpz = false;
        SacPZ stasrv = null;
        String pzunit = "nm";
        String stahost = props.getString("metadataserver");

        // TODO probably a more elegant way of handling command line args out there.

        // This loop must validate all arguments on the command line, parsing is really done below
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-f")) {
                filenamein = args[i + 1];
                i++;
            } else if (args[i].equals("-eqdbg")); else if (args[i].equals("-t")) {
                type = args[i + 1];
                i++;
            } else if (args[i].equals("-msb")) {
                blocksize = Integer.parseInt(args[i + 1]);
                i++;
            } else if (args[i].equals("-o")) {
                filemask = args[i + 1];
                i++;
            } else if (args[i].equals("-e")) {
                exclude = "exclude.txt";
            } else if (args[i].equals("-el")) {
                exclude = args[i + 1];
                i++;
            } else if (args[i].equals("-ls")) {
                lsoption = true;
            } else if (args[i].equals("-lsc")) {
                lschannels = true;
                lsoption = true;
            } else if (args[i].equals("-reset")) {
                reset = true;
            } else if (args[i].equals("-b")) {
                begin = args[i + 1];
                i++;
            } else if (args[i].equals("-s")) {
                seedname = args[i + 1];
                i++;
            } else if (args[i].equals("-d")) {
                durationString = args[i + 1];
                i++;
            } else if (args[i].equals("-q")) {
                quiet = true;
            } else if (args[i].equals("-fap")); // legal for wf and wf1 types
            else if (args[i].equals("-nosort")) {
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
            } else if (args[i].equals("-little")); else if (args[i].equals("-dbgdup")) {
                dbgdup = true;
            } else if (args[i].equals("-nometa")); else if (args[i].equals("-fill")) {
                i++;
            } else if (args[i].equals("-sacpz")) {
                sacpz = true;
                if (i + 1 > args.length) {
                    System.out.println(" ***** -sacpz units must be either um or nm and is required!");
                    System.exit(0);
                }

                pzunit = args[i + 1];
                if (stahost == null || stahost.equals("")) {
                    stahost = "137.227.230.1";
                }
                if (!args[i + 1].equalsIgnoreCase("nm") && !args[i + 1].equalsIgnoreCase("um")) {
                    System.out.println("   ****** -sacpz units must be either um or nm switch values is " + args[i + 1]);
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
                System.out.println("Holdings server=" + holdingIP + "/" + holdingPort + " type=" + holdingType);
            } else {
                System.out.println("Unknown CWB Query argument=" + args[i]);
            }

        }

        if (reset) {
            try {
                Socket ds = new Socket(host, port);
                InputStream in = ds.getInputStream();        // Get input and output streams
                OutputStream outtcp = ds.getOutputStream();
                line = "'-reset'\n";

                outtcp.write(line.getBytes());
                return null;
            } catch (IOException e) {
                System.err.print(e + " Getting a directory");
            }
            return null;
        }
        // The ls option does not require any args checking
        if (lsoption) {
            try {
                Socket ds = new Socket(host, port);
                ds.setReceiveBufferSize(512000);
                //ds.setTcpNoDelay(true);
                InputStream in = ds.getInputStream();        // Get input and output streams
                OutputStream outtcp = ds.getOutputStream();
                if (!exclude.equals("")) {
                    line = "'-el' '" + exclude + "' ";
                } else {
                    line = "";
                }
                if (!begin.equals("")) {
                    line += "'-b' '" + begin.trim() + "' ";
                }
                if (!durationString.equals("")) {
                    line += "'-d' '" + durationString + "' ";
                }
                if (lschannels) {
                    if (showIllegals) {
                        line += "'-si' ";
                    }
                    line += "'-lsc'\n";
                } else {
                    line += "'-ls'\n";
                }
                System.out.println("line=" + line + ":");
                outtcp.write(line.getBytes());
                StringBuffer sb = new StringBuffer(100000);
                int len = 0;
                while ((len = in.read(b, 0, 512)) > 0) {
                    sb.append(new String(b, 0, len));
                }
                System.out.println(sb.toString());
                return null;
            } catch (IOException e) {
                System.err.println(e + " Getting a directory");
                return null;
            }
        }


        // if not -f mode, read in more command line parameters for the run
        if (filenamein.equals(" ")) {
            for (int i = 0; i < args.length; i++) {
                line += args[i].replaceAll(" ", "@") + " ";
            }
            infile = new BufferedReader(new StringReader(line));
        } else {
            try {
                infile = new BufferedReader(new FileReader(filenamein));
            } catch (FileNotFoundException e) {
                System.out.println("did not find the input file=" + filenamein);
                return null;
            }
        }

        // the "in" BufferedReader will give us the command lines we need for the other end
        try {
            // for each line of input, read it, reformat it with single quotes, send to server
            int nline = 0;
            int totblks = 0;
            // particularly for the DCC we want this program to not error out if we cannot connect to the server
            // So make sure we can connect and print messages
            Socket ds = null;
            while (ds == null) {
                try {
                    ds = new Socket(host, port);
                } catch (IOException e) {
                    ds = null;
                    if (e != null) {
                        if (e.getMessage() != null) {
                            if (e.getMessage().indexOf("Connection refused") >= 0) {
                                System.out.println("Got a connection refused. " + host + "/" + port + "  Is the server up?  Wait 20 and try again");
                            }
                        } else {
                            System.out.println("Got IOError opening socket to server e=" + e);
                        }
                    } else {
                        System.out.println("Got IOError opening socket to server e=" + e);
                    }
                    Util.sleep(20000);
                }
            }
            InputStream in = ds.getInputStream();        // Get input and output streams
            OutputStream outtcp = ds.getOutputStream();
            msConnect += (System.currentTimeMillis() - startPhase);
            startPhase = System.currentTimeMillis();
            while ((line = infile.readLine()) != null) {
                if (line.length() < 2) {
                    continue;
                }
                nline++;
                boolean on = false;

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

                args = line.split(" ");
                for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-b")) {
                        begin = args[i + 1].replaceAll("@", " ");
                    } else if (args[i].equals("-s")) {
                        seedname = args[i + 1].replaceAll("@", " ");
                    } else if (args[i].equals("-d")) {
                        if (args[i + 1].endsWith("d") || args[i + 1].endsWith("D")) {
                            duration = Double.parseDouble(args[i + 1].substring(0, args[i + 1].length() - 1)) * 86400.;
                        } else {
                            duration = Double.parseDouble(args[i + 1]);
                        }
                    } else if (args[i].equals("-t")) {
                        type = args[i + 1];
                        i++;
                    } else if (args[i].equals("-msb")) {
                        blocksize = Integer.parseInt(args[i + 1]);
                        i++;
                    } else if (args[i].equals("-o")) {
                        filemask = args[i + 1].replaceAll("@", " ");
                        i++;
                    } else if (args[i].equals("-e")) {
                        exclude = "exclude.txt";
                    } else if (args[i].equals("-el")) {
                        exclude = args[i + 1].replaceAll("@", " ");
                        i++;
                    } else if (args[i].equals("-q")) {
                        quiet = true;
                    } else if (args[i].equals("-nosort")) {
                        nosort = true;
                    } else if (args[i].indexOf("-hold") == 0) {
                        args[i] = "-gaps";     // change to tel other end gaps mode
                    }
                }
                if (blocksize != 512 && blocksize != 4096) {
                    System.out.println("-msb must be 512 or 4096 and is only meaningful for msz type");
                    return null;
                }
                if (begin.equals("")) {
                    System.out.println("You must enter a beginning time @line " + nline);
                    return null;
                } else {
                    beg = Util.stringToDate2(begin);
                    if (beg.before(Util.stringToDate2("1970/12/31/ 23:59"))) {
                        System.out.println("the -b field date did not parse correctly. @line" + nline);
                        return null;
                    }
                }
                if (seedname.equals("")) {
                    System.out.println("-s SCNL is not optional.  Specify a seedname @line" + nline);
                    return null;
                }
                if (type.equals("ms") || type.equals("msz") || type.equals("sac") ||
                        type.equals("dcc") || type.equals("dcc512") ||
						type.equals("HOLD") || type.equals("text")) {
                    if (seedname.length() < 12) {
                        seedname = (seedname + ".............").substring(0, 12);
                    }
                    if (type.equals("ms")) {
                        out = new MSOutputer(nosort);
                    }
                    if (type.equals("sac")) {
                        out = new SacOutputer();
                    }
                    if (type.equals("msz")) {
                        out = new MSZOutputer(blocksize);
                    }
                    if (type.equals("dcc")) {
                        out = new DCCOutputer();
                    }
                    if (type.equals("dcc512")) {
                        out = new DCC512Outputer();
                    }
                    if (type.equals("HOLD")) {
                        out = new HoldingOutputer();
                    }
                    if (type.equals("text")) {
                        out = new TextOutputer();
                    }
                } else if (type.equals("null")) {
                    out = null;
                    blksAll = new ArrayList<ArrayList<MiniSeed>>(20);
                } else {
                    System.out.println("Output format not supported.  Choose dcc, dcc512, ms, msz, or sac");
                    return null;
                }

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

                // put command line in single quotes.
                line = "";
                long maxTime = 0;
                int ndups = 0;
                for (int i = 0; i < args.length; i++) {
                    if (!args[i].equals("")) {
                        line += "'" + args[i].replaceAll("@", " ") + "' ";
                    }
                }
                line = line.trim() + "\t";
                try {
                    msSetup += (System.currentTimeMillis() - startPhase);
                    startPhase = System.currentTimeMillis();
                    boolean perfStart = true;
                    outtcp.write(line.getBytes());
                    int iblk = 0;
                    String lastComp = "            ";
                    boolean eof = false;
                    MiniSeed ms = null;
                    if (type.equals("sac")) {
                        if (compareLength < 10) {
                            System.out.println("\n    ***** Sac files must have names including the channel! *****");
                            return null;
                        }

                    }
                    if (type.equals("msz") && compareLength < 10) {
                        System.out.println("\n    ***** msz files must have names including the channel! *****");
                        return null;
                    }
                    int npur = 0;
                    ArrayList<MiniSeed> blks = new ArrayList<MiniSeed>(100);

                    while (!eof) {
                        try {
                            // Try to read a mini-seed, if it failes mark eof
                            if (read(in, b, 0, (gapsonly ? 64 : 512))) {
                                if (b[0] == '<' && b[1] == 'E' && b[2] == 'O' && b[3] == 'R' && b[4] == '>') {
                                    eof = true;
                                    ms = null;
                                    if (dbg) {
                                        System.out.println("EOR found");
                                    }
                                } else {
                                    ms = new MiniSeed(b);
                                    //SSystem.out.println(""+ms);
                                    if (!gapsonly && ms.getBlockSize() != 512) {
                                        read(in, b, 512, ms.getBlockSize() - 512);
                                        ms = new MiniSeed(b);
                                    }
                                    iblk++;
                                    totblks++;
                                }
                            } else {
                                eof = true;         // still need to process this last channel THIS SHOULD NEVER  HAPPEN unless socket is lost
                                ms = null;
                                System.out.println("   *** Unexpected EOF Found");
                                if (out != null) {
                                    System.exit(1);      // error out with no file
                                }
                            }
                            if (perfStart) {
                                msCommand += (System.currentTimeMillis() - startPhase);
                                startPhase = System.currentTimeMillis();
                                perfStart = false;

                            }
                            //System.out.println(iblk+" "+ms.toString());
                            if (!quiet && iblk % 1000 == 0 && iblk > 0) {
                                System.out.print("\r            \r" + iblk + "...");
                            }

                            if (eof || (lastComp.trim().length() > 0 &&
                                    (ms == null ? true : !lastComp.substring(0, compareLength).equals(ms.getSeedName().substring(0, compareLength))))) {
                                msTransfer += (System.currentTimeMillis() - startPhase);
                                startPhase = System.currentTimeMillis();
                                if (!quiet) {
                                    int nsgot = 0;
                                    if (blks.size() > 0) {
                                        Collections.sort(blks);
                                        //System.out.println(blks.size() +" "+iblk);
                                        for (int i = 0; i < blks.size(); i++) {
                                            nsgot += (blks.get(i)).getNsamp();
                                        }
                                        //System.out.println(""+(MiniSeed) blks.get(blks.size()-1));
                                        System.out.println("\r" + Util.asctime() + " Query on " + lastComp.substring(0, compareLength) + " " +
                                                df6.format(blks.size()) + " mini-seed blks " +
                                                (blks.get(0) == null ? "Null" : ((MiniSeed) blks.get(0)).getTimeString()) + " " +
                                                (blks.get((blks.size() - 1)) == null ? "Null" : (blks.get(blks.size() - 1)).getEndTimeString()) + " " +
                                                " ns=" + nsgot);
                                    } else {
                                        System.out.println("\rQuery on " + seedname + " returned 0 blocks!");
                                    }


                                }
                                if (blks.size() > 0) {
                                    MiniSeed ms2 = blks.get(0);
                                    if (out == null) {     // Get the array list output
                                        ArrayList<MiniSeed> newBlks = new ArrayList<MiniSeed>(blks.size());
                                        for (int i = 0; i < blks.size(); i++) {
                                            newBlks.add(i, blks.get(i));
                                        }
                                        blksAll.add(newBlks);
                                    } else {      // create the output file
                                        if (type.equals("ms") || type.equals("dcc") || type.equals("dcc512") || type.equals("msz")) {
                                            filename = EdgeQueryClient.makeFilename(filemask, lastComp, ms2);
                                        } else {
                                            filename = EdgeQueryClient.makeFilename(filemask, lastComp, beg);
                                        }


                                        //filename = lastComp;
                                        filename = filename.replaceAll(" ", "_");
                                        if (dbg) {
                                            System.out.println(((MiniSeed) blks.get(0)).getTimeString() + " to " +
                                                    ((MiniSeed) blks.get(blks.size() - 1)).getTimeString() +
                                                    " " + (((MiniSeed) blks.get(0)).getGregorianCalendar().getTimeInMillis() -
                                                    ((MiniSeed) blks.get(blks.size() - 1)).getGregorianCalendar().getTimeInMillis()) / 1000L);
                                        }
                                        // Due to a foul up in data in Nov, Dec 2006 it is possible the Q330s got the
                                        // same baler block twice, but the last 7 512's of the block zeroed and the other
                                        // correct.  Find these and purge the bad ones.

                                        if (!gapsonly) {
                                            for (int i = blks.size() - 1; i >= 0; i--) {
                                                if (blks.get(i).getBlockSize() == 4096 && // Has to be a big block or it does not happen
                                                        blks.get(i).getGregorianCalendar().compareTo(jan_01_2007) < 0 &&
                                                        blks.get(i).getUsedFrameCount() < blks.get(i).getB1001FrameCount() &&
                                                        blks.get(i).getUsedFrameCount() <= 7 && blks.get(i).getB1001FrameCount() > 7) {
                                                    blks.remove(i);
                                                    npur++;
                                                }
                                            }
                                        }
                                        //System.out.println("Found "+npur+" recs with on first block of 4096 valid");
                                        blks.trimToSize();
                                        //for(int i=0; i<blks.size(); i++) System.out.println(((MiniSeed) blks.get(i)).toString());
                                        if (sacpz && out.getClass().getSimpleName().indexOf("SacOutputer") < 0) {   // if asked for write out the sac response file
                                            String time = blks.get(0).getTimeString();
                                            time = time.substring(0, 4) + "," + time.substring(5, 8) + "-" + time.substring(9, 17);
                                            stasrv.getSACResponse(lastComp, begin, filename);
                                        }

                                        out.makeFile(lastComp, filename, filemask, blks, beg, duration, args);
                                    }
                                }
                                maxTime = 0;
                                if (blks.size() > 0) {
                                    blks.clear();
                                    System.gc();        // Lots of memory just abandoned.  Try garbage collector
                                }
                                msOutput += (System.currentTimeMillis() - startPhase);
                                startPhase = System.currentTimeMillis();
                            }

                            // If this block is the first in a new component, clear the blks array
                            //if(!lastComp.substring(0,compareLength).equals(
                            //    ms.getSeedName().substring(0,compareLength))) blks.clear();
              /* in late 2007 there was some files which were massively duplicated by block.
                             * to prevent this from blowing memory when there are so may we eliminate and duplicate
                             * blocks here.  If it is massively out of order , all of these block checks will slow things
                             * down.
                             **/
                            boolean isDuplicate = false;
                            if (ms != null) {
                                if (ms.getTimeInMillis() <= maxTime) {    // No need to check duplicates if this is newest seen
                                    if (!gapsonly) {
                                        if (blks.size() >= 1) {
                                            for (int i = blks.size() - 1; i >= 0; i--) {
                                                if (ms.isDuplicate(blks.get(i))) {
                                                    isDuplicate = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (dbgdup && isDuplicate) {
                                        System.out.println("Dup:" + ms);
                                    }
                                    if (!isDuplicate && ms.getIndicator().compareTo("D ") >= 0) {
                                        blks.add(ms);
                                    } else {
                                        ndups++;
                                    }
                                } else {
                                    if (ms.getIndicator().compareTo("D ") >= 0) {
                                        blks.add(ms); // If its not D or better, its been zapped!
                                    }
                                    maxTime = ms.getTimeInMillis();
                                }
                                lastComp = ms.getSeedName();
                            }
                        } catch (IllegalSeednameException e) {
                            System.out.println("Seedname exception making a seed record e=" + e.getMessage());
                        }
                    }   // while(!eof)
                    if (!quiet && iblk > 0) {
                        System.out.println(iblk + " Total blocks transferred in " +
                                (System.currentTimeMillis() - startTime) + " ms " +
                                (iblk * 1000L / Math.max(System.currentTimeMillis() - startTime, 1)) + " b/s " + npur + " #dups=" + ndups);
                    }
                    if (out == null) {
                        return blksAll;      // If called in no file output mode, return the blocks
                    }
                    blks.clear();
                } catch (UnknownHostException e) {
                    System.out.println("EQC main: Host is unknown=" + host + "/" + port);
                    if (out != null) {
                        System.exit(1);
                    }
                    return null;
                } catch (IOException e) {
                    if (e.getMessage().equalsIgnoreCase("Connection refused")) {
                        System.out.println("The connection was refused.  Server is likely down or is blocked. This should never happen.");
                        return null;
                    } else {
                        System.err.println(e + " EQC main: IO error opening/reading socket=" + host + "/" + port);
                        if (out != null) {
                            System.exit(1);
                        }
                    }
                }
            }       // End of readline
            outtcp.write("\n".getBytes());      // Send end of request marker to query
            if (ds.isClosed()) {
                try {
                    ds.close();
                } catch (IOException e) {
                }
            }
            if (perfMonitor) {
                long msEnd = System.currentTimeMillis() - startPhase;
                System.out.println("Perf setup=" + msSetup + " connect=" + msConnect + " Cmd=" + msCommand + " xfr=" + msTransfer + " out=" + msOutput +
                        " last=" + msEnd + " tot=" + (msSetup + msConnect + msTransfer + msOutput + msEnd) + " #blks=" + totblks + " #lines=" + nline);
            }
            return null;
        } catch (IOException e) {
            System.err.println(e + " IOError reading input lines.");
        }
        return null;
    }

    public static boolean read(InputStream in, byte[] b, int off, int l)
            throws IOException {
        int len;
        while ((len = in.read(b, off, l)) > 0) {
            off += len;
            l -= len;
            if (l == 0) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {

        // TODO - not sure this is doing anything.
        Util.setModeGMT();

		logger.finest("Running Edge Query");

        ArrayList<ArrayList<MiniSeed>> mss = EdgeQueryClient.query(args);
        if (mss == null) {
            // TODO Evil - needs a log message.
			logger.severe("Query returned null.");
            System.exit(1);
        }
        System.exit(0);
    }
}
