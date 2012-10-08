/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.messagehandler;

import nl.rug.client.model.Message;
import nl.rug.client.repository.MySVNRepository;

/**
 *
 * @author Rene
 */
public class ChildHandler extends MessageHandler {

    MySVNRepository repository;
    
    public void handleMessage(Message message) {
        System.out.println("Child: I GOT A MESSAGE! - " + message.getMessage());
        Message replyMessage = new Message("Child: 'Ik heb het ontvangen!'");
        replyMessage.setTargetAddress(message.getSenderAddress());
        MessageController.talk(replyMessage);
    }
    
}
