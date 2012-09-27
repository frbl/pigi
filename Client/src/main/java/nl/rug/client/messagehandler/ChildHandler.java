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
public class ChildHandler implements MessageHandler {

    public void handleMessage(Message message) {
        System.out.println("Child: I GOT A MESSAGE! - " + message.getMessage());
        ClientController.talk(new Message("Child: 'Ik heb het ontvangen!'"));
    }
    
}
