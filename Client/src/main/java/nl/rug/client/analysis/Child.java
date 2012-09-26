/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.analysis;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rene
 */
public class Child implements Runnable {
    
    private Socket socket;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private boolean running = true;
    
    public Child(Socket socket){
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());         
        } catch (Exception ex) {
            Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getAddress(){
        return socket.getInetAddress().toString();
    }
    
    public void run() {
        System.out.println("I am now listening to my child");
        while(running){
            try {
                Object to = in.readObject();
                System.out.println("Received object");
            } catch (IOException ex) {
                Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
