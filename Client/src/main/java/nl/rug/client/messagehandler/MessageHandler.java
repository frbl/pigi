/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.messagehandler;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.model.Message;

/**
 *
 * @author Rene
 */
public abstract class MessageHandler implements Runnable {
    
    protected BlockingDeque<Message> messageQueue = new LinkedBlockingDeque<Message>();
    public abstract void handleMessage(Message message);

    public void run(){
        while(true){
            try {
                handleMessage(messageQueue.take());
            } catch (InterruptedException ex) {
                Logger.getLogger(ChildHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
