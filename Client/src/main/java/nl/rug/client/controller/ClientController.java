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
    private Connection parentConnection = null; //When i am not the root, this is my parent
    private Map<String, Connection> children = new HashMap<String, Connection>(); //My children
    
    private int port = 4040;
    
    private MessageHandlerController messageHandlerController;
    
    private static final BlockingQueue<Message> talkQueue = new LinkedBlockingQueue<Message>();
    
    public ClientController(boolean leader){
        messageHandlerController = new MessageHandlerController();
        //Receiving of address of one of the nodes in the cluster
        //Get to now who is the leader, then ask who should be my parent
        String parentAddress = "localhost";
        
        //If leader, pass leaderHandler
        startListeningForChildren(leader ? ConnectionType.LEADER : ConnectionType.CHILD);
        if(!leader)startClient(parentAddress);
        
        //Takes care of the messages which need to be send
        new Thread(talkToOthers()).start();
        
        
        //TESTESTTESTEST
        //Message testMessage = new Message("HALLO!!");
        //testMessage.setTargetAddress("localhost");
        //talk(testMessage);
    }
    
    //Connect to my parent
    private void startClient(String host){
        try {
            parentConnection = new Connection(new Socket(host, port), ConnectionType.PARENT);
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
        
        //Start the handling of incoming messages
        new Thread(messageHandlerController).start();
    }
    
    private Runnable waitForConnection(final ConnectionType type){
        return new Runnable() {
            public void run() {
                try {
                    while(running){
                        Connection newChild = new Connection(serverSocket.accept(), type);
                        children.put(newChild.getAddress(), newChild);
                        
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
    
    private Runnable talkToOthers(){
        return new Runnable() {
            public void run() {
                while(running){
                    try {
                        Message message = talkQueue.take();
                        
                        //THIS IS NOT WORKING YET!!
                        Connection con = children.get(message.getTargetAddress());
                        
                        //if(con == null && message.getTargetAddress().equals(parentConnection.getAddress())){
                            con = parentConnection;
                        //}
                        
                        con.talk(message);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
        };
    }

    public static void talk(Message message){
        talkQueue.add(message);
    }
    
    public static void main(String args[]){
        new ClientController(true);
    }
}
