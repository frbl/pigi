package nl.rug.client;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.database.Database;


/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        
        // This call is needed to make the Maven supplied SQLite4Java library 
        // work. This is needed because of the Maven/JNI comination.
        SQLite.setLibraryPath("target/lib/");
        
        /*
        if (args.length != 3) {
            
            System.out.println("No repository specified. Usage: java -jar App.jar repository_address username password");
            
            System.exit(1);
            
        }
        */
        // Initialize the database
        try {
            
            Database db = Database.getInstance();
            
            db.initialize();
            
        } catch (SQLiteException ex) {
            
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Unable to initialize Database, exiting.", ex);
            
            System.exit(-1);
            
        }
            
        // initialize repository object
        String repositoryAddress = "svn.devided.nl";//args[0];
        String repositoryUsername = "wes";//args[1];
        String repositoryPassword = "xbox360";//args[2];
        RepositoryModel repositoryModel = new RepositoryModel(repositoryAddress, repositoryUsername, repositoryPassword);
        // update the repository object (fetch missing and insert it into the database)
        repositoryModel.initialize();
        
        
        
        // load working set information (file/revision hashes and complexity values)
        
        // connect to the Chord network (fetch address from webservice and initilize network)
             
        // sort working set based on what work remains to be done (with a priority for own work, based on chord range)
        
        // start working (get next item from working set and retrieve from chord
        
    }
    
}
