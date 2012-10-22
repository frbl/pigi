/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.events.EndDocument;
import nl.rug.client.model.*;
import nl.rug.client.model.Request.RequestType;

/**
 *
 * @author Rene
 */
public class ChordNode implements IChordNode {
    
    private Map<Address, IChordNode> connections = new HashMap<Address, IChordNode>();
    
    private Address myAddress;
    
    private IChordNode predecessor;
    private IChordNode successor;
    
    private List<Address> fingers = new LinkedList<Address>();
        
    public ChordNode(int port) {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        myAddress = new Address(ip, port);
        
        addConnection(this);
        
        scheduleTasks();
    }
    
    private void scheduleTasks(){
        Timer timer = new Timer();
        timer.schedule(scheduleFixFingers(), 0, 10000);
    }
    
    private TimerTask scheduleFixFingers(){
        return new TimerTask() {
            @Override
            public void run() {
                checkPredecessor();
                stabalize();
                fixFingers();
            }
        };
    }
    
    public void addConnection(IChordNode connection){
        connections.put(connection.getAddress(), connection);
    }
    
    public IChordNode getConnection(Address address){
        if(connections.containsKey(address)){
            return connections.get(address);
        } else {
            IChordNode connection = null;
            try {
                connection = new ChordConnection(address);
            } catch (IOException ex) {
                Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            connections.put(address, connection);
            return connection;
        }
    }
    
    public void create(){
        predecessor = null;
        successor = this;
    }
    
    @Override
    public Address findSuccessor(String id){        
        if(Util.isBetween(id, myAddress.getHash(), successor.getAddress().getHash())){
            return successor.getAddress();
        } else {
            //n0 = closestPrecedingNode(id)
            //Request cpnr = new Request(successor.getAddress(), myAddress, Request.RequestType.CPN);
            Address n0Address = closestPrecedingNode(id);
            IChordNode cpn = getConnection(n0Address);
            return cpn.findSuccessor(id);
        }
    }

    @Override
    public Address closestPrecedingNode(String id){
        Iterator<Address> it = fingers.iterator();
        while(it.hasNext()){
            Address finger = it.next();
            if(finger.isBetween(myAddress.getHash(), id)){
                return finger;
            }
        }
        return this.getAddress();
    }
    
    @Override
    public void join(Address address){
        predecessor = null;
        IChordNode node = getConnection(address);
        successor = getConnection(node.findSuccessor(this.getAddress().getHash()));
    }
    
    @Override
    public void stabalize(){
        Address x = successor.getPredecessor();
        
        if(x != null && x.isBetween(this.getAddress().getHash(), successor.getAddress().getHash())){
            successor = getConnection(x);
        }
        
        successor.notify(this.getAddress());
    }
    
    @Override
    public void notify(Address address){
        if(predecessor == null || address.isBetween(predecessor.getAddress().getHash(), this.getAddress().getHash())){
            predecessor = getConnection(address);
        }
    }
    
    int index = 0;
    
    @Override
    public void fixFingers(){
        BigInteger maxHash = new BigInteger("ffffffffffffffffffffffffffffffffffffffff", 16);
        BigInteger myHash = new BigInteger(myAddress.getHash(), 16);
        
        BigInteger lookupIndex = myHash.add(BigInteger.valueOf((int)Math.pow(2, index)).divide(maxHash));
        if(lookupIndex.compareTo(maxHash) > 0){
            index = 0;
            lookupIndex = lookupIndex.subtract(maxHash);
        }
        
        fingers.add(index, findSuccessor(lookupIndex.toString(16)));
        //Print fingers
        Iterator<Address> it = fingers.iterator();
        System.out.println("------- Fingers -------");
        while(it.hasNext()){
            Address a = it.next();
            System.out.println(a.toString());
        }
        System.out.println("------- End fingers -------");
        index++;
    }
    
    @Override
    public void checkPredecessor(){
        if(predecessor != null && !predecessor.isAlive()){
            predecessor = null;
        }
    }
    
    @Override
    public Address getAddress(){
        return myAddress;
    }

    @Override
    public Address getSuccessor() {
        return successor.getAddress();
    }

    @Override
    public Address getPredecessor() {
        if(predecessor == null){
            return null;
        }
        return predecessor.getAddress();
    }
    
    @Override
    public void ping(){
        System.out.println("Ping not needed here - ChordNode");
    }

    public boolean isAlive() {
        return true;
    }

    public FileComplexity getFileComplexity(String filepath, int revision) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
