package nl.rug.client.model;

import java.util.List;

/**
 * Model representing a repository - a model in the MVC sense that is used as a 
 * proxy to the repository information that is needed to display information.
 * 
 * @author wesschuitema
 */
public interface Repository {
    
    /**
     * This method can be used to determine which files are present in the 
     * repository
     * 
     * @return A list containing all of the paths to the files in a repository 
     *  (e.g., svn://repo.example.nl/dir/file). This list is used to create an 
     *  overview of files present in a repository    
     */
    public List<String> filePaths();
    
    /**
     * This method is used to determine which revisions exist for a file that is
     * present in the repository
     * 
     * @param filePath - the path for the file that you want to get the 
     *  revisions for; this would be in the form of: "svn://rep.nl/dir/file"
     * @return A list of integers corresponding to the existing revisions of a 
     *  file
     */
    public List<Integer> fileRevisions(String filePath);
    
    /**
     * This method is used to retrieve the complexity for a certain file at a 
     * certain revision.
     * 
     * @param filePath - the path for the file that you want to get the 
     *  complexity for; this would be in the form of: "svn://rep.nl/dir/file"
     * @param revision - the revision of the file for which you want to know the
     *  complexity
     * @return An Integer representing the complexity of the file at the 
     *  requested revision
     */
    public Integer complexity(String filePath, Integer revision);
    
}
