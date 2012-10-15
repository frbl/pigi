/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.controller;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.messagehandler.*;
import nl.rug.client.model.Address;
import nl.rug.client.model.Request;
import nl.rug.client.model.Request.RequestType;

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
        
    private int myPort = 4040;
    
    private MessageController messageHandlerController;
    
    public ClientController(){
        messageHandlerController = new MessageController();
        
        getPosition();

        //TESTESTTESTEST
        //Message testMessage = new Message("HALLO!!");
        //testMessage.setTargetAddress(parentAddress);
        //MessageController.talk(testMessage);
              
    }
    
    private void getPosition(){
        /*
            Address leader = Server.getLeader();
        */
        
        try {
            Address leaderAddress = new Address("127.0.0.1", 4040);
            Address myAddress = new Address(InetAddress.getLocalHost().getHostAddress(), myPort);
            Connection con = new Connection(new Socket(leaderAddress.ip, leaderAddress.port));
            con.talk(new Request(leaderAddress, myAddress,RequestType.POSITION));
        
            boolean leader = false;
            leader = leaderAddress.ip.equals(myAddress.ip);
        
        
            if(!leader){
                //Address parentAddress = LeaderConnection.requestPosition();
                Address parentAddress = new Address("127.0.0.1", 4040);
                boolean started = startClient(parentAddress);
            }
            startListeningForChildren();     
        } catch (IOException e1) {
            //Something went wrong while connecting to leader
            //Leader is dead? lets assume. Then now set myself as leader
            //leaderIp = Server.setLeader(leaderIp, myIp); //will return the new leader ip (But might not be myIp)
        } catch (UnknownHostException e2) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, e2);
        }
    }
    
    //Connect to my parent
    private boolean startClient(Address address){
        try {
            messageHandlerController.setParent(new Connection(new Socket(address.ip, address.port)));
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    private void startListeningForChildren() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(myPort));
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
                        Connection newChild = new Connection(serverSocket.accept());
                        //children.put(newChild.getAddress(), newChild);
                        messageHandlerController.addChild(newChild);
                        Thread thread = new Thread(newChild);
                        thread.start();
                        
                        System.out.println("New child!");
                    }
                } catch (IOException e) {
                    System.out.println("Accept failed: " + myPort);
                }
            }
        };
    }
    
    public static void main(String args[]){
        new ClientController();
    }
}
