/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.repository;

/**
 *
 * @author Rene
 */
public interface Repository {
        
    public String retrieveDirectoryWithFiles(String path, long revision);
    
    public String retrieveFile(String path, long revision);
    
}
