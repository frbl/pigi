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
public class Response extends Message implements Serializable {
    
    public Request request;
    public Object object;
    
    public Response(Address target, Address sender, Request request){
        super(target, sender);
        this.request = request;
    }
}
