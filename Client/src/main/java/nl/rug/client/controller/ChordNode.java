/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.WorkingSet;
import nl.rug.client.analysis.ComplexityAnalyzer;
import nl.rug.client.model.*;

/**
 *
 * @author Rene
 */
public class ChordNode implements IChordNode {

    private final static int m = 160; // max bit length
    private Map<String, IChordNode> connections = new HashMap<String, IChordNode>();
    private ComplexityAnalyzer complexityAnalyzer = new ComplexityAnalyzer();
    private Address myAddress;
    private Address predecessor;
    private Address successor;
    private Address[] finger = new Address[m];
    private int next = 0;
    private BigInteger myHash;
    private BigInteger maxHash = new BigInteger("ffffffffffffffffffffffffffffffffffffffff", 16);
    private BigInteger onePartHash = maxHash.divide(BigInteger.valueOf(160));
    private WorkingSet workingSet;
    private boolean notifyRound = false;

    public ChordNode(Address address, WorkingSet workingSet) {

        this.workingSet = workingSet;

        String ip = address.getIp();
        if (ip == null || ip.equals(address)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        myAddress = new Address(ip, address.getPort());
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

                notifyRound = false;
                checkPredecessor();
                stabalize();
                fixFingers();
                System.out.println((predecessor == null ? "null" : predecessor) + " : " + myAddress + " : " + (successor == null ? "null" : successor));
                //System.out.println("number of connections: " + connections.size());
            }
        };
    }

    public void removeConnection(IChordNode node) {
        if(successor.getHash().equals(node.getAddress().getHash())){
            successor = myAddress;
        }
        if(predecessor.getHash().equals(node.getAddress().getHash())){
            predecessor = null;
        }
        for(int i = 0; i < finger.length; i++){
            if(finger[i] != null && finger[i].getHash().equals(node.getAddress())){
                finger[i] = null;
            }
        }
        connections.remove(node.getAddress().getHash());
    }

    public boolean containsConnection(Address address) {
        return connections.containsKey(address.getHash());
    }

    public void addConnection(Address address, IChordNode connection) {
        System.out.println("addConnection(" + address + ")");

        if (!connections.containsKey(address.getHash())) {
            connections.put(address.getHash(), connection);
        } else {
            System.out.println("Connection already here! - " + address);
            if (myAddress.getHash().compareTo(address.getHash()) > 0) {
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

        if (address == null) {
            System.out.println("Address is null @ " + myAddress);
        }

        if (address.equals(myAddress)) {
            return this;
        }

        if (!connections.containsKey(address.getHash())) {

            ChordConnection connection = null;

            try {
                connection = new ChordConnection(address);
            } catch (IOException ex) {
                Logger.getLogger(ChordNode.class.getName()).log(Level.SEVERE, null, ex);
            }

            Thread thread = new Thread(connection);
            thread.start();

            addConnection(address, connection);

        }

        return connections.get(address.getHash());
    }

    public void create() {
        predecessor = null;
        successor = myAddress;
    }

    @Override
    public Address findSuccessor(String id) {

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

        if (Util.isBetween(id, myAddress.getHash(), successor.getHash()) || id.equals(successor.getHash())) {
            return successor;
        } else {
            Address n0Address = closestPrecedingNode(id);
            if (n0Address.getHash().equals(myAddress.getHash())) {
                return successor;
            }
            IChordNode cpn = getConnection(n0Address);
            return cpn.findSuccessor(id);

        }
    }

    @Override
    public Address closestPrecedingNode(String id) {

        for (int i = m; i > 0; i--) {
            if (finger[i - 1] != null && Util.isBetween(id, this.getAddress().getHash(), finger[i - 1].getHash())) {
                return finger[i - 1];
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
            if ((Util.isBetween(x.getHash(), this.getAddress().getHash(), successor.getHash())
                    || this.getAddress().getHash().equals(successor.getHash()))
                    && !x.getHash().equals(myAddress.getHash())) {
                successor = x;
            }
        }

        successorConnection = getConnection(successor);
        successorConnection.notify(this.getAddress());
    }

    @Override
    public void notify(Address address) {
        if (predecessor != null && address.getHash().equals(myAddress.getHash())) {
            return;
        }
        if (predecessor == null || Util.isBetween(address.getHash(), predecessor.getHash(), this.getAddress().getHash()) || predecessor.getHash().equals(this.getAddress().getHash())) {
            predecessor = address;
        }
    }

    @Override
    public void fixFingers() {

        next++;

        if (next > m) {

            next = 1;

        }

        finger[next - 1] = findSuccessor(Util.addPowerOfTwo(this.getAddress(), next - 1));

    }

    @Override
    public void checkPredecessor() {
        if (predecessor == null) {
            return;
        }

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

    public void kill() {
        //Not needed
    }

    public FileComplexity calculateFileComplexity(String filepath, long revision) {

        FileComplexity fileComplexity = new FileComplexity(filepath, revision);

        Address address = findSuccessor(fileComplexity.getHash());

        IChordNode node = getConnection(address);

        fileComplexity = node.getFileComplexity(filepath, revision);

        if (fileComplexity.getComplexity() == -1) {

            fileComplexity.setComplexity(complexityAnalyzer.startAnalyzing(filepath, revision));

        }

        node.updateComplexity(fileComplexity);

        return fileComplexity;

    }

    @Override
    public FileComplexity getFileComplexity(String filepath, long revision) {

        int complexity = workingSet.getComplexity(filepath, revision);

        FileComplexity fileComplexity = new FileComplexity(filepath, revision);

        fileComplexity.setComplexity(complexity);

        return fileComplexity;

    }

    @Override
    public void updateComplexity(FileComplexity fileComplexity) {

        workingSet.setComplexity(fileComplexity);

    }
}
