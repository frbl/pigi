/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.analysis;

import java.io.File;
import javancss.Javancss;

/**
 *
 * @author Rene
 */
public class ComplexityAnalyzer implements Analyzer {
    
    public ComplexityAnalyzer(){
        
    }
            
    public Integer determineComplexity(File file) {
        //File file = new File("C:\\Users\\Rene\\Desktop\\DistributedSystems\\pigi\\Client\\src\\main\\java\\nl\\rug\\client\\controller\\ClientController.java");
        Javancss pJavancss = new Javancss(file);
        return pJavancss.getNcss();
    }

    public int startAnalyzing(String filepath, long revision){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
