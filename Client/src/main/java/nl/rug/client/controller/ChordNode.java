/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.controller;

import ccl.servlet.ChangedRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.WorkingSet;
import nl.rug.client.analysis.ComplexityAnalyzer;
import nl.rug.client.database.ChangedPath;
import nl.rug.client.model.*;

/**
 *
 * @author Rene
 */
public class ChordNode implements IChordNode {

    private Map<String, IChordNode> connections = new HashMap<String, IChordNode>();
    private ComplexityAnalyzer complexityAnalyzer = new ComplexityAnalyzer();
    private Address myAddress;
    private Address predecessor;
    private Address successor;
    
    private Map<Integer, Address> fingers = new HashMap<Integer, Address>();
    private int fingerIndex = 1;
    private BigInteger myHash;
    private BigInteger maxHash = new BigInteger("ffffffffffffffffffffffffffffffffffffffff", 16);
    private BigInteger onePartHash = maxHash.divide(BigInteger.valueOf(160));
    private WorkingSet workingSet;

    public ChordNode(int port, WorkingSet workingSet) {

        this.workingSet = workingSet;

        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        myAddress = new Address(ip, port);
        myHash = new BigInteger(myAddress.getHash(), 16);
        addConnection(myAddress, this);

        scheduleTasks();
    }

    //TO BE REMOVED!!
    //public Map<String, IChordNode> getConnections(){
    //    return connections;
    //}
    
    private void scheduleTasks() {
        Timer timer = new Timer();
        timer.schedule(scheduleFixFingers(), 1000, 3000);
    }

    private TimerTask scheduleFixFingers() {
        return new TimerTask() {
            @Override
            public void run() {
                
                checkPredecessor();
                stabalize();
                fixFingers();
                System.out.println((predecessor == null ? "null" : predecessor) + " : " + myAddress + " : " + (successor == null ? "null" : successor));
                System.out.println("number of connections: " + connections.size());
            }
        };
    }

    public void removeConnection(IChordNode node){
        connections.remove(node.getAddress().getHash());
    }
    
    public boolean containsConnection(Address address){
        return connections.containsKey(address.getHash());
    }
    
    public void addConnection(Address address, IChordNode connection) {
        System.out.println("addConnection(" + address + ")");
        
        if(!connections.containsKey(address.getHash())){
            connections.put(address.getHash(), connection);
        } else {
            System.out.println("Connection already here! - " + address);
            if(myAddress.getHash().compareTo(address.getHash()) > 0){
                connections.get(address.getHash()).kill();
                connections.put(address.getHash(), connection);
                System.out.println("Connection added! i won :D");
            } else {
                System.out.println("Connection ignored, i lost :(");
                connection.kill();
            }
        }
    }

    public IChordNode getConnection(Address address) {
        
        if(address == null){
            System.out.println("Address is null @ " + myAddress);
        }
        
        if (address.equals(myAddress)) {
            return this;
        }        
        
        if(!connections.containsKey(address.getHash())) {
            try {
                ChordConnection connection = new ChordConnection(address);
                Thread thread = new Thread(connection);
                thread.start();
                addConnection(address, connection);
            } catch (IOException ex) {
                Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return connections.get(address.getHash());
    }

    public void create() {
        predecessor = null;
        successor = myAddress;
    }

    @Override
    public Address findSuccessor(String id) {
        Address returnValue = null;
        //if (id.equals(myAddress.getHash())) {
        //    returnValue = successor;
        //}
        //TODO Check here if the data is in de database?
        // Ie:
        /*
         * if(!workingSet.getComplexity(id).isEmpty()) {
         *  return myaddress;
         * }
         */
        
        if (Util.isBetween(id, myAddress.getHash(), successor.getHash())
                //|| (id.compareTo(successor.getHash()) == 0)
                //|| (id.compareTo(myAddress.getHash()) == 0)) {
                ){
            returnValue = successor;
        } else {
            Address n0Address = closestPrecedingNode(id);
            //IChordNode cpn = getConnection(n0Address);
            if (n0Address.getHash().equals(myAddress.getHash())) {
            //    IChordNode successorConnection = getConnection(successor);
            //    return successorConnection.findSuccessor(myAddress.getHash());
                return successor;
            }
            IChordNode cpn = getConnection(n0Address);
            returnValue = cpn.findSuccessor(id);

        }
        
        if(returnValue == null){
            System.out.println("ADDRESS IS NULL!!!");
        }
        
        return returnValue;
    }

    @Override
    public Address closestPrecedingNode(String id) {
        Collection<Address> fingerCol = fingers.values();
        Address[] addresses = fingerCol.toArray(new Address[fingers.size()]);
        for (int i = addresses.length - 1; i >= 0; i--) {
            if (Util.isBetween(addresses[i].getHash(), myAddress.getHash(), id)) {
                return addresses[i];
            }
        }

        return this.getAddress();
    }

    @Override
    public void join(Address address) {
        predecessor = null;
        IChordNode node = getConnection(address);
        successor = node.findSuccessor(myAddress.getHash());
    }

    @Override
    public void stabalize() {

        IChordNode successorConnection = getConnection(successor);
        if (!successorConnection.isAlive()) {
            System.out.println("successor is dead!");
            successor = myAddress;
        }

        successorConnection = getConnection(successor);
        Address x = successorConnection.getPredecessor();

        if (x != null) {
            if (Util.isBetween(x.getHash(), this.getAddress().getHash(), successor.getHash())) {
                System.out.println("change here");
                successor = x;
            }
        }

        successorConnection = getConnection(successor);
        successorConnection.notify(this.getAddress());
    }

    @Override
    public void notify(Address address) {
        if (predecessor == null || Util.isBetween(address.getHash(), predecessor.getHash(), this.getAddress().getHash())) {
            predecessor = address;
        }
    }

    @Override
    public void fixFingers() {
        int maxBitLenth = 160; //hex number with length 40
        fingerIndex++;

        if (Math.pow(2, fingerIndex) > maxBitLenth) {
            fingerIndex = 1;
        }
        BigInteger lookupIndex = myHash.add(onePartHash.multiply(new BigInteger("" + ((int)Math.pow(2, fingerIndex-1)), 10)));
        lookupIndex = lookupIndex.mod(maxHash);
        Address indexSuccessor = findSuccessor(lookupIndex.toString(16));
        if(indexSuccessor != null){
            fingers.put(fingerIndex, indexSuccessor);
        }

        //Print fingers
        /*Iterator<Address> it = fingers.values().iterator();
         System.out.println("------- Fingers -------");
         while(it.hasNext()){
         Address a = it.next();
         System.out.println(a);
         }
         System.out.println("------- End fingers " + fingers.size() + "-------");*/
    }

    @Override
    public void checkPredecessor() {
        if(predecessor == null) return;
        
        IChordNode predecessorConnection = getConnection(predecessor);
        if (predecessorConnection != null && !predecessorConnection.isAlive()) {
            predecessor = null;

        }
    }

    @Override
    public Address getAddress() {
        return myAddress;
    }

    @Override
    public Address getSuccessor() {
        return successor;
    }

    @Override
    public Address getPredecessor() {
        if (predecessor == null) {
            return null;
        }
        return predecessor;
    }

    @Override
    public void ping() {
        System.out.println("Ping not needed here - ChordNode");
    }

    @Override
    public boolean isAlive() {
        return true;
    }
    
    public void kill(){
        //Not needed
    }

    public FileComplexity calculateFileComplexity(String filepath, long revision) {

        FileComplexity fileComplexity = new FileComplexity(filepath, revision);

        Address address = findSuccessor(fileComplexity.getHash());

        IChordNode node = getConnection(address);

        int complexity = node.findFileComplexity(filepath, revision);
        
        if (complexity == -1) {

            complexity=  complexityAnalyzer.startAnalyzing(filepath, revision);

        }

        fileComplexity.setComplexity(complexity);
        
        node.updateComplexity(fileComplexity);

        return fileComplexity;

    }

    @Override
    public Integer findFileComplexity(String filepath, long revision) {

        int complexity = workingSet.getComplexity(filepath, revision);

        return complexity;

    }

    public void updateComplexity(FileComplexity fileComplexity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
