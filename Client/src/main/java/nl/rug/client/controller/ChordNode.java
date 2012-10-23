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
    private BigDecimal maxHashDec = new BigDecimal(maxHash);
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

    private void scheduleTasks() {
        Timer timer = new Timer();
        timer.schedule(scheduleFixFingers(), 1000, 100);
    }

    private TimerTask scheduleFixFingers() {
        return new TimerTask() {
            @Override
            public void run() {

                if (predecessor != null) {
                    System.out.println("p" + predecessor.getAddress() + ":" + getAddress() + ":s" + successor.getAddress());
                }
                checkPredecessor();
                stabalize();
                fixFingers();
            }
        };
    }

    public void addConnection(IChordNode connection) {
        connections.put(connection.getAddress().getHash(), connection);
    }

    public IChordNode getConnection(Address address) {
        if (connections.containsKey(address.getHash())) {
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

    public void create() {
        predecessor = null;
        successor = this;
    }

    @Override
    public Address findSuccessor(String id) {

        if(id.equals(myAddress.getHash())) {
            return myAddress;
        }
        
        if (Util.isBetween(id, myAddress.getHash(), successor.getAddress().getHash())
                || (id.compareTo(successor.getAddress().getHash()) == 0)
                || (id.compareTo(getAddress().getHash()) == 0)) {

            return successor.getAddress();

        } else {

            Address n0Address = closestPrecedingNode(id);
            IChordNode cpn = getConnection(n0Address);
            if (cpn.getAddress().getHash().equals(myAddress.getHash())) {
                return successor.findSuccessor(myAddress.getHash());
            }
            return cpn.findSuccessor(id);

        }

    }

    @Override
    public Address closestPrecedingNode(String id) {
        Collection<Address> fingerCol = fingers.values();
        Address[] addresses = fingerCol.toArray(new Address[fingers.size()]);
        for (int i = addresses.length - 1; i == 0; i--) {
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
        successor = getConnection(node.findSuccessor(myAddress.getHash()));
    }

    @Override
    public void stabalize() {
        Address x = successor.getPredecessor();

        if (x != null) {
            
            if (Util.isBetween(x.getHash(), this.getAddress().getHash(), successor.getAddress().getHash())) {
                successor = getConnection(x);
            }

        }

        successor.notify(this.getAddress());
    }

    @Override
    public void notify(Address address) {
        if (predecessor == null || Util.isBetween(address.getHash(), predecessor.getAddress().getHash(), this.getAddress().getHash())) {
            predecessor = getConnection(address);
        }
    }

    @Override
    public void fixFingers() {
        fingerIndex++;

        if (fingerIndex > 160) {
            fingerIndex = 1;
        }

        BigInteger fingered = myHash.add(BigInteger.valueOf(2).pow(fingerIndex - 1)).mod(maxHash);

        if (fingered.compareTo(maxHash) > 0) {
            System.out.println("Larger than mod operation, not possible!!");
        }

        fingers.put(fingerIndex, findSuccessor(fingered.toString(16)));

    }

    @Override
    public void checkPredecessor() {
        if (predecessor != null && !predecessor.isAlive()) {
            System.out.println("Removing predecessor " + predecessor.getAddress().getPort());
            connections.remove(predecessor.getAddress().getHash());
            predecessor = null;
        }
    }

    @Override
    public Address getAddress() {
        return myAddress;
    }

    @Override
    public Address getSuccessor() {
        return successor.getAddress();
    }

    @Override
    public Address getPredecessor() {
        if (predecessor == null) {
            return null;
        }
        return predecessor.getAddress();
    }

    @Override
    public void ping() {
        System.out.println("Ping not needed here - ChordNode");
    }

    public boolean isAlive() {
        return true;
    }

    public FileComplexity getFileComplexity(String filepath, int revision) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
