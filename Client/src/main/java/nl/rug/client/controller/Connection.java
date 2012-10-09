/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.controller;

import nl.rug.client.messagehandler.MessageHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.messagehandler.MessageController;
import nl.rug.client.model.Address;
import nl.rug.client.model.Message;

/**
 *
 * @author Rene
 */
public class Connection implements Runnable {
    
    private Address address;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
        
    private boolean running = true;
        
    public Connection(Socket socket) throws IOException{
        address = new Address();
        address.ip = socket.getInetAddress().getHostAddress();
        address.port = socket.getPort();
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public Address getAddress(){
        return address;
    }
    
    public boolean isAlive(){
        return running;
    }
    
    public void kill(){
        running = false;
    }
    
    public void talk(Message message){
        try {
            message.setSenderAddress(this.getAddress());
            out.writeObject(message);
            System.out.println("Message send to " + this.getAddress());
        } catch (IOException ex) {
            running = false;
            System.out.println("OWNOO! the socket closed!! Do something!!!!");
        }
    }
    
    public void run() {
        System.out.println("Now listening");
        while(running){
            try {
                Message message = (Message)in.readObject();
                System.out.println("Received message");
                MessageController.queueMessage(message);
            } catch(IOException e){
              running = false;
              System.out.println("OWNOO! the socket closed!! Do something!!!!");  
            } catch (Exception ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
