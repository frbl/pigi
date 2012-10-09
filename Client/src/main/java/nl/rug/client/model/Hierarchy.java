/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author Rene
 */
public class Hierarchy {
    
    private Child leader;
    
    public Hierarchy(Address address){
        leader = new Child(address);
    }
    
    /*
     * Returns parentIp for the next position
     */
    public Address getNextPosition(){
        Stack<Child> list = new Stack<Child>();
        list.push(leader);
        Child current;
        while((current = list.pop()) != null){
            if(current.getChildren().size() < 2){
                return current.address;
            } else {
                list.addAll(current.getChildren());
            }
        }
        return null; //We should not end up here
    }
    
    public void addChild(Address address){
        
    }
    
    private class Child {
        
        private List<Child> children = new ArrayList<Child>();
        private Address address;
        
        public Child(Address address){
            this.address = address;
        }
        
        public void addChild(Child child){
            children.add(child);
        }
        
        public List<Child> getChildren(){
            return children;
        }
        
    };
}
