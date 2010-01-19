package gov.usgs.anss.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author richardg
 */
public class SeedName {
	private String network,station,channel,location;
	
	public enum Format {
		NSCL(Pattern.compile("([A-Z]{2})([A-Z ]{5})([A-Z]{3})([0-9]{2})")),
		SCNL(Pattern.compile("")),
		SLCN(Pattern.compile(""));
		
		private Pattern pattern;

		Format(Pattern pattern) {
			this.pattern = pattern;
		}

		public String[] split(String input) {
			Matcher m = pattern.matcher(input);
			return new String[]{m.group(1), m.group(2), m.group(3), m.group(4)};
		}
	}

	/**
	 * TODO: handle whitespace and/or wildcards...?
	 * @param network
	 * @param station
	 * @param channel
	 * @param location
	 */
	public SeedName(String network, String station, String channel, String location) {
		this.network = network;
		this.station = station;
		this.channel = channel;
		this.location = location;
	}

	//TODO throw IllegalArgumentException...?
	public static SeedName stringToSeedName(SeedName.Format format, String input) {
		String[] e = format.split(input);
		
		return new SeedName(e[0],e[1],e[2],e[3]);
	}

	/**
	 * Returns a new SeedName object constructed from the NNSSSSSCCCLL input String.
	 * @param input 12 character String formatted as NNSSSSSCCLL.
	 * @return a new SeedName representation of the input NSCL.
	 */
	public static SeedName nsclStringToSeedName(String input) {
		return new SeedName(input.substring(0, 2), input.substring(2, 7),
				input.substring(7, 10), input.substring(10, 12));
	}

	/**
	 * @return the network
	 */
	public String getNetwork() {
		return network;
	}

	/**
	 * @param network the network to set
	 */
	public void setNetwork(String network) {
		this.network = network;
	}

	/**
	 * @return the station
	 */
	public String getStation() {
		return station;
	}

	/**
	 * @param station the station to set
	 */
	public void setStation(String station) {
		this.station = station;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * True if both SeedName objects are not null and both NSCL String
	 * components are equal.
	 * Throws an NullPointerException if any of the NSCL components in this are null.
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		SeedName other = (SeedName) obj;
		if (other == null) {
			return false;
		}

		return this.network.equals(other.network) &&
				this.station.equals(other.station) &&
				this.channel.equals(other.channel) &&
				this.location.equals(other.location);
	}

	@Override
	public String toString() {
		return (network + station + channel + location);
	}

}
