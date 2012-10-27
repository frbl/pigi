package nl.rug.client;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.controller.ClientController;
import nl.rug.client.database.ChangedPath;
import nl.rug.client.database.Database;
import nl.rug.client.model.FileComplexity;
import nl.rug.client.model.Util;
import org.tmatesoft.svn.core.SVNException;

/**
 * Main entry point for the Client Application
 */
public class App {

    private final static Logger logger = Logger.getLogger(App.class.getName());

    /**
     * Main method for the application. Initialized what is needed and starts
     * the work.
     *
     * @param args Command-line arguments in the form of repository_address
     * username password chord_seed_address chord_seed_port
     */
    public static void main(String[] args) {

        // This call is needed to make the Maven supplied SQLite4Java library 
        // work. This is needed because of the Maven/JNI comination.
        SQLite.setLibraryPath("target/lib/");

        if (args.length != 5) {

            logger.log(Level.SEVERE, "No repository specified. Usage: java -jar App.jar repository_address username password chord_seed_address chord_seed_port");

            System.exit(1);

        }
        
        //TODO Shouldnt the client recieve its own port to run on? or is this random or something?
        String repositoryAddress = args[0];
        String repositoryUsername = args[1];
        String repositoryPassword = args[2];
        String chordSeedAddress = args[3];
        int chordSeedPort = 0;
        try {
            
            chordSeedPort = Integer.parseInt(args[4]);
            
        } catch (NumberFormatException ex) {
            
            logger.log(Level.SEVERE, "Port specified should be a number.", ex);

            System.exit(1);
            
        }

        // Initialize the database, needed because I made everything too complex
        // and now we are stuck here initializing stuff for nothing
        try {

            Database db = Database.getInstance();

            db.initialize();

        } catch (SQLiteException ex) {

            logger.log(Level.SEVERE, "Unable to initialize Database, exiting.", ex);

            System.exit(-1);

        }

        // update the repository object (fetch missing and insert it into the database)        
        RepositoryModel repositoryModel = new RepositoryModel(repositoryAddress, repositoryUsername, repositoryPassword);;
        try {

            repositoryModel.initialize();

        } catch (SVNException ex) {

            logger.log(Level.SEVERE, "Error while making a connection with the repository, exiting", ex);

            System.exit(2);

        }

        // load working set information (file/revision hashes and complexity values)
        String nodeHash = Util.getHash(chordSeedAddress + chordSeedPort);
        WorkingSet workingSet = new WorkingSet(repositoryAddress, nodeHash);


        // connect to the Chord network (fetch address from webservice and initilize network)

        // start working (get next item from working set and retrieve from chord)
        int numberOfJobs = workingSet.getNumberOfJobs();
        int finishedNumberOfJobs = workingSet.getFinishedNumberOfJobs();
        int remaining = numberOfJobs - finishedNumberOfJobs;

        logger.log(Level.INFO, "Starting work, {0} of {1} remaining.", new Object[]{remaining, numberOfJobs});

        //TODO!!!!!!!!!! Chordseedport is not correct here!
        ClientController clientController = new ClientController(chordSeedPort, workingSet);

        while (remaining != 0) {

            // get next job from working set
            List<ChangedPath> jobs = workingSet.nextJob();

            // give job to chord network and retrieve value
            for (ChangedPath job : jobs) {

                FileComplexity fileComplexity = clientController.getComplexity(job.getPath(), job.getRevision());

                // Will this work for the workingset inside the ChordNode as well?
                workingSet.setComplexity(fileComplexity);

            }

            // if retrieved value is -1, calculate it ourselves and put value in chord to update the node that is responsible for it

            // update workingset with retrieved (or calculated) value

            finishedNumberOfJobs = workingSet.getFinishedNumberOfJobs();

            remaining = numberOfJobs - finishedNumberOfJobs;

            logger.log(Level.INFO, "Finished job, {0} of {1} remaining.", new Object[]{remaining, numberOfJobs});

        }

        logger.log(Level.INFO, "Finished retrieving complexity values for {0}.", repositoryAddress);
        logger.log(Level.INFO, "Going into seeding mode, still providing information to other nodes.");
        logger.log(Level.INFO, "Press any key to stop...");

        try {

            System.in.read();

        } catch (IOException ex) {

            logger.log(Level.SEVERE, "Something went wrong while waiting for you to exit the application", ex);

            System.exit(3);

        }

        System.exit(0);

    }
}
