package nl.rug.client.database;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteQueue;
import java.io.File;

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
    private final static File DEFAULT_DATABASE_FILE_LOCATION = new File("client.db"); // default location

    /**
     * Default constructor, uses the database file at the default location.
     */
    public Database() throws SQLiteException {

        this(DEFAULT_DATABASE_FILE_LOCATION);

    }

    /**
     * This constructor creates an instance that uses the database file found in
     * the specified location.
     *
     * @param databaseFileLocation - The location of the database file that is
     * to be used
     */
    public Database(File databaseFileLocation) throws SQLiteException {

        initialize(databaseFileLocation);

        queue = new SQLiteQueue(databaseFileLocation).start();

    }

    /**
     * This method is called after creating the SQLite queue. It checks to see
     * if the required tables are present in the database and creates them if
     * not present
     *
     * @param databaseFileLocation - The file where the database should be, this
     * is needed because the queue is not used here. The queue is not used
     * because we do want to block execution here
     */
    private void initialize(File databaseFileLocation) throws SQLiteException {

        SQLiteConnection db = new SQLiteConnection(databaseFileLocation);

        try {

            db.open(true);

            db.exec(CREATE_DATABASE_SQL);

        } finally {

            db.dispose();

        }

    }
    // Maybe change this to use some .sql file with all of this information
    private final static String CREATE_DATABASE_SQL =
            "BEGIN TRANSACTION; "
            + "CREATE TABLE IF NOT EXISTS Repository"
            + "("
            + "repositoryId INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "location CHARACTER VARYING NOT NULL,"
            + "title CHARACTER(255) NOT NULL,"
            + "description CHARACTER VARYING NOT NULL,"
            + "CONSTRAINT Repository_UC UNIQUE(location)"
            + ");"
            + "CREATE TABLE IF NOT EXISTS Revision"
            + "("
            + "revisionId INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "author CHARACTER(255) NOT NULL,"
            + "date TIMESTAMP NOT NULL,"
            + "number UNSIGNED INTEGER NOT NULL,"
            + "logMessage CHARACTER VARYING"
            + ");"
            + "CREATE TABLE IF NOT EXISTS Path"
            + "("
            + "\"value\" CHARACTER VARYING PRIMARY KEY NOT NULL,"
            + "type CHARACTER(1) NOT NULL"
            + ");"
            + "CREATE TABLE IF NOT EXISTS RepositoryHasRevision"
            + "("
            + "repositoryId INTEGER NOT NULL,"
            + "revisionId INTEGER NOT NULL,"
            + "PRIMARY KEY(revisionId, repositoryId),"
            + "FOREIGN KEY (revisionId) REFERENCES Revision (revisionId) ON DELETE RESTRICT ON UPDATE RESTRICT,"
            + "FOREIGN KEY (repositoryId) REFERENCES Repository (repositoryId) ON DELETE RESTRICT ON UPDATE RESTRICT"
            + ");"
            + "CREATE TABLE IF NOT EXISTS RevisionHasPath"
            + "("
            + "revisionId INTEGER NOT NULL,"
            + "path CHARACTER VARYING NOT NULL,"
            + "PRIMARY KEY(revisionId, path),"
            + "FOREIGN KEY (revisionId) REFERENCES Revision (revisionId) ON DELETE RESTRICT ON UPDATE RESTRICT,"
            + "FOREIGN KEY (path) REFERENCES Path (\"value\") ON DELETE RESTRICT ON UPDATE RESTRICT"
            + ");"
            + "COMMIT;";
}
