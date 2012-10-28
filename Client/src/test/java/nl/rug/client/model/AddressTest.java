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
public class AddressTest extends TestCase {
    Address instance;
    int port;
    String ip;
            
    public AddressTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        ip = "192.168.10.12";
        port = 1234;
        instance = new Address(ip, port);
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getIp method, of class Address.
     */
    public void testGetIp() {
        System.out.println("getIp");
        String result = instance.getIp();
        assertEquals(ip, result);
    }

    /**
     * Test of getPort method, of class Address.
     */
    public void testGetPort() {
        int result = instance.getPort();
        assertEquals(port, result);
    }

    /**
     * Test of getHash method, of class Address.
     */
    public void testGetHash() {
        System.out.println("getHash");
        String expResult = Util.getHash(ip+":"+port);
        String result = instance.getHash();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Address.
     */
    public void testEquals() {
        System.out.println("equals");
        Address address = new Address("123", 123);
        boolean expResult = false;
        boolean result = instance.equals(address);
        assertEquals(expResult, result);
        
        address = new Address(ip, port);
        expResult = true;
        result = instance.equals(address);
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Address.
     */
    public void testToString() {
        System.out.println("toString");
        String expResult = ip+":"+port;
        String result = instance.toString();
        assertEquals(expResult, result);
    }
}
