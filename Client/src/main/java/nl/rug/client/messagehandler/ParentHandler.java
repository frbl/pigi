/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.messagehandler;

import nl.rug.client.controller.ClientController;
import nl.rug.client.model.Message;

/**
 *
 * @author Rene
 */
public class ParentHandler implements MessageHandler {

    public void handleMessage(Message message) {
        System.out.println("Parent: I GOT A MESSAGE! - " + message.getMessage());
        Message replyMessage = new Message("Parent: 'Ik heb het ontvangen!'");
        replyMessage.setTargetAddress(message.getSenderAddress());
        MessageHandlerController.talk(replyMessage);
    }
    
}
