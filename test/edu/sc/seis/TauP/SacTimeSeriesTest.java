/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.sc.seis.TauP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic serialization test for SacTimeSeriesTest.
 * @author richardg
 */
public class SacTimeSeriesTest {

    public SacTimeSeriesTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

	@Test
	public void serializationTest() throws Exception {
		MessageDigest digest = MessageDigest.getInstance("MD5");

		InputStream in = getClass().getResourceAsStream("/sac-data/test-one/NZOUZ__HHZ10.sac");
		
		File originalFile = File.createTempFile("sac_serialization", null);
		originalFile.deleteOnExit();
		OutputStream out = new FileOutputStream(originalFile);
		
		//Copy and md5 at the same time
		byte[] b = new byte[8192];
		
		int len;
		while ((len = in.read(b)) != -1) {
			out.write(b, 0, len);
			digest.update(b, 0, len);
		}		
		out.close();
		
		byte[] md51 = digest.digest();


		SacTimeSeries sac = new SacTimeSeries();
		sac.read(originalFile);
		
		File newFile = File.createTempFile("sac_serialization", null);
		newFile.deleteOnExit();

		sac.write(newFile);
		in = new FileInputStream(newFile);

		while ((len = in.read(b)) != -1) {
			digest.update(b, 0, len);
		}
		byte[] md52 = digest.digest();

//		System.out.println(toHexString(md51));
//		System.out.println(toHexString(md52));

		assertArrayEquals(md51, md52);
	}

	private static String toHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2 + 1);
		for (byte b : bytes) {
			int v = b & 0xff;
			sb.append(hexChars[v >> 4]);
			sb.append(hexChars[v & 0xf]);
		}
		return sb.toString();
	}
	private static final char[] hexChars = {
		'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
	};
}
