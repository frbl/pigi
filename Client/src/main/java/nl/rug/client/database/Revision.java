package nl.rug.client.database;

import java.sql.Date;
import java.util.List;

/**
 *
 * @author wesschuitema
 */
public class Revision {
    
    public static List<Revision> findByRepositoryId(int repositoryId) {
        
        return null;
        
    }
    
    private int revisionId;
    private String author;
    private Date date;
    private long number;
    private String logMessage;

    /**
     * @return the revisionId
     */
    public int getRevisionId() {
        return revisionId;
    }

    /**
     * @param revisionId the revisionId to set
     */
    public void setRevisionId(int revisionId) {
        this.revisionId = revisionId;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the number
     */
    public long getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(long number) {
        this.number = number;
    }

    /**
     * @return the logMessage
     */
    public String getLogMessage() {
        return logMessage;
    }

    /**
     * @param logMessage the logMessage to set
     */
    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }
    
}
