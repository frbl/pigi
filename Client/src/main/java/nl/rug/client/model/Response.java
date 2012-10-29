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
public class Response implements Serializable {
    
    public Request request = null;
    public Object object = null;
    
    //public Response(Address target, Address sender, Request request){
    public Response(Request request){
        //super(target, sender);
        this.request = request;
    }
}
