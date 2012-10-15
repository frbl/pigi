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
public class Address implements Serializable {
    
    public String ip;
    public int port;
    
    public Address(String ip, int port){
        this.ip = ip;
        this.port = port;
    }
    
    public boolean equals(Address address){
        return this.ip.equals(address.ip) && this.port == address.port;
    }
}
