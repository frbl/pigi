/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.analysis;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rene
 */
public class ComplexityAnalyzer implements Analyzer {
    
    private File database = new File("client.db");
    
    private boolean running = true;
    private ServerSocket serverSocket; //For new connections
    private Socket parentSocket; //When i am not the root, this is my parent
    private List<Child> children = new ArrayList<Child>(); //My children
    
    public ComplexityAnalyzer(boolean root){
        try {
            startListeningForChildren();
            if(!root)startClient("localhost");
        } catch (Exception ex) {
            Logger.getLogger(ComplexityAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void startClient(String host) throws UnknownHostException, IOException{
        parentSocket = new Socket(host, 4040);
    }

    private void startListeningForChildren() throws IOException{
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(4040));
        new Thread(waitForConnection()).start();
        System.out.println("Now waiting for clients!");
    }
    
    private Runnable waitForConnection(){
        return new Runnable() {
            public void run() {
                try {
                    while(running){
                        Child newChild = new Child(serverSocket.accept());
                        children.add(newChild);
                        
                        Thread thread = new Thread(newChild);
                        thread.start();
                        
                        System.out.println("New child! I now have the following children: ");
                        Iterator<Child> ite = children.iterator();
                        while(ite.hasNext()){
                            System.out.println(ite.next().getAddress());
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Accept failed: 1337");
                }
            }
        };
    }
    
    public Integer determineComplexity(File file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void startAnalyzing(String filepath, int revision){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void main(String args[]){
        new ComplexityAnalyzer(true);
    }
}
