package nl.rug.client.database;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles communication with the SQLite database. It provides some
 * convenient methods to retrieve and store the required information. A 
 * SQLiteQueue is used to make access from different threads possible, we use it
 * primarily to prevent the GUI from blocking when accessing the database. Since
 * access is asynchronous, the methods return a proxy object representing the 
 * required information when reading, the SQLiteQueue callback will make sure 
 * the returned object provides access to the correct information when it is 
 * retrieved.
 * 
 * @author wesschuitema
 */
public class Database {
    
    // this queue is used so that other threads can add a job to the queue.
    private SQLiteQueue queue;
    
    // this default location is used when an instance is created without 
    // providing a file location
    private final static File DEFAULT_DATABASE_FILE_LOCATION 
            = new File("client.db"); // default location
    
    /**
     * Default constructor, uses the database file at the default location.
     */
    public Database() {
        
        this(DEFAULT_DATABASE_FILE_LOCATION);
        
    }
    
    /**
     * This constructor creates an instance that uses the database file found in
     * the specified location.
     * 
     * @param databaseFileLocation - The location of the database file that is 
     *  to be used
     */
    public Database(File databaseFileLocation) {
        
        initialize(databaseFileLocation);
        
        queue = new SQLiteQueue(databaseFileLocation).start();
        
    }
    
    /**
     * This method is called after creating the SQLite queue. It checks to see 
     * if the required tables are present in the database and creates them if 
     * not present
     * 
     * @param databaseFileLocation - The file where the database should be, this
     *  is needed because the queue is not used here. The queue is not used 
     *  because we do want to block execution here
     */
    private void initialize(File databaseFileLocation) {
        
        SQLiteConnection db = new SQLiteConnection(databaseFileLocation);
        
        try {
            
            db.open(true);
            
        } catch (SQLiteException ex) {
            
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, 
                    "Unable to open the database file", ex);
            
            System.exit(-1);
            
        }
        
        // Should change this to use "create table if not exists TableName", I 
        // am not familiar enough with SQLite yet to do this, but it is a far 
        // more elegant solution than this. I'm still just trying to get the 
        // create statements to work, when these work I can add the "if not 
        // exists" bits.
        if (tablesArePresent(db) == false) {
            
            createTables(db);
            
        }
        
        db.dispose();

    }
    
    private boolean tablesArePresent(SQLiteConnection db) {
        
        SQLiteStatement st = null;
        
        try {
            
            st = db.prepare("SELECT count(*) FROM sqlite_master WHERE type='table' AND name=?;");
            
            st.bind(1, "pigi.Repository").step();
            
            if (st.columnInt(0) != 1) {
                
                return false;
                
            }
            
        } catch (SQLiteException ex) {
            
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Unable to read from the database", ex);
            
            System.exit(-1);
            
        } finally {
            
            if (st != null) {
                
                st.dispose();
                
            }
            
        }
        
        return true;
        
    }
    
    private void createTables(SQLiteConnection db) {
        
        try {
            
            db.exec(CREATE_DATABASE_SQL);
            
        } catch (SQLiteException ex) {
            
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Unable to create the database tables", ex);
            
        }
        
    }
    
    // Maybe change this to use some .sql file with all of this information, not sure if this will work with sqlite4java though.
    private final static String CREATE_DATABASE_SQL =
        "BEGIN TRANSACTION; "
            + "CREATE TABLE Repository "
            + "("
            + "repositoryId INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "location CHARACTER VARYING NOT NULL,"
            + "title CHARACTER(255) NOT NULL,"
            + "description CHARACTER VARYING NOT NULL,"
            + "CONSTRAINT Repository_UC UNIQUE(location)"
            + ");"
            + "CREATE TABLE Revision"
            + "("
            + "revisionId INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "author CHARACTER(255) NOT NULL,"
            + "date TIMESTAMP NOT NULL,"
            + "number UNSIGNED INTEGER NOT NULL,"
            + "logMessage CHARACTER VARYING"
            + ");"
            + "CREATE TABLE Path"
            + "("
            + "\"value\" CHARACTER VARYING PRIMARY KEY NOT NULL,"
            + "type CHARACTER(1) NOT NULL"
            + ");"
            + "CREATE TABLE RepositoryHasRevision"
            + "("
            + "repositoryId INTEGER NOT NULL,"
            + "revisionId INTEGER NOT NULL,"
            + "PRIMARY KEY(revisionId, repositoryId)"
            + ");"
            + "CREATE TABLE RevisionHasPath"
            + "("
            + "revisionId INTEGER NOT NULL,"
            + "path CHARACTER VARYING NOT NULL,"
            + "PRIMARY KEY(revisionId, path)"
            + ");"
            + "ALTER TABLE RepositoryHasRevision ADD CONSTRAINT RepositoryHasRevision_FK1 FOREIGN KEY (repositoryId) REFERENCES Repository (repositoryId) ON DELETE RESTRICT ON UPDATE RESTRICT;"
            + "ALTER TABLE RepositoryHasRevision ADD CONSTRAINT RepositoryHasRevision_FK2 FOREIGN KEY (revisionId) REFERENCES Revision (revisionId) ON DELETE RESTRICT ON UPDATE RESTRICT;"
            + "ALTER TABLE RevisionHasPath ADD CONSTRAINT RevisionHasPath_FK1 FOREIGN KEY (revisionId) REFERENCES Revision (revisionId) ON DELETE RESTRICT ON UPDATE RESTRICT;"
            + "ALTER TABLE RevisionHasPath ADD CONSTRAINT RevisionHasPath_FK2 FOREIGN KEY (path) REFERENCES Path (\"value\") ON DELETE RESTRICT ON UPDATE RESTRICT;"
            + "COMMIT;";
    
}
