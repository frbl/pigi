/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.io.Serializable;

/**
 *
 * @author Rene
 */
public class FileComplexity implements Serializable {
    
    private String hash;
    private String filepath;
    private long revision;
    private int complexity;
    
    public FileComplexity(String filepath, long revision){
        hash = Util.getHash(filepath + revision);
        this.filepath = filepath;
        this.revision = revision;
    }
    
    public void setComplexity(int complexity){
        this.complexity = complexity;
    }
    
    public String getHash(){
        return hash;
    }
    
    public String getFilePath(){
        return filepath;
    }
    
    public long getRevision(){
        return revision;
    }
    
    public int getComplexity(){
        return complexity;
    }
    
}
