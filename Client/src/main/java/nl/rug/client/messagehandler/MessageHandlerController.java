/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.messagehandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.model.Message;

/**
 *
 * @author Rene
 */
public class MessageHandlerController implements Runnable {
        
    private static BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
    
    private MessageHandler childHandler;
    private MessageHandler leaderHandler;
    private MessageHandler parentHandler;
    
    public MessageHandlerController(){
        childHandler = new ChildHandler();
        parentHandler = new ParentHandler();
        leaderHandler = new LeaderHandler();
    }
    
    public static void queueMessage(Message message) {
        messageQueue.add(message);
    }
    
    public void run(){
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
}
