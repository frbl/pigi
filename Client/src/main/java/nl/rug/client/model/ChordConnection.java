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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
    private int timeout = 30; //In seconds
    private Request waitingFor = null;
    
    //REMOVE??
    private BlockingQueue<Request> toRequest = new LinkedBlockingQueue<Request>();
    private boolean addressSet = false;
    //private boolean remoteAddressSet = false;
    private final static Logger logger = Logger.getLogger(ChordConnection.class.getName());

    public ChordConnection(Socket socket) throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());

        out.flush();
        
        in = new ObjectInputStream(socket.getInputStream());
        
        //REMOVE?
        new Thread(processRequests()).start();
    }

    public ChordConnection(Address address) throws IOException {

        this(new Socket(address.getIp(), address.getPort()));

        logger.log(Level.INFO, "Initializing connection for address {0}:{1}, with id {2}", new Object[]{address.getIp(), address.getPort(), address.getHash()});

        setAddress(address);
    }

    public boolean isAddressSet() {
        return addressSet;
    }

    public void setAddress(Address address) {
        myAddress = address;
        addressSet = true;
    }

    public void sendMessage(Object message) {
        if (message == null) {
            System.out.println("Message to send is null, this is likely to cause an error");
        }
        try {
            out.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ChordConnection.class.getName()).log(Level.SEVERE, null, ex);
            kill();
        }
    }

    //REMOVE??
    public Runnable processRequests() {
        return new Runnable() {
            public void run() {
                while (alive) {
                    try {
                        handleRequest(toRequest.take());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ChordConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
    }

    public void handleRequest(Request request) {
        //if(waitingFor != null){
        //    System.out.println("Waiting for type and already getting new request!! STRESSSS! now waiting for:" + waitingFor.type + " request for: " + request.type);
        //}
        ChordNode localNode = ClientController.getChordNode();
        Response response = new Response(request);
        String id = null;
        Address address = null;
        switch (request.type) {
            case ADDRESS:
                response.object = localNode.getAddress();
                sendMessage(response);
                //alive = true; //Connection finished at remote node
                break;
            case CPN:
                id = (String) request.getObject();
                address = localNode.closestPrecedingNode(id);
                response.object = address;
                sendMessage(response);
                break;
            case FS:
                id = (String) request.getObject();
                address = localNode.findSuccessor(id);
                response.object = address;
                sendMessage(response);
                break;
            case CP:
                System.out.println("Not needed. CP at handleRequest - ChordConnection");
                break;
            case PING:
                boolean isAlive = localNode.isAlive();
                response.object = isAlive;
                sendMessage(response);
                break;
            case SUCCESSOR:
                address = localNode.getSuccessor();
                response.object = address;
                sendMessage(response);
                break;
            case PREDECESSOR:
                address = localNode.getPredecessor();
                response.object = address;
                sendMessage(response);
                break;
            case JOIN:
                address = (Address) request.getObject();
                localNode.join(address);
                break;
            case STABALIZE:
                System.out.println("Not needed. STABALIZE at handleRequest - ChordConnection");
                break;
            case NOTIFY:
                address = (Address) request.getObject();
                localNode.notify(address);
                break;
            case FILE:
                FileComplexity file = (FileComplexity) request.getObject();
                response.object = localNode.getFileComplexity(file.getFilePath(), file.getRevision());
                sendMessage(response);
                break;
            case UPDATE:
                FileComplexity fileComplexityUpdate = (FileComplexity) request.getObject();
                localNode.updateComplexity(fileComplexityUpdate);
                break;
        }
    }

    @Override
    public void run() {
        //while(!alive){
        //    try {
        //        Thread.sleep(500); //Wait for address to be synced
        //    } catch (InterruptedException ex) {
        //        Logger.getLogger(ChordConnection.class.getName()).log(Level.SEVERE, null, ex);
        //    }
        //}
        System.out.println("Now listening");
        while (alive) {
            try {

                Object message = in.readObject();
                //System.out.println(mes + " - " + myAddress);
                //Message message = (Message)mes;
                if (message instanceof Request) {
                    Request request = (Request) message;
                    //System.out.println("Request received " + request.type);
                    //handleRequest(request);
                    //REMOVE??
                    toRequest.add(request);
                } else if (message instanceof Response) {
                    Response response = (Response) message;
                    //System.out.println("Response received " + response.request.type);
                    responses.put(response.request.UID, response);
                }
            } catch (IOException e) {
                System.out.println("OWNOO! the socket closed!! Do something!!!!");
                //Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, e);
                kill();
            } catch (Exception ex) {
                Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public Address getAddress() {
        if (addressSet) {
            return myAddress;
        }

        Request request = new Request(RequestType.ADDRESS);
        sendMessage(request);
        Response response = waitForObject(request);
        if (response == null) {
            System.out.println("Address is null :o");
            return null;
        }
        return (Address) response.object;
    }

    @Override
    public Address getSuccessor() {
        Request request = new Request(RequestType.SUCCESSOR);
        sendMessage(request);
        Response response = waitForObject(request);
        if (response == null) {
            return null;
        }
        return (Address) response.object;
    }

    @Override
    public Address getPredecessor() {
        Request request = new Request(RequestType.PREDECESSOR);
        sendMessage(request);
        Response response = waitForObject(request);
        if (response == null) {
            return null;
        }
        return (Address) response.object;
    }

    @Override
    public Address findSuccessor(String id) {
        Request request = new Request(RequestType.FS);
        request.setObject(id);
        sendMessage(request);
        Response response = waitForObject(request);
        if (response == null) {
            return null;
        }
        return (Address) response.object;
    }

    @Override
    public Address closestPrecedingNode(String id) {
        Request request = new Request(RequestType.CPN);
        request.setObject(id);
        sendMessage(request);
        Response response = waitForObject(request);
        return (Address) response.object;
    }

    @Override
    public void join(Address node) {
        Request request = new Request(RequestType.JOIN);
        request.setObject(node);
        sendMessage(request);
    }

    @Override
    public void stabalize() {
        System.out.println("Implements stabalize in ChordConnection. It's needed after all");
    }

    @Override
    public void notify(Address node) {
        Request request = new Request(RequestType.NOTIFY);
        request.setObject(node);
        sendMessage(request);
    }

    @Override
    public void fixFingers() {
        System.out.println("Implements fixFingers in ChordConnection. It's needed after all");
    }

    @Override
    public void checkPredecessor() {
        System.out.println("Implements checkPredecessor in ChordConnection. It's needed after all");
    }

    public Response waitForObject(Request request) {
        waitingFor = request;
        long start = System.currentTimeMillis();
        try {
            while (!responses.containsKey(request.UID) && alive) {
                if (System.currentTimeMillis() > start + timeout * 1000) {
                    kill();
                    System.out.println("1TIME OUT!!!!!!");
                    System.out.println("2TIME OUT!!!!!!");
                    System.out.println("3TIME OUT!!!!!!");
                    System.out.println("4TIME OUT!!!!!!");
                    System.out.println("requesttype: " + request.type + " @ " + myAddress);
                    break;
                } else {
                    Thread.sleep(500);
                    //System.out.println("Waiting for response for " + request.type + " @ " + myAddress);
                }
            }
            //System.out.println("Received response for " + request.type);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChordConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        waitingFor = null;
        return responses.remove(request.UID);
    }

    @Override
    public FileComplexity getFileComplexity(String filepath, long revision) {
        Request request = new Request(RequestType.FILE);
        request.setObject(new FileComplexity(filepath, revision));
        sendMessage(request);
        Response response = waitForObject(request);
        if (response == null) {
            return null;
        }
        return (FileComplexity) response.object;
    }

    @Override
    public void ping() {
        Request request = new Request(RequestType.PING);
        sendMessage(request);
    }

    @Override
    public void kill() {
        alive = false;
        ClientController.getChordNode().removeConnection(this);
        System.out.println("Connection killed");
        if (waitingFor != null) {
            System.out.println("Im killed, but i was still waiting on something. " + myAddress);
        }
    }

    @Override
    public void updateComplexity(FileComplexity fileComplexity) {
        Request request = new Request(RequestType.UPDATE);
        request.setObject(fileComplexity);
        sendMessage(request);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
