/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.model.*;

/**
 *
 * @author Rene
 */
public class ChordNode implements IChordNode {
    
    private Map<String, IChordNode> connections = new HashMap<String, IChordNode>();
    
    private Address myAddress;
    
    private IChordNode predecessor;
    private IChordNode successor;
    
    private Map<Integer, Address> fingers = new HashMap<Integer, Address>();
    
    private int fingerIndex = 1;
    
    private BigInteger myHash;
    private BigInteger maxHash = new BigInteger("ffffffffffffffffffffffffffffffffffffffff", 16);
    private BigInteger onePartHash = maxHash.divide(BigInteger.valueOf(160));
    //private BigDecimal one = new BigDecimal(1);
    
    public ChordNode(int port) {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        myAddress = new Address(ip, port);
        myHash = new BigInteger(myAddress.getHash(), 16);
        addConnection(this);
        
        scheduleTasks();
    }
    
    private void scheduleTasks(){
        Timer timer = new Timer();
        timer.schedule(scheduleFixFingers(), 1000, 100);
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
        connections.put(connection.getAddress().getHash(), connection);
    }
    
    public IChordNode getConnection(Address address){
        if(connections.containsKey(address.getHash())){
            return connections.get(address.getHash());
        } else {
            ChordConnection connection = null;
            try {
                connection = new ChordConnection(address);
                Thread thread = new Thread(connection);
                thread.start();
            } catch (IOException ex) {
                Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            connections.put(address.getHash(), connection);
            return connection;
        }
    }
    
    public void create(){
        predecessor = null;
        successor = this;
    }
    
    @Override
    public Address findSuccessor(String id){ 
        if(Util.isBetween(id, myAddress.getHash(), successor.getAddress().getHash()) || myAddress.equals(successor.getAddress())){
            return successor.getAddress();
        } else {
            Address n0Address = closestPrecedingNode(id);
            IChordNode cpn = getConnection(n0Address);
            return cpn.findSuccessor(id);
        }
    }

    @Override
    public Address closestPrecedingNode(String id){
        Collection<Address> fingerCol = fingers.values();
        Iterator<Address> it = fingerCol.iterator();
        while(it.hasNext()){
            Address finger = it.next();
            if(Util.isBetween(finger.getHash(), myAddress.getHash(), id)) {
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
        System.out.println(myAddress + ". Successor is: " + successor.getAddress());
    }
    
    @Override
    public void stabalize(){
        Address x = successor.getPredecessor();
        
        if(x != null && Util.isBetween(x.getHash(), this.getAddress().getHash(), successor.getAddress().getHash())){
            successor = getConnection(x);
        }
        
        successor.notify(myAddress);
        System.out.println(myAddress + ". Successor is: " + successor.getAddress());
    }
    
    @Override
    public void notify(Address address){
        if(predecessor == null || Util.isBetween(address.getHash(), predecessor.getAddress().getHash(), this.getAddress().getHash())){
            predecessor = getConnection(address);
        }
    }
    
    @Override
    public void fixFingers(){
        int maxBitLenth = 160; //hex number with length 40
        
        if(Math.pow(2, fingerIndex) > maxBitLenth){
            fingerIndex = 1;
        }
        BigInteger lookupIndex = myHash.add(onePartHash.multiply(new BigInteger("" + ((int)Math.pow(2, fingerIndex)), 10)));
        
        fingers.put(fingerIndex, findSuccessor(lookupIndex.toString(16)));
        
        //Print fingers
        Iterator<Address> it = fingers.values().iterator();
        System.out.println("------- Fingers -------");
        while(it.hasNext()){
            Address a = it.next();
            System.out.println(a.toString());
        }
        System.out.println("------- End fingers " + fingers.size() + "-------");
        fingerIndex++;
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
