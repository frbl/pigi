package nl.rug.client.database;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;
import java.io.File;

/**
 * This class handles communication with the SQLite database. It provides some
 * convenient methods to retrieve and store the required information. A
 * SQLiteQueue is used to make access from different threads possible, we use it
 * primarily so we can access the database from different threads. This class is
 * implemented as a singleton to the database can be reached from everywhere in 
 * the program, we have to make sure this is only done where needed and prevent 
 * that the whole application becomes dependant on this class.
 *
 * @author wesschuitema
 */
public class Database {
    
    private static Database INSTANCE;
    
    public static Database getInstance() {
            
        if (INSTANCE == null) {

            INSTANCE = new Database();

        }
        
        return INSTANCE;
        
    }
    
    // this queue is used so that other threads can add a job to the queue.
    private SQLiteQueue queue;
    
    // actual location of the SQLite database file (if not set this will befome 
    // the default defined above
    private File databaseFileLocation = new File("client.db");

    /**
     * Default constructor, private because this is a singleton
     */
    private Database() { }

    /**
     * Executes a SQLiteJob in a blocking way
     *
     * @param <T> Type of the return value, set for the job
     * @param job implementation for an SQLiteJob, this is query and execution,
     * see the SQLite4Java site for examples
     * @return The result of the query defined in the job
     */
    public <T> T executeJobBlocking(SQLiteJob<T> job) {

        return queue.execute(job).complete();

    }

    /**
     * This method is called after creating the SQLite queue. It checks to see
     * if the required tables are present in the database and creates them if
     * not present
     */
    public void initialize() throws SQLiteException {
        
        SQLiteConnection db = new SQLiteConnection(databaseFileLocation);

        try {
            
            // using the sqliteconnection here to create the file if it doesn't 
            // exist
            db.open(true); 

            db.exec(CREATE_DATABASE_SQL);

        } finally {

            db.dispose();

        }
        
        queue = new SQLiteQueue(databaseFileLocation).start();
        
    }
    
    // Maybe change this to use some .sql file with all of this information
    private final static String CREATE_DATABASE_SQL = 
            "BEGIN TRANSACTION;"
            
            + "CREATE TABLE IF NOT EXISTS ChangedPath"
            + "("
            + "	path CHARACTER VARYING NOT NULL,"
            + "	type CHARACTER(1) NOT NULL,"
            + "	number UNSIGNED INTEGER NOT NULL,"
            + "	repository CHARACTER VARYING,"
            + "	PRIMARY KEY (path, repository, number),"
            + " FOREIGN KEY (repository, number) REFERENCES Revision (repository, number) ON DELETE RESTRICT ON UPDATE RESTRICT"
            + ");"

            + "CREATE TABLE IF NOT EXISTS Repository"
            + "("
            + "	URL CHARACTER VARYING,"
            + "	title CHARACTER VARYING NOT NULL,"
            + "	description CHARACTER VARYING,"
            + " PRIMARY KEY (URL)"
            + ");"

            + "CREATE TABLE IF NOT EXISTS Revision"
            + "("
            + "	number UNSIGNED INTEGER NOT NULL,"
            + "	repository CHARACTER VARYING NOT NULL,"
            + "	author CHARACTER VARYING NOT NULL,"
            + "	\"date\" TIMESTAMP NOT NULL,"
            + "	PRIMARY KEY (repository, number)"
            + " FOREIGN KEY (repository) REFERENCES Repository (URL) ON DELETE RESTRICT ON UPDATE RESTRICT"
            + ");"

            + "COMMIT;";
    
}
