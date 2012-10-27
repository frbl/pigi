/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.util.List;
import nl.rug.client.database.ChangedPath;

/**
 *
 * @author Rene
 */
public interface IChordNode {
    
    public Address getAddress();
    public Address getSuccessor();
    public Address getPredecessor();
    
    public Address findSuccessor(String id);
    public Address closestPrecedingNode(String id);
    public void join(Address node);
    public void stabalize();
    public void notify(Address node);
    public void fixFingers();
    public void checkPredecessor();
    
    public void ping();
    public boolean isAlive();
    
    public Integer findFileComplexity(String filepath, long revision);
    public void updateComplexity(FileComplexity fileComplexity);
}
