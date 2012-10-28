/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.controller;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.WorkingSet;
import nl.rug.client.database.Revision;
import nl.rug.client.model.Address;
import nl.rug.client.model.ChordConnection;
import nl.rug.client.model.FileComplexity;

/**
 *
 * @author Rene
 */
public class ClientController {

    //private File database = new File("client.db");
    private boolean running = true;
    private ServerSocket serverSocket; //For new connections
    private static ChordNode node;

    public ClientController(Address localAddress, Address remoteAddress, WorkingSet workingSet) {
        this(localAddress, workingSet);
        //TEST!
        if (remoteAddress != null) {
            node.join(remoteAddress);
        }
    }

    public ClientController(Address localAddress, WorkingSet workingSet) {
        node = new ChordNode(localAddress, workingSet);

        node.create();
        startListeningForChildren(localAddress.getPort());

    }

    public FileComplexity getComplexity(String file, Revision revision) {

        return node.calculateFileComplexity(file, revision.getNumber());

    }

    public static ChordNode getChordNode() {
        return node;
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

    private Runnable waitForConnection() {
        return new Runnable() {
            public void run() {
                while (running) {
                    try {
                        ChordConnection newChild = new ChordConnection(serverSocket.accept());
                        System.out.println("New incoming connection");
                        Thread thread = new Thread(newChild);
                        thread.start();

                        Address remoteAddress = newChild.getAddress();
                        newChild.setAddress(remoteAddress);

                        node.addConnection(remoteAddress, newChild);

                    } catch (IOException e) {
                        System.out.println("Accept failed: " + node.getAddress().getPort());
                    }
                }
            }
        };
    }

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        Address localAddress = new Address(null, 4042);
        
        if(localAddress.getPort() != 4040) {
             try {
                String ip = InetAddress.getLocalHost().getHostAddress();
                Address remoteAddress = new Address(ip, 4040);
                new ClientController(localAddress, remoteAddress, null);
            } catch (UnknownHostException ex) {
                Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, "Cannot determine host (ip address)", ex);

                System.exit(1);
            }
        } else {
            new ClientController(localAddress, null);
        }
    }
}
