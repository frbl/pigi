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
public abstract class Response extends Message implements Serializable {
    
    public Request request;
    
    public Response(Address target, Address sender, Request request){
        super(target, sender);
        this.request = request;
    }
}
