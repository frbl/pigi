package nl.rug.client.analysis;

import java.io.File;

/**
 * This interface represents the functionality needed to determine complexity of
 * a give file.
 * 
 * @author wesschuitema
 */
public interface ComplexityAnalyzer {
    
    /**
     * This method is called in order to determine the complexity for a certain 
     * file
     * 
     * @param file - The File for which you want to determine the complexity
     * @return An integer value representing the complexity for the file
     */
    public Integer determineComplexity(File file);
    
}
