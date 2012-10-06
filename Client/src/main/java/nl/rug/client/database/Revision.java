package nl.rug.client.database;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * The revision database entity uses a simplified active record pattern. As 
 * described for the Repository class, there is some duplicate code here. Will 
 * be refactored should the need arise. At least the SQLiteQueue parts should 
 * not be done multiple times.
 * 
 * @author wesschuitema
 */
public class Revision {
    
    public static Revision findById(final Repository repository, final long number) {
        
        // probably not _the_ way to do this, but good enough for now
        return Database.getInstance().executeJobBlocking(new SQLiteJob<Revision>() {

            @Override
            protected Revision job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("SELECT number, author, date FROM Revision WHERE repository = ? AND number = ?");
                
                // bind starts at 1!
                String URL = repository.getURL();
                st.bind(1, URL);
                st.bind(2, number);
                
                Revision revision = null;
                
                while (st.step()) {
                    
                    revision = new Revision();
                    /* 
                     * Set like this to prevent having to do a query to retrieve
                     * the repository.
                     */
                    revision.setRepository(repository);
                    revision.setNumber(number);
                    /* 
                     * 0 for the first string or 1 for the second column... 
                     */
                    revision.setAuthor(st.columnString(1));
                    /* 
                     * SQLite returns a Unix timestamp for the date
                     */
                    revision.setDate(new Date(st.columnLong(2)));
                    
                }
                               
                return revision;
                
            }
            
        });
        
    }
    
    public static List<Revision> findByRepository(final Repository repository) {
        
        // probably not _the_ way to do this, but good enough for now
        return (List<Revision>) Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Object job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("SELECT number, author, date FROM Revision WHERE repository = ?");
                
                // bind starts at 1!
                String URL = repository.getURL();
                st.bind(1, URL);
                
                List<Revision> revisions = new ArrayList<Revision>();
                
                while (st.step()) {
                    
                    Revision revision = new Revision();
                    /* 
                     * Set like this to prevent having to do a query to retrieve
                     * the repository.
                     */
                    revision.setRepository(repository);
                    revision.setNumber(st.columnLong(0));
                    /* 
                     * 0 for the first string or 1 for the second column... 
                     */
                    revision.setAuthor(st.columnString(1));
                    /* 
                     * SQLite returns a Unix timestamp for the date
                     */
                    revision.setDate(new Date(st.columnLong(2)));
                    
                    revisions.add(revision);
                    
                }
                               
                return revisions;
                
            }
            
        });
        
    }
        
    private Repository repository; // repository URL, part of the PK
    private long number; // other half of the PK
    private String author; // Not null
    private Date date; // Not null
    /* not yet in the database! (remember to change queries when adding) */
    private String logMessage;
    
    public boolean delete() {
        
        Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("DELETE FROM Revision WHERE repository = ? AND number = ?");

                st.bind(1, repository.getURL()); // bind starts at 1...
                st.bind(2, number);
                st.step(); // this executes the statement
                
                return null;
                
            }
            
        });
        
        // TODO: Find a way to determine if delete was successfull!
        return true;
        
    }
    
    public boolean save() {
        
        Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("INSERT INTO Revision (repository, number, author, date) VALUES (?, ?, ?, ?)");

                st.bind(1, repository.getURL()); // bind starts at 1...
                st.bind(2, number);
                st.bind(3, author);
                st.bind(4, date.getTime());
                st.step(); // this executes the statement
                
                return null;
                
            }
            
        });
        
        // TODO: Find a way to determine if save was successfull!
        return true;
        
    }

    /**
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
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
