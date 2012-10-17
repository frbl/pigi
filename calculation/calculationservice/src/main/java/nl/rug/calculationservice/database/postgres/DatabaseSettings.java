package nl.rug.calculationservice.database.postgres;

/**
 * DatabaseSettings class. Should be replaced by the Maven settings
 */
public class DatabaseSettings {
    
    /**
     * The database driver to use. For Postgres this is org.postgresql.Driver
     */
    public static final String DB_DRIVER = "org.postgresql.Driver";
    
    /**
     * The database type used (mysql, postgresql etc.)
     */
    public static final String DB_TYPE = "postgresql";
    
    /**
     * The IP address of the database host.
     */
    public static final String DB_HOST = "192.168.1.20";
    
    /**
     * The port on which the database runs (Postgres is 5432)
     */
    public static final String DB_PORT = "5432";
    
    /**
     * The database name the application should use
     */
    public static final String DB_NAME = "pigi";
    
    /**
     * The username of the user accessing the DB
     */
    public static final String DB_USER = "**";
    
    /**
     * The password of the user accessing the DB
     */
    public static final String DB_PASSWORD = "**";
    
}
