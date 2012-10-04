package nl.rug.client.database;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;
import java.io.File;

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
    
    private final static File DEFAULT_DATABASE_FILE_LOCATION = new File("client.db"); // default location
    private final static SQLiteQueue queue = new SQLiteQueue(DEFAULT_DATABASE_FILE_LOCATION).start();
    
    public static Repository findByURL(final String URL) {
        
        return queue.execute(new SQLiteJob<Repository>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("SELECT URL, name, description FROM Repository WHERE URL = ?");
                
                // bind starts at 1!
                st.bind(1, URL); 
                
                // URL is the primary key so we should have only one result.
                st.step(); 
                
                Repository repository = new Repository();
                repository.setURL(st.columnString(0));
                repository.setTitle(st.columnString(1));
                repository.setDescription(st.columnString(2));                
                return repository;
                
            }
            
        }).complete();
        
    }
    
    private String URL; // Primary Key
    private String title; // Not Null
    private String description;
    
    public Repository() { }
    
    public boolean save() {
        
        queue.execute(new SQLiteJob<Object>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("INSERT INTO Repository (URL, title, description) VALUES (?, ?, ?)");

                st.bind(1, URL); // bind starts at 1...
                st.bind(2, title);
                st.bind(3, description);
                st.step(); // this executes the statement
                
                return null;
                
            }
            
        }).complete();
        
        // TODO: Find a way to determine if save was successfull!
        return true;
        
    }
    
    public boolean delete() {
        
        queue.execute(new SQLiteJob<Object>() {

            @Override
            protected Repository job(SQLiteConnection connection) throws Throwable {
                
                SQLiteStatement st = connection.prepare("DELETE FROM Repository WHERE URL = ?");

                st.bind(1, URL); // bind starts at 1...
                st.step(); // this executes the statement
                
                return null;
                
            }
            
        }).complete();
        
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
