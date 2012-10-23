package nl.rug.client.database;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wesschuitema
 */
public class ChangedPath {
    
    // this will return all the changed paths records for a repository, unless 
    // it regards a deleted value, in that case there is no complexity to 
    // calculate
    public static List<ChangedPath> getWorkingSet(final Repository repository) {
        
        // probably not _the_ way to do this, but good enough for now
        return (List<ChangedPath>) Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Object job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare(""
                        + "SELECT number, path, type, complexity, hash "
                        + "FROM ChangedPath "
                        + "WHERE repository = ? "
                        + "AND path LIKE ?"
                        + "AND type <> ?");
       
                // bind starts at 1!
                String URL = repository.getURL();
                st.bind(1, URL);
                // only get the java files
                String like = "%.java";
                st.bind(2, like);
                st.bind(3, DELETED);
                
                List<ChangedPath> changedPaths = new ArrayList<ChangedPath>();
                
                while (st.step()) {
                    
                    long number = st.columnLong(0);
                    String path = st.columnString(1);
                    
                    // TODO: Fix another hack... reading it as a string gets a 
                    // number.
                    char type = (char)Integer.parseInt(st.columnString(2));
                    
                    int complexity = st.columnInt(3);
                    String hash = st.columnString(4);
                    
                    ChangedPath changedPath = new ChangedPath();
                    
                    // TODO: Dirtyhack to prevent extra query to get revision
                    Revision revision = new Revision();
                    revision.setNumber(number);                    
                    changedPath.setRevision(revision);
                    
                    changedPath.setPath(path);                    
                    
                    // perhaps using an enum was not as elegant as I imagined, 
                    // should change it when I get the chance
                    changedPath.setType(type);
                    
                    changedPath.setComplexity(complexity);
                    changedPath.setHash(hash);
                    
                    changedPaths.add(changedPath);
                    
                }
                               
                return changedPaths;
                
            }
            
        });
        
    }
    
    public static List<ChangedPath> findByRevision(final Revision revision) {
        
        // probably not _the_ way to do this, but good enough for now
        return (List<ChangedPath>) Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Object job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("SELECT path, type, complexity, hash FROM ChangedPath WHERE repository = ? AND number = ?");
                
                // bind starts at 1!
                String URL = revision.getRepository().getURL();
                st.bind(1, URL);
                long number = revision.getNumber();
                st.bind(2, number);
                
                List<ChangedPath> changedPaths = new ArrayList<ChangedPath>();
                
                while (st.step()) {
                    
                    ChangedPath changedPath = new ChangedPath();
                    
                    changedPath.setRevision(revision);
                    changedPath.setPath(st.columnString(0));                    
                    
                    // perhaps using an enum was not as elegant as I imagined, 
                    // should change it when I get the chance
                    changedPath.setType(st.columnString(1).charAt(0));
                    
                    changedPath.setComplexity(st.columnInt(2));
                    changedPath.setHash(st.columnString(3));
                    
                    changedPaths.add(changedPath);
                    
                }
                               
                return changedPaths;
                
            }
            
        });
        
    }

    /**
     * @return the complexity
     */
    public int getComplexity() {
        return complexity;
    }

    /**
     * @param complexity the complexity to set
     */
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }
    
    private final static char ADDED = 'A';
    private final static char DELETED = 'D';
    private final static char MODIFIED = 'M';
    
    private Revision revision; // first part of PK
    private String path; // second part of PK
    // TODO add final static chars with descriptive names for 'A' - added, 'D' - deleted and 'M' - modified
    private char type; // Not null (stored as char in database)
    private int complexity; // -1 is when we do not know
    private String hash;
    
    public Revision getRevision() {
        
        return revision;
        
    }
    
    public void setRevision(Revision revision) {
        
        this.revision = revision;
        
    }

    /**
     * @return the value
     */
    public String getPath() {
        return path;
    }

    /**
     * @param value the value to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the type
     */
    public char getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(char type) {
        this.type = type;
    }
    
    public boolean delete() {
        
        Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("DELETE FROM ChangedPath WHERE repository = ? AND number = ? AND path = ?");

                st.bind(1, revision.getRepository().getURL()); // bind starts at 1...
                st.bind(2, revision.getNumber());
                st.bind(3, path);
                st.step(); // this executes the statement
                
                return null;
                
            }
            
        });
        
        // TODO: Find a way to determine if delete was successfull!
        return true;
        
    }
    
    public boolean save() {
        
        /*
         * This entity is the only one that is also updated in the application. 
         * To make it easy to use save for an update, we jsut delete the exiting
         * one. This method should not be used if the system is extended, it can
         * only be used here; it shouldn't even be used here!
         * -=QUICKFIX/DIRTYHACK=-
         */
        delete();
        
        Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("INSERT INTO ChangedPath (repository, number, path, type, complexity, hash) VALUES (?, ?, ?, ?, ?, ?)");

                st.bind(1, revision.getRepository().getURL()); // bind starts at 1...
                st.bind(2, revision.getNumber());
                st.bind(3, path);
                st.bind(4, type);
                st.bind(5, complexity);
                st.bind(6, hash);
                st.step(); // this executes the statement
                
                return null;
                
            }
            
        });
        
        // TODO: Find a way to determine if save was successfull!
        return true;
        
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }
    
}
