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
    private int revision;
    private double complexity;
    
    public FileComplexity(String filepath, int revision){
        hash = Util.getHash(filepath + ":" + revision);
        this.filepath = filepath;
        this.revision = revision;
    }
    
    public void setComplexity(double complexity){
        this.complexity = complexity;
    }
    
    public String getHash(){
        return hash;
    }
    
    public String getFilePath(){
        return filepath;
    }
    
    public int getRevision(){
        return revision;
    }
    
    public double getComplexity(){
        return complexity;
    }
    
}
