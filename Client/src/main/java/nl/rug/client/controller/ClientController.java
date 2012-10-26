/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.controller;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.analysis.Analyzer;
import nl.rug.client.analysis.ComplexityAnalyzer;
import nl.rug.client.model.Address;
import nl.rug.client.model.ChordConnection;

/**
 *
 * @author Rene
 */
public class ClientController {
    
    //private File database = new File("client.db");
    
    private boolean running = true;
    private ServerSocket serverSocket; //For new connections
    
    private static ChordNode node;
    
    private Analyzer analyzer = new ComplexityAnalyzer();
    
    public ClientController(int port){
        node = new ChordNode(port);
        
        node.create();
        startListeningForChildren(port);
        
        startAnalyzingMyRange();
        
        //TEST!
        if(port != 4040){
            Address address = new Address("192.168.1.4", 4040);
            node.join(address);
        }
    }
    
    public static ChordNode getChordNode(){
        return node;
    }
    
    private void startAnalyzingMyRange(){
        
    }
    
    private void startListeningForChildren(int port) {
        try {
            serverSocket = new ServerSocket(port);
            //serverSocket.setReuseAddress(true);
            //serverSocket.bind(new InetSocketAddress(node.getAddress().getPort()));
        } catch (IOException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Start the message handlers
        new Thread(waitForConnection()).start();
        System.out.println("Now waiting for clients!");
    }
    
    private Runnable waitForConnection(){
        return new Runnable() {
            public void run() {
                try {
                    while(running){
                        ChordConnection newChild = new ChordConnection(serverSocket.accept());
                        
                        node.addConnection(newChild);
                        
                        Thread thread = new Thread(newChild);
                        thread.start();
                        
                        System.out.println("New node!");
                    }
                } catch (IOException e) {
                    System.out.println("Accept failed: " + node.getAddress().getPort());
                }
            }
        };
    }
    
    /**
     * 
     * @param args
     */
    public static void main(String args[]){
        new ClientController(4045);
    }
}
