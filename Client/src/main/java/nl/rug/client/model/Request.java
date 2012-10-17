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
public class Request extends Message implements Serializable {
    
    public RequestType type;
    
    //POSITION = To leader: What should be my position in the tree
    //FILE_COMPLEXITY: From leader: What is the complexity of this file
    //ROOT: What is the current root of your local tree (When things crash/leader election)
    public static enum RequestType {POSITION, FILE_COMPLEXITY, ROOT, LEADER_ELECTION }
    
    public Request(Address target, Address sender, RequestType type){
        super(target, sender);
        this.type = type;
    }
}
