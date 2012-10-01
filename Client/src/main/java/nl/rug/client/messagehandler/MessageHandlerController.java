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
import nl.rug.client.model.Message;

/**
 *
 * @author Rene
 */
public class MessageHandlerController {
        
    private static BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
    private static final BlockingQueue<Message> talkQueue = new LinkedBlockingQueue<Message>();
    
    private MessageHandler childHandler;
    private MessageHandler leaderHandler;
    private MessageHandler parentHandler;
    
    private Connection parentConnection = null; //When i am not the root, this is my parent
    private Map<String, Connection> children = new HashMap<String, Connection>(); //My children
    
    public MessageHandlerController(){
        childHandler = new ChildHandler();
        parentHandler = new ParentHandler();
        leaderHandler = new LeaderHandler();
        
        //Takes care of incomming messages
        new Thread(handleMessages()).start();
        
        //Takes care of the messages which need to be send
        new Thread(talkToOthers()).start();
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
                        switch(message.getType()){
                            case CHILD: childHandler.handleMessage(message); break;
                            case PARENT: parentHandler.handleMessage(message); break;
                            case LEADER: leaderHandler.handleMessage(message); break;
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
    }
    
    private Runnable talkToOthers(){
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
