/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.controller.ChordNode;
import nl.rug.client.controller.ClientController;

/**
 *
 * @author Rene
 */
public class ChordConnection implements IChordNode, Runnable {
    
    private Address myAddress;
    
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    
    private Map<String, Response> responses = new HashMap<String, Response>();
    
    private boolean alive = true;
    private int timeout = 10; //In seconds
    
    public ChordConnection(Socket socket) throws IOException {
        myAddress = new Address(socket.getInetAddress().getHostAddress(), socket.getPort());
        out = new ObjectOutputStream(socket.getOutputStream());        
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    public ChordConnection(Address address) throws IOException {
        this(new Socket(address.getIp(), address.getPort()));
    }
    
    public void sendMessage(Message message){
        try {
            out.writeObject(message);
            //System.out.println("Message send to " + myAddress.toString());
        } catch (IOException ex) {
            System.out.println("OWNOO! the socket closed!! Do something!!!!");
            alive = false;
        }
    }
    
    public void handleRequest(Request request){
        IChordNode node = ClientController.getChordNode();
        Response response = new Response(myAddress, node.getAddress(), request);
        String id = null;
        Address address = null;
        switch(request.type){
            case CPN:
                id = (String)request.object;
                address = ClientController.getChordNode().closestPrecedingNode(id);
                response.object = address;
                sendMessage(response);
                break;
            case FS:
                id = (String)request.object;
                address = ClientController.getChordNode().findSuccessor(id);
                response.object = address;
                sendMessage(response);
                break;
            case CP:
                System.out.println("Not needed. CP at handleRequest - ChordConnection");
                break;
            case PING:
                boolean alive = ClientController.getChordNode().isAlive();
                response.object = alive;
                sendMessage(response);
                break;
            case SUCCESSOR:
                address = ClientController.getChordNode().getSuccessor();
                response.object = address;
                sendMessage(response);
                break;
            case PREDECESSOR:
                address = ClientController.getChordNode().getPredecessor();
                response.object = address;
                sendMessage(response);
                break;
            case JOIN:
                address = (Address)request.object;
                ClientController.getChordNode().join(address);
                break;
            case STABALIZE:
                System.out.println("Not needed. STABALIZE at handleRequest - ChordConnection");
                break;
            case NOTIFY:
                System.out.println("Recieved notify!!!!!!");
                address = (Address)request.object;
                ClientController.getChordNode().notify(address);
                break;
            case FILE:
                FileComplexity file = (FileComplexity)request.object;
                file = ClientController.getChordNode().getFileComplexity(file.getFilePath(), file.getRevision());
                response.object = file;
                sendMessage(response);
                break;
        }
    }
    
    public void run() {
        System.out.println("Now listening");
        while(alive){
            try {
                Message message = (Message)in.readObject();
                //System.out.println("Message received");
                if(message instanceof Request){
                    //System.out.println("Message is an request");
                    Request request = (Request)message;
                    handleRequest(request);
                } else if(message instanceof Response){
                    //System.out.println("Message is an response");
                    Response response = (Response)message;
                    responses.put(response.request.UID, response);
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
        sendMessage(request);
        Response response = waitForObject(request);
        if(response == null) return null;
        return (Address)response.object;
    }
    
    public Address getPredecessor(){
        Request request = new Request(myAddress, myAddress, Request.RequestType.PREDECESSOR);
        sendMessage(request);
        Response response = waitForObject(request);
        if(response == null) return null;
        return (Address)response.object;
    }

    public Address findSuccessor(String id) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.FS);
        request.object = id;
        sendMessage(request);
        Response response = waitForObject(request);
        if(response == null) return null;
        return (Address)response.object;
    }

    public Address closestPrecedingNode(String id) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.CPN);
        request.object = id;
        sendMessage(request);
        Response response = waitForObject(request);
        return (Address)response.object;
    }

    public void join(Address node) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.JOIN);
        request.object = node;
        sendMessage(request);
    }

    public void stabalize() {
        System.out.println("Implements stabalize in ChordConnection. It's needed after all");
    }

    public void notify(Address node) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.NOTIFY);
        request.object = node;
        sendMessage(request);
    }

    public void fixFingers() {
        System.out.println("Implements fixFingers in ChordConnection. It's needed after all");
    }

    public void checkPredecessor() {
        System.out.println("Implements checkPredecessor in ChordConnection. It's needed after all");
    }
    
    public Response waitForObject(Request request){
        long start = System.currentTimeMillis();
        try {
            while(!responses.containsKey(request.UID)) {
                if(System.currentTimeMillis() > start + timeout * 1000){
                    alive = false;
                    System.out.println("1TIME OUT!!!!!!");
                    System.out.println("2TIME OUT!!!!!!");
                    System.out.println("3TIME OUT!!!!!!");
                    System.out.println("4TIME OUT!!!!!!");
                    break;
                } else {
                    Thread.sleep(500);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ChordConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return responses.remove(request.UID);
    }

    public FileComplexity getFileComplexity(String filepath, int revision) {
        Request request = new Request(myAddress, myAddress, Request.RequestType.FILE);
        request.object = new FileComplexity(filepath, revision);
        sendMessage(request);
        Response response = waitForObject(request);
        if(response == null) return null;
        return (FileComplexity)response.object;
    }
    
    public void ping(){
        Request request = new Request(myAddress, myAddress, Request.RequestType.PING);
        sendMessage(request);
    }

    public boolean isAlive() {
        return alive;
    }
}
