/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rene
 */
public class Util {
    
    public static String getHash(String string) {
        String hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            hash = byteArrayToHexString(md.digest(string.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hash;
    }
    
    private static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
    
    public static boolean isBetween(BigInteger compareTo, BigInteger compareLow, BigInteger compareHigh){
        
        if(compareLow.compareTo(compareHigh) < 0){ //Low is lower then high (expected most of the time)
            
            return compareTo.compareTo(compareLow) > 0 && compareTo.compareTo(compareHigh) < 0;
            
        } else if(compareLow.compareTo(compareHigh) > 0){ //Low is higher then high
            
            return !isBetween(compareTo, compareHigh, compareLow);//compareTo.compareTo(compareLow) > 0 || compareTo.compareTo(compareHigh) <= 0;
            
        } else {
            System.out.println(compareTo + " : " + compareLow + " : " + compareHigh);
            System.out.println(compareTo.compareTo(compareLow) > 0 || compareTo.compareTo(compareHigh) <= 0);
            //return compareTo.compareTo(compareLow) > 0 || compareTo.compareTo(compareHigh) <= 0;
        }
        
        //low and high are the same. return true
        return false;
    }
    
    public static boolean isBetween(String valueToCompare, String compareLow, String compareHigh){
        
        return isBetween(new BigInteger(valueToCompare,16), new BigInteger(compareLow, 16), new BigInteger(compareHigh, 16));
    
    }
//    
//     public static boolean isBetweenFindSucTest(BigInteger valueToCompare, BigInteger from, BigInteger to){
//        
//        if (from.compareTo(to) < 0) {
//                        if (valueToCompare.compareTo(from) > 0 && valueToCompare.compareTo(to) < 0) {
//                                return true;
//                        }
//                } else if (from.compareTo(to) > 0) {
//                        if (valueToCompare.compareTo(to) < 0 || valueToCompare.compareTo(from) > 0) {
//                                return true;
//                        }
//                }
//                return false;
//    }
    
    public static void main(String[] args){
        BigInteger test1 = BigInteger.valueOf(10);
        BigInteger test2 = BigInteger.valueOf(20);
        BigInteger test3 = BigInteger.valueOf(30);
        
        System.out.println(Util.isBetween(test1, test2, test3)); //False
        System.out.println(Util.isBetween(test2, test3, test1)); //False
        System.out.println(Util.isBetween(test2, test1, test3)); //True
        System.out.println(Util.isBetween(test3, test1, test2)); //False
        System.out.println(Util.isBetween(test3, test1, test3)); //False
        System.out.println(Util.isBetween(test3, test2, test1)); //True
        System.out.println(Util.isBetween(test1, test3, test2)); //True
    }
}
