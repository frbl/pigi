/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.controller;

import nl.rug.client.messagehandler.MessageHandler;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.controller.Connection.ConnectionType;
import nl.rug.client.messagehandler.*;
import nl.rug.client.model.Message;

/**
 *
 * @author Rene
 */
public class ClientController {
    
    //private File database = new File("client.db");
    
    private boolean running = true;
    private ServerSocket serverSocket; //For new connections
    //private Connection parentConnection = null; //When i am not the root, this is my parent
    //private Map<String, Connection> children = new HashMap<String, Connection>(); //My children
    
    private int port = 4040;
    
    private MessageHandlerController messageHandlerController;
    
    public ClientController(boolean leader){
        messageHandlerController = new MessageHandlerController();
        
        //If leader, pass leaderHandler
        startListeningForChildren(leader ? ConnectionType.LEADER : ConnectionType.CHILD);
        if(!leader){
            //Receiving of address of one of the nodes in the cluster
            //TODO Get to now who is the leader, then ask who should be my parent
            String parentAddress = "127.0.0.1";
            startClient(parentAddress);
            
            //TESTESTTESTEST
            Message testMessage = new Message("HALLO!!");
            testMessage.setTargetAddress(parentAddress);
            MessageHandlerController.talk(testMessage);
        }
                
    }
    
    //Connect to my parent
    private void startClient(String host){
        try {
            messageHandlerController.setParent(new Connection(new Socket(host, port), ConnectionType.PARENT));
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void startListeningForChildren(ConnectionType type) {
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));
                    } catch (IOException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Start the message handlers
        new Thread(waitForConnection(type)).start();
        System.out.println("Now waiting for clients!");
    }
    
    private Runnable waitForConnection(final ConnectionType type){
        return new Runnable() {
            public void run() {
                try {
                    while(running){
                        Connection newChild = new Connection(serverSocket.accept(), type);
                        //children.put(newChild.getAddress(), newChild);
                        messageHandlerController.addChild(newChild);
                        Thread thread = new Thread(newChild);
                        thread.start();
                        
                        System.out.println("New child!");
                    }
                } catch (IOException e) {
                    System.out.println("Accept failed: " + port);
                }
            }
        };
    }
    
    public static void main(String args[]){
        new ClientController(false);
    }
}
