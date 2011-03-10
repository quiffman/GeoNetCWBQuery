/*
 * Copyright 2010, Institute of Geological & Nuclear Sciences Ltd or
 * third-party contributors as indicated by the @author tags.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package nz.org.geonet;

/**
 * A generic Version class which reads package version information, usually from
 * a jar's manifest.
 * 
 * @version $Id$
 * @author richardg
 */
public class Version {

	private static String specificationTitle;
	private static String specificationVersion;
	private static String specificationVendor;
	private static String implementationTitle;
	private static String implementationVersion;
	private static String implementationVendor;
	private static String osName;
	private static String osVersion;
	private static String osArch;
	private static String javaVendor;
	private static String javaVersion;

	static {
		Package p = Version.class.getPackage();
		System.out.println(p.toString());
		if (p != null) {
			specificationTitle = p.getSpecificationTitle();
			specificationVendor = p.getSpecificationVendor();
			specificationVersion = p.getSpecificationVersion();
			implementationTitle = p.getImplementationTitle();
			implementationVendor = p.getImplementationVendor();
			implementationVersion = p.getImplementationVersion();
		}

		osName = System.getProperty("os.name");
		osVersion = System.getProperty("os.version");
		osArch = System.getProperty("os.arch");
		javaVendor = System.getProperty("java.vendor");
		javaVersion = System.getProperty("java.version");
	}

	public static String getOS() {
		return osName + " " + osArch + "; " + osVersion;
	}

	public static String getJava() {
		return javaVendor + "; Java/" + javaVersion;
	}

	public static String getUserAgent() {
		return "(" + getOS() + "; " + getJava() + ") " +
				specificationTitle + "/" + implementationVersion;
	}

	public static void main(String[] args) {
		System.out.println(getUserAgent());
	}

	/**
	 * @return the specificationTitle
	 */
	public static String getSpecificationTitle() {
		return specificationTitle;
	}

	/**
	 * @return the specificationVersion
	 */
	public static String getSpecificationVersion() {
		return specificationVersion;
	}

	/**
	 * @return the specificationVendor
	 */
	public static String getSpecificationVendor() {
		return specificationVendor;
	}

	/**
	 * @return the implementationTitle
	 */
	public static String getImplementationTitle() {
		return implementationTitle;
	}

	/**
	 * @return the implementationVersion
	 */
	public static String getImplementationVersion() {
		return implementationVersion;
	}

	/**
	 * @return the implementationVendor
	 */
	public static String getImplementationVendor() {
		return implementationVendor;
	}

	/**
	 * @return the osName
	 */
	public static String getOsName() {
		return osName;
	}

	/**
	 * @return the osVersion
	 */
	public static String getOsVersion() {
		return osVersion;
	}

	/**
	 * @return the osArch
	 */
	public static String getOsArch() {
		return osArch;
	}

	/**
	 * @return the javaVendor
	 */
	public static String getJavaVendor() {
		return javaVendor;
	}

	/**
	 * @return the javaVersion
	 */
	public static String getJavaVersion() {
		return javaVersion;
	}
}

