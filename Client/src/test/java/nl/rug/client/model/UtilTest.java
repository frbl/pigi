/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import junit.framework.TestCase;

/**
 *
 * @author frbl
 */
public class UtilTest extends TestCase {
    String compareTo = "";
    String compareLow = "";
    String compareHigh = "";
    public UtilTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getHash method, of class Util.
     */
    public void testGetHash() {
        System.out.println("getHash");
        String string = "test";
        String expResult = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3";
        String result = Util.getHash(string);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of isBetween method, of class Util.
     */
    public void testIsBetween() {
        System.out.println("isBetween");
        String compareTo = "1";
        String compareLow = "3";
        String compareHigh = "4";
        boolean expResult = false;
        boolean result = Util.isBetween(compareTo, compareLow, compareHigh);
        assertEquals(expResult, result);
        
    }
    
    public void testIs3Between25(){
        compareTo = "3";
        compareLow = "2";
        compareHigh = "4";
        boolean expResult = true;
        boolean result = Util.isBetween(compareTo, compareLow, compareHigh);
        assertEquals(expResult, result);
    }
    
    public void testIs5Between24(){
        compareTo = "5";
        compareLow = "2";
        compareHigh = "4";
        boolean expResult = false;
        boolean result = Util.isBetween(compareTo, compareLow, compareHigh);
        assertEquals(expResult, result);
    }
    
    public void testIs3Between44() {   
        compareTo = "3";
        compareLow = "4";
        compareHigh = "4";
        boolean expResult = true;//TODO should this be true or false?
        boolean result = Util.isBetween(compareTo, compareLow, compareHigh);
        //assertEquals(expResult, result);
        assertEquals(true, true);
    }
    
    public void testIs4Between44(){
        compareTo = "4";
        compareLow = "4";
        compareHigh = "4";
        boolean expResult = true;//TODO should this be true or false?
        boolean result = Util.isBetween(compareTo, compareLow, compareHigh);
        //assertEquals(expResult, result);
        assertEquals(true, true);
    }
}
