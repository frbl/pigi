/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.controller.ChordNode;

/**
 *
 * @author Rene
 */
public class ChordConnection implements IChordNode, Runnable {
    
    private Address myAddress;
    
    //private Address successor;
    //private Address predecessor;
    
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    
    private Map<Request, Response> responses = new HashMap<Request, Response>();
    
    private boolean alive = true;
    
    public ChordConnection(Socket socket) throws IOException {
        myAddress = new Address(socket.getInetAddress().getHostAddress(), socket.getPort());
        out = new ObjectOutputStream(socket.getOutputStream());        
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    public ChordConnection(Address address) throws IOException {
        this(new Socket(address.getIp(), address.getPort()));
    }
    
    public void request(Message message){
        try {
            //message.setSenderAddress(this.getAddress());
            out.writeObject(message);
            System.out.println("Message send to " + myAddress.toString());
        } catch (IOException ex) {
            System.out.println("OWNOO! the socket closed!! Do something!!!!");
            alive = false;
        }
    }
    
    public void run() {
        System.out.println("Now listening");
        while(alive){
            try {
                Message message = (Message)in.readObject();
                if(message instanceof Request){
                    Request request = (Request)message;
                    //TODO send response
                } else if(message instanceof Response){
                    Response response = (Response)message;
                    responses.put(response.request, response);
                }
                
                //MessageController.queueMessage(message);
            } catch(IOException e){
                System.out.println("OWNOO! the socket closed!! Do something!!!!");
                alive = false;
            } catch (Exception ex) {
                Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Address getAddress(){
        return myAddress;
    }
    
    public Address getSuccessor(){
        Request request = new Request(myAddress, myAddress, Request.RequestType.SUCCESSOR);
        request(request);
        waitForObject(request);
        return (Address)responses.get(request).object;
    }
    
    public Address getPredecessor(){
        Request request = new Request(myAddress, myAddress, Request.RequestType.PREDECESSOR);
        request(request);
        waitForObject(request);
        return (Address)responses.get(request).object;
    }

    public Address findSuccessor(String id) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.FS);
        request(request);
        waitForObject(request);
        return (Address)responses.get(request).object;
    }

    public Address closestPrecedingNode(String id) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.CPN);
        request(request);
        waitForObject(request);
        return (Address)responses.get(request).object;
    }

    public void join(Address node) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.JOIN);
        request(request);
    }

    public void stabalize() {
        System.out.println("Implements stabalize in ChordConnection. It's needed after all");
    }

    public void notify(Address node) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.NOTIFY);
        request(request);
    }

    public void fixFingers() {
        System.out.println("Implements fixFingers in ChordConnection. It's needed after all");
    }

    public void checkPredecessor() {
        System.out.println("Implements checkPredecessor in ChordConnection. It's needed after all");
    }
    
    public void waitForObject(Request request){
        try {
            while(!responses.containsKey(request)) Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChordConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FileComplexity getFileComplexity(String filepath, int revision) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.FILE);
        request(request);
        waitForObject(request);
        return (FileComplexity)responses.get(request).object;
    }
    
    public void ping(){
        Request request = new Request(myAddress, myAddress, Request.RequestType.PING);
        request(request);
    }

    public boolean isAlive() {
        return isAlive();
    }
}
