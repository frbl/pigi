package nl.rug.client.database;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;

/**
 * Repository database entity, uses a simplified Active Record pattern. Should 
 * be refactored so that shared code between other entities is kept to a 
 * minimum. At this point, we'll already be satisfied with something that works.
 * We do not expect this code to last; if this is not the case, refactoring 
 * three classes is not too much work.
 * 
 * @author wesschuitema
 */
public class Repository {    
    
    public static Repository findByURL(final String URL) {
        
        return Database.getInstance().executeJobBlocking(new SQLiteJob<Repository>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("SELECT title, description FROM Repository WHERE URL = ?");
                
                // bind starts at 1!
                st.bind(1, URL);
                
                Repository repository = null;
                
                // should only happe once
                while (st.step()) {
                    repository = new Repository();
                    repository.setURL(URL);
                    repository.setTitle(st.columnString(0));
                    repository.setDescription(st.columnString(1));    
                }               
                            
                return repository;
                
            }
            
        });
        
    }
    
    private String URL; // Primary Key
    private String title; // Not Null
    private String description;
    
    public Repository() { }
    
    /**
     * Saves the Repository entity. DOES NOT WORK FOR UPDATES!
     * 
     * @return true if save is successful, false otherwise (TBD)
     */
    public boolean save() {
        
        Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("INSERT INTO Repository (URL, title, description) VALUES (?, ?, ?)");

                st.bind(1, URL); // bind starts at 1...
                st.bind(2, title);
                st.bind(3, description);
                st.step(); // this executes the statement
                
                return null;
                
            }
            
        });
        
        // TODO: Find a way to determine if save was successfull!
        return true;
        
    }
    
    public boolean delete() {
        
        Database.getInstance().executeJobBlocking(new SQLiteJob<Object>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("DELETE FROM Repository WHERE URL = ?");

                st.bind(1, URL); // bind starts at 1...
                st.step(); // this executes the statement
                
                return null;
                
            }
            
        });
        
        // TODO: Find a way to determine if delete was successfull!
        return true;
        
    }
    
    public String getURL() {
        
        return URL;
        
    }
    
    public String getTitle() {
        
        return title;
        
    }

    public String getDescription() {
        
        return description;
        
    }
    
    public void setURL(String URL) {
        
        this.URL = URL;
        
    }
    
    public void setTitle(String title) {
        
        this.title = title;
        
    }
    
    public void setDescription(String description) {
        
        this.description = description;
        
    }
    
}
