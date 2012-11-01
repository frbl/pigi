package nl.rug.client;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.analysis.Analyzer;
import nl.rug.client.analysis.ComplexityAnalyzer;
import nl.rug.client.controller.ClientController;
import nl.rug.client.database.ChangedPath;
import nl.rug.client.database.Database;
import nl.rug.client.model.Address;
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

        if (args.length != 8 && args.length != 6) {
            
            logger.log(Level.SEVERE, "No repository specified. Usage: java -jar "
                    + "App.jar repository_address username password address port database_location"
                    + " [chord_seed_address chord_seed_port]");

            System.exit(1);

        }
        
        String repositoryAddress = args[0];
        String repositoryUsername = args[1];
        String repositoryPassword = args[2];
        String address = args[3];
        int port = 0;
        try {
            
            port = Integer.parseInt(args[4]);
            
        } catch (NumberFormatException ex) {
            
            logger.log(Level.SEVERE, "Port should be a number.", ex);

            System.exit(1);
            
        }
        String databaseLocation = args[5];
        
        // if we need to connect to an existing node
        String chordSeedAddress = "";
        int chordSeedPort = 0;
        if (args.length == 8) {
            
            chordSeedAddress = args[6];
            
            try {

                chordSeedPort = Integer.parseInt(args[7]);

            } catch (NumberFormatException ex) {

                logger.log(Level.SEVERE, "Chord seed port should be a number.", ex);

                System.exit(1);

            }
            
            logger.log(Level.INFO, "Will connect to {0}:{1}", new Object[]{chordSeedAddress, chordSeedPort});
            
        }
        
        

        // Initialize the database, needed because I made everything too complex
        // and now we are stuck here initializing stuff for nothing
        try {

            Database db = Database.getInstance();

            db.initialize(new File(databaseLocation));

        } catch (SQLiteException ex) {

            logger.log(Level.SEVERE, "Unable to initialize Database, exiting.", ex);

            System.exit(-1);

        }

        // update the repository object (fetch missing and insert it into the database)        
        RepositoryModel repositoryModel = new RepositoryModel(repositoryAddress, repositoryUsername, repositoryPassword);
        try {

            repositoryModel.initialize();

        } catch (SVNException ex) {

            logger.log(Level.SEVERE, "Error while making a connection with the repository, exiting", ex);

            System.exit(2);

        }

        // load working set information (file/revision hashes and complexity values)
        String nodeHash = Util.getHash(address + port);
        
        WorkingSet workingSet = new WorkingSet(repositoryAddress, nodeHash);

        // connect to the Chord network (fetch address from webservice and initilize network)
        ClientController clientController = null;
        
        Address localAddress = new Address(address, port);
        
        // Create the analyzer which should perform the actual complexity calculation.
        Analyzer complexityAnalyzer = new ComplexityAnalyzer(port+"",repositoryModel);
        
        
        if (chordSeedAddress.equals("") && chordSeedPort == 0) {
            // this is the first node
            clientController = new ClientController(localAddress, workingSet, complexityAnalyzer);
            
        } else {
            // connect to existing node
            Address remoteAddress = new Address(chordSeedAddress, chordSeedPort);
            clientController = new ClientController(localAddress, remoteAddress, workingSet, complexityAnalyzer);          
        }

        // start working (get next item from working set and retrieve from chord)
        int numberOfJobs = workingSet.getNumberOfJobs();
        int finishedNumberOfJobs = workingSet.getFinishedNumberOfJobs();
        int remaining = numberOfJobs - finishedNumberOfJobs;

        logger.log(Level.INFO, "Starting work, {0} of {1} remaining.", new Object[]{remaining, numberOfJobs});

        while (remaining != 0) {

            // get next job from working set
            try{
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

            logger.log(Level.INFO, "Finished job, {0} of {1} "
                    + "remaining.", new Object[]{remaining, numberOfJobs});

            }catch (NoSuchElementException ex){
                logger.log(Level.INFO, "Finished jobs all jobs on the queue.", (Object[]) null);
                break;
            }
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
