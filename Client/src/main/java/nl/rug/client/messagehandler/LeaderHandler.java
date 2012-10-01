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
public class LeaderHandler implements MessageHandler {

    public void handleMessage(Message message) {
        System.out.println("Leader: I GOT A MESSAGE! - " + message.getMessage());
        Message replyMessage = new Message("Leader: 'Ik heb het ontvangen!'");
        replyMessage.setTargetAddress(message.getSenderAddress());
        ClientController.talk(replyMessage);
    }
    
}
