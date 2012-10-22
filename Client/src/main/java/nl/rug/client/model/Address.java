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
    
    private String ip;
    private int port;
    
    private String hash;
    
    public Address(String ip, int port){
        this.ip = ip;
        this.port = port;
        setHash(ip, port);
    }
    
    private void setHash(String ip, int port) {
        hash = Util.getHash(ip + ":" + port);
    }
    
    public String getIp(){
        return ip;
    }
    
    public int getPort(){
        return port;
    }
    
    public String getHash(){
        return hash;
    }
    
    public boolean equals(Address address){
        return this.ip.equals(address.ip) && this.port == address.port;
    }
    
    @Override
    public String toString(){
        return ip + ":" + port;
    }
    
    public boolean isBetween(String compareLeft, String compareRight){
        return hash.compareTo(compareLeft) >= 0 && hash.compareTo(compareRight) < 0;
    }
}
