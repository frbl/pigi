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
import nl.rug.client.messagehandler.MessageHandlerController;
import nl.rug.client.model.Message;

/**
 *
 * @author Rene
 */
public class Connection implements Runnable {
    
    public static enum ConnectionType { PARENT, CHILD, LEADER }
    
    private Socket socket;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    
    private ConnectionType type;
    private boolean running = true;
        
    public Connection(Socket socket, ConnectionType type){
        this.type = type;
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());         
        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getAddress(){
        return socket.getInetAddress().toString();
    }
    
    public boolean isAlive(){
        return running;
    }
    
    public void talk(Message message){
        try {
            message.setSenderAddress(this.getAddress());
            out.writeObject(message);
            System.out.println("Message send");
        } catch (IOException ex) {
            running = false;
            System.out.println("OWNOO! the socket closed!! Do something!!!!");
            //Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() {
        System.out.println("Child is now listening");
        while(running){
            try {
                Message message = (Message)in.readObject();
                //message.setSenderAddress(this.getAddress());
                message.setType(type);
                System.out.println("Received message");
                //messageQueue.add(ob);
                MessageHandlerController.queueMessage(message);
            } catch(IOException e){
              running = false;
              System.out.println("OWNOO! the socket closed!! Do something!!!!");  
            } catch (Exception ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}