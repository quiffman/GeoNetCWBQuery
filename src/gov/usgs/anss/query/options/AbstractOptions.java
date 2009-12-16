package gov.usgs.anss.query.options;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;

public abstract class AbstractOptions {
	protected JSAP jsap;

	public AbstractOptions() {
		jsap = new JSAP();
	}
	
	protected void addOption(Parameter parameter) throws JSAPException {
		jsap.registerParameter(parameter);
	}

	protected JSAPResult parseArgs(String[] args) {
		return jsap.parse(args);
	}
}