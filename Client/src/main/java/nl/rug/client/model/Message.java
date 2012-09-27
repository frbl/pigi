/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.io.Serializable;
import nl.rug.client.controller.Connection.ConnectionType;

/**
 *
 * @author Rene
 */
public class Message implements Serializable {
    
    private String message;
    private String senderAddress;
    private String targetAddress;
    
    private ConnectionType type = null;
    
    public Message(String message){
        this.message = message;
    }
    
    public Message(){
        this("Ik heb nog niet zo veel te vertellen :(");
    }
    
    public String getMessage(){
        return message;
    }
    
    public void setType(ConnectionType type){
        this.type = type;
    }
    
    public ConnectionType getType(){
        return type;
    }
    
    public String getTargetAddress(){
        return targetAddress;
    }
    
    public void setTargetAddress(String address){
        targetAddress = address;
    }
    
    public String getSenderAddress(){
        return senderAddress;
    }
    
    public void setSenderAddress(String address){
        senderAddress = address;
    }
    
}
