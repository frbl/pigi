/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.analysis;

import junit.framework.TestCase;
import nl.rug.client.model.FileComplexity;

/**
 *
 * @author frbl
 */
public class ComplexityAnalyzerTest extends TestCase {
    
    public ComplexityAnalyzerTest(String testName) {
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
     * Test of startAnalyzing method, of class ComplexityAnalyzer.
     */
    public void testStartAnalyzing() {
        System.out.println("startAnalyzing");
        FileComplexity fileComplexity = null;
        ComplexityAnalyzer instance = new ComplexityAnalyzer(null);
        int expResult = -1;
        int result = instance.startAnalyzing(fileComplexity);
        assertEquals(expResult, result);
        
    }
}
