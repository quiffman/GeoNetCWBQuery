/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.anss.query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author geoffc
 */
public class StationMetaDataTest {

    private static MetaDataServer metaDataServer;

    public StationMetaDataTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

        metaDataServer = new MetaDataServer(
                QueryProperties.getNeicMetadataServerIP(),
                QueryProperties.getNeicMetadataServerPort());

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetSACResponse() {

        System.out.println(metaDataServer.getSACResponse("NZ", "WEL  ", "HHZ", "10", "2009,010-11:11:11"));
fail();

    }

}