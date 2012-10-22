/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.math.BigInteger;
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
    
    public static boolean isBetween(String compareTo, String compareLow, String compareHigh){
        return compareTo.compareTo(compareLow) >= 0 && compareTo.compareTo(compareHigh) <= 0;
    }
}
