/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.analysis;

import com.trilead.ssh2.log.Logger;
import java.io.File;
import java.util.logging.Level;
import javancss.Javancss;
import nl.rug.client.App;
import nl.rug.client.RepositoryModel;
import nl.rug.client.model.FileComplexity;
import nl.rug.client.repository.MySVNRepository;
import nl.rug.client.repository.Repository;

/**
 *
 * @author Rene
 */
public class ComplexityAnalyzer implements Analyzer {

    private final String DIRECTORY = "svnfiles";
    
    private Repository repository;
    
    private final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ComplexityAnalyzer.class.getName());

    public ComplexityAnalyzer(RepositoryModel repositoryModel) {

        this.repository = new MySVNRepository(repositoryModel.getAddress(),
                                                "",
                                                repositoryModel.getUsername(),
                                                repositoryModel.getPassword());

    }

    private Integer determineComplexity(File file) {
        
        Javancss pJavancss = new Javancss(file);
        
        return pJavancss.getNcss();
        
    }

    @Override
    public int startAnalyzing(FileComplexity fileComplexity) {
        
        final String FILE_SEPARATOR = System.getProperty("file.separator");
        
        repository.retrieveFile(fileComplexity.getFilePath(), fileComplexity.getRevision());
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(DIRECTORY);

        sb.append(FILE_SEPARATOR);
        
        //TODO This is now hardcoded both here and in MySvnrepository, update it.
        sb.append("TEST");
        
        sb.append(FILE_SEPARATOR);
        
        sb.append(fileComplexity.getRevision());
        
        sb.append(fileComplexity.getFilePath());
        
        File file = new File(sb.toString());
        
        int complexity = determineComplexity(file);
        
        logger.log(Level.INFO, "Complexity calculated for {0} is {1} "
                    , new Object[]{file.getAbsolutePath(), complexity});
        
        return complexity;
    }
}
