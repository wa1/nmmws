/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.erlundin.ninemenmorris;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Erik
 */
public class ServerConfigurationTest {
    
    public ServerConfigurationTest() {
    }
    /**
     * Test of saveAuthToken method, of class ServerConfiguration.
     */
    @Ignore
    @Test
    public void testSaveAuthToken() {
        System.out.println("saveAuthToken");
        String _token = "abcd1234tooookie";
        boolean expResult = true;
        boolean result = ServerConfiguration.saveAuthToken(_token);
        assertEquals(expResult, result);
    }   

    /**
     * Test of updateAuthToken method, of class ServerConfiguration.
     */
    @Ignore
    @Test
    public void testUpdateAuthToken() {
        System.out.println("updateAuthToken");
        ServerConfiguration.updateAuthToken();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAuthToken method, of class ServerConfiguration.
     */
    @Test
    public void testGetAuthToken() {
        System.out.println("getAuthToken");
        String result = ServerConfiguration.getAuthToken();
        String expected ="";
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

}
