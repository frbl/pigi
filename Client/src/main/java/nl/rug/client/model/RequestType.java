/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.model;

import java.io.Serializable;

/**
 *
 * @author Rene
 */
public enum RequestType implements Serializable {
     ADDRESS, CPN, FS, CP, PING, SUCCESSOR, PREDECESSOR, JOIN, STABALIZE, NOTIFY, FILE
}
