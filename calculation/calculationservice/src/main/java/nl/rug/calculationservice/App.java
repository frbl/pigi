package nl.rug.calculationservice;

import nl.rug.calculationservice.hadoop.*;


/**
 * Hello world!
 *
 */
public class App {
    
    
    IHadoop hadoop;
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        
        new App();

    }
    
    
    /**
     *
     */
    public App() {
        
        hadoop = new MockHadoop();
        
        hadoop.performCalculation();
        
    }
    
}
