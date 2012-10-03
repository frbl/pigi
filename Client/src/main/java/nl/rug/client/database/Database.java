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
 * primarily so we can access the database from different threads.
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

    public Repository getRepository(final String location) {

        return queue.execute(new SQLiteJob<Repository>() {
            protected Repository job(SQLiteConnection connection) throws SQLiteException {

                SQLiteStatement st = connection.prepare("SELECT repositoryId, title, location, description FROM Repository WHERE location=?");

                st.bind(1, location); // bind starts at 1...

                st.step(); // location is unique so we should have only one result.

                final int id = st.columnInt(0);
                final String title = st.columnString(0);
                final String location = st.columnString(1);
                final String description = st.columnString(2);

                return new Repository() {
                    public String getDescription() {
                        return description;
                    }

                    public String getLocation() {
                        return location;
                    }

                    public int getRepositoryId() {
                        return id;
                    }

                    public String getTitle() {
                        return title;
                    }
                };

            }
        }).complete(); // complete makes this blocking, see the sqlite4java site for an asynchronous example

    }

    public void addRepository(final String title, final String location, final String description /* id is automatically generated (if I did my work right ;)*/) {

        queue.execute(new SQLiteJob<Object>() {
            protected Repository job(SQLiteConnection connection) throws SQLiteException {

                SQLiteStatement st = connection.prepare("INSERT INTO Repository (location, title, description) VALUES (?, ?, ?)");

                st.bind(1, location); // bind starts at 1...
                st.bind(2, title);
                st.bind(3, description);

                st.step(); // WTF, how do you insert using a prepared statement, like this I guess...

                return null;

            }
        }).complete(); // complete makes this blocking, see the sqlite4java site for an asynchronous example

    }

    /**
     * This method is called after creating the SQLite queue. It checks to see
     * if the required tables are present in the database and creates them if
     * not present
     *
     * @param databaseFileLocation - The file where the database should be, this
     * is needed because the queue is not used here.
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
            "BEGIN TRANSACTION;"
            
            + "CREATE TABLE IF NOT EXISTS ChangedPath"
            + "("
            + "	path CHARACTER VARYING NOT NULL,"
            + "	type CHARACTER(1) NOT NULL,"
            + "	number UNSIGNED INTEGER NOT NULL,"
            + "	repository CHARACTER VARYING,"
            + "	PRIMARY KEY(path, repository, number),"
            + " FOREIGN KEY (repository, number) REFERENCES Revision (repository, number) ON DELETE RESTRICT ON UPDATE RESTRICT"
            + ");"

            + "CREATE TABLE IF NOT EXISTS Repository"
            + "("
            + "	URL CHARACTER VARYING"
            + "	name CHARACTER VARYING NOT NULL,"
            + "	description CHARACTER VARYING,"
            + " PRIMARY KEY(URL)"
            + ");"

            + "CREATE TABLE IF NOT EXISTS Revision"
            + "("
            + "	number UNSIGNED INTEGER NOT NULL,"
            + "	repository CHARACTER VARYING NOT NULL,"
            + "	author CHARACTER VARYING NOT NULL,"
            + "	\"date\" TIMESTAMP NOT NULL,"
            + "	PRIMARY KEY(repository, number)"
            + " FOREIGN KEY (repository) REFERENCES Repository (URL) ON DELETE RESTRICT ON UPDATE RESTRICT,"
            + ");"

            + "COMMIT WORK;";
    
}
