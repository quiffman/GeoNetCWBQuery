package gov.usgs.anss.query.options;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

public class BatchFileOptions extends GlobalOptions {

	public static String FILE = "file";

	private static FlaggedOption file = new FlaggedOption("file",
			FileStringParser.getParser().setMustExist(true).setMustBeFile(true),
			JSAP.NO_DEFAULT, JSAP.REQUIRED, 'f', "file",
			"File mode or batch mode:\n" +
			// TODO: use query mode usage here!!!
			"Create a file with one line per query with lines like [QUERY SWITCHES][OUTPUT SWITCHES]:\n" +
			"example line :   '-s NSCL -b yyyy/mm/dd hh:mm:ss -d duration -t sac -o %N_%y_%j'\n" +
			"Then run CWB Query with  '-f filename' filename with list of SNCL start times and durations.");

	public BatchFileOptions() throws JSAPException {
		super();
		this.addOption(file);
	}

}