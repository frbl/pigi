/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author Rene
 */
public class Request extends Message implements Serializable {
    
    public RequestType type;
    public Object object;
    
    public String UID = UUID.randomUUID().toString();
    
    //CPN = Closest Preceding Node
    //FS = Find Successor
    //CP = Check predecessor
    public static enum RequestType {CPN, FS, CP, PING, SUCCESSOR, PREDECESSOR, JOIN, STABALIZE, NOTIFY, FILE }
    
    public Request(Address target, Address sender, RequestType type){
        super(target, sender);
        this.type = type;
    }
}
