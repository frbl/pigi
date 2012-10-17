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
public abstract class Message implements Serializable {
    
    private Address senderAddress;
    private Address targetAddress;
    
    public Message(Address targetAddress, Address senderAddress){
        setTargetAddress(targetAddress);
        setSenderAddress(senderAddress);
    }
    
    public Address getTargetAddress(){
        return targetAddress;
    }
    
    public void setTargetAddress(Address address){
        targetAddress = address;
    }
    
    public Address getSenderAddress(){
        return senderAddress;
    }
    
    public void setSenderAddress(Address address){
        senderAddress = address;
    }
    
}
