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
public class Message implements Serializable {
    
    public static enum MessageType {POSITION_REQUEST, CALC_FILE, STRING }
    
    private String message;
    private Address senderAddress;
    private Address targetAddress;
    
    private MessageType messageType;
    
    public Message(String message, MessageType messageType){
        setMessage(message);
        setMessageType(messageType);
    }
    
    public Message(MessageType messageType){
        this("Ik heb nog niet zo veel te vertellen :(", messageType);
    }
    
    public void setMessage(String message){
        this.message = message;
    }
    
    public String getMessage(){
        return message;
    }
    
    public void setMessageType(MessageType type){
        this.messageType = type;
    }
    
    public MessageType getMessageType(){
        return messageType;
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
