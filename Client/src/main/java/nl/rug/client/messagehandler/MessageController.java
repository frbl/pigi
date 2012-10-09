/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.messagehandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.controller.ClientController;
import nl.rug.client.controller.Connection;
import nl.rug.client.model.Address;
import nl.rug.client.model.Message;

/**
 *
 * @author Rene
 */
public class MessageController {
        
    private static BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
    private static final BlockingQueue<Message> talkQueue = new LinkedBlockingQueue<Message>();
    
    private MessageHandler messageHandler;
    
    private Connection parentConnection = null; //When i am not the root, this is my parent
    private Map<Address, Connection> children = new HashMap<Address, Connection>(); //My children
    
    public MessageController(){
        messageHandler = new MessageHandler();
        new Thread(messageHandler).start();
        
        //Takes care of incoming messages
        new Thread(handleMessages()).start();
        
        //Takes care of the messages which need to be send
        new Thread(sendMessages()).start();
    }
    
    public static void queueMessage(Message message) {
        messageQueue.add(message);
    }
    
    public void setParent(Connection parent){
        parentConnection = parent;
    }
    
    public void addChild(Connection child){
        children.put(child.getAddress(), child);
    }
    
    private Runnable handleMessages(){
        return new Runnable(){

            public void run() {
                while(true){
                    try {
                        Message message = messageQueue.take();
                        messageHandler.handleMessage(message);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
    }
    
    private Runnable sendMessages(){
        return new Runnable() {
            public void run() {
                while(true){
                    try {
                        Message message = talkQueue.take();
                        
                        //Find address in children
                        Connection con = children.get(message.getTargetAddress());
                        
                        //If not leader and address is not in children. Check parent
                        if(con == null && parentConnection != null && message.getTargetAddress().equals(parentConnection.getAddress())){
                            con = parentConnection;
                        }
                        
                        if(con != null){
                            con.talk(message);
                        } else {
                            System.out.println("Could not find target - " + message.getTargetAddress());   
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
        };
    }

    public static void talk(Message message){
        talkQueue.add(message);
    }
}
