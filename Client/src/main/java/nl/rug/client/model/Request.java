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
public class Request implements Serializable {
    
    public RequestType type = null;
    private Object object = null;
    
    public String UID = UUID.randomUUID().toString();
    
    //CPN = Closest Preceding Node
    //FS = Find Successor
    //CP = Check predecessor
    
    //public Request(Address target, Address sender, RequestType type){
    public Request(RequestType type){
        //super(target, sender);
        this.type = type;
    }
    
    public void setObject(Object object){
        if(object == null){
            System.out.println("Object is null @ type: " + type);
        }
        this.object = object;
    }
    
    public Object getObject(){
        return object;
    }
}
