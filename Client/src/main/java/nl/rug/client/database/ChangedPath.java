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
    
    public static List<ChangedPath> findByRevision(final Revision revision) {
        
        // probably not _the_ way to do this, but good enough for now
        return (List<ChangedPath>) Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Object job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("SELECT path, type, complexity FROM ChangedPath WHERE repository = ? AND number = ?");
                
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
                    changedPath.setType(determineType(st.columnString(1)));
                    
                    changedPath.setComplexity(st.columnInt(2));
                    
                    changedPaths.add(changedPath);
                    
                }
                               
                return changedPaths;
                
            }

            private Type determineType(String columnString) {
                if (columnString.equals("A")) {
                    return Type.ADDED;
                }
                else if (columnString.equals("D")) {
                    return Type.DELETED;
                }
                else if (columnString.equals("M")) {
                    return Type.MODIFIED;
                }
                else if (columnString.equals("R")) {
                    return Type.REPLACED;
                }
                else {
                    // this should never happen, but beware there is not 
                    // constraint present in the database for this!
                    return Type.REPLACED;
                } 
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
    
    /**
     * Type indicates the type of ChangedPath, these are the values that are 
     * stored in the database. 
     */
    public enum Type {
        ADDED('A'), 
        DELETED('D'),
        MODIFIED('M'),
        REPLACED('R');
        
        private char value;
        
        private Type(char value) {
            
            this.value = value;
            
        }
        
        @Override
        public String toString() {
            
            return "" + value;
            
        }
        
    }
    
    private Revision revision; // first part of PK
    private String path; // second part of PK
    private Type type; // Not null (stored as char in database)
    private int complexity;
    
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
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
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
                
                SQLiteStatement st = connection.prepare("INSERT INTO ChangedPath (repository, number, path, type, complexity) VALUES (?, ?, ?, ?, ?)");

                st.bind(1, revision.getRepository().getURL()); // bind starts at 1...
                st.bind(2, revision.getNumber());
                st.bind(3, path);
                st.bind(4, type.toString());
                st.bind(5, complexity);
                st.step(); // this executes the statement
                
                return null;
                
            }
            
        });
        
        // TODO: Find a way to determine if save was successfull!
        return true;
        
    }
    
}
