package nl.rug.client.analysis;

import java.io.File;
import nl.rug.client.model.FileComplexity;

/**
 * This interface represents the functionality needed to determine complexity of
 * a give file.
 * 
 * @author wesschuitema
 */
public interface Analyzer {
    
    /**
     * This method is called in order to determine the complexity for a certain 
     * file
     * 
     * @param fileComplexity - The File and revision for which you want to 
     *  determine the complexity
     * 
     * @return The comlexity of the file/revision
     */
    public int startAnalyzing(FileComplexity fileComplexity);
    
}
