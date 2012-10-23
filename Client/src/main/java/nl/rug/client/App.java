package nl.rug.client;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.database.Database;
import nl.rug.client.model.Util;
import org.tmatesoft.svn.core.SVNException;


/**
 * Main entry point for the Client Application
 */
public class App {

    /**
     * Main method for the application. Initialized what is needed and starts 
     * the work.
     * 
     * @param args Command-line arguments in the form of repository_address username password chord_seed_address chord_seed_port
     */
    public static void main(String[] args) {
        
        // This call is needed to make the Maven supplied SQLite4Java library 
        // work. This is needed because of the Maven/JNI comination.
        SQLite.setLibraryPath("target/lib/");
        
        if (args.length != 5) {
            
            System.out.println("No repository specified. Usage: java -jar App.jar repository_address username password chord_seed_address chord_seed_port");
            
            System.exit(1);
            
        }
        
        String repositoryAddress = args[0];
        String repositoryUsername = args[1];
        String repositoryPassword = args[2];
        String chordSeedAddress = args[3];
        String chordSeedPort = args[4];
        
        // Initialize the database, needed because I made everything too complex
        // and now we are stuck here initializing stuff for nothing
        try {
            
            Database db = Database.getInstance();
            
            db.initialize();
            
        } catch (SQLiteException ex) {
            
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Unable to initialize Database, exiting.", ex);
            
            System.exit(-1);
            
        }        
        
        // update the repository object (fetch missing and insert it into the database)        
        RepositoryModel repositoryModel = new RepositoryModel(repositoryAddress, repositoryUsername, repositoryPassword);;
        try {
            
            repositoryModel.initialize();
            
        } catch (SVNException ex) {
            
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Error while making a connection with the repository, exiting", ex);
            
            System.exit(2);
            
        }
        
        // load working set information (file/revision hashes and complexity values)
        String nodeHash = Util.getHash(chordSeedAddress + chordSeedPort);
        WorkingSet workingSet = new WorkingSet(repositoryAddress, nodeHash);
            
        // connect to the Chord network (fetch address from webservice and initilize network)

        // start working (get next item from working set and retrieve from chord
        
        System.exit(0);
        
    }
    
}
