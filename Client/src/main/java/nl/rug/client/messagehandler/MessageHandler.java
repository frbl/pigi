/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.messagehandler;

import nl.rug.client.model.Message;

/**
 *
 * @author Rene
 */
public interface MessageHandler {
        
    public void handleMessage(Message message);
}
