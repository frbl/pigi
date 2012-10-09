/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

/**
 *
 * @author Rene
 */
public class Address {
    
    public String ip;
    public int port;
    
    public boolean equals(Address address){
        return this.ip.equals(address.ip) && this.port == address.port;
    }
}
