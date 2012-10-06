package nl.rug.calculationservice.database.postgres;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author frbl
 */
public class Database {

    private static Database instance = new Database();
    
    private static String dbUrl = "jdbc:" + DatabaseSettings.DB_TYPE
            + "://" + DatabaseSettings.DB_HOST
            + ":" + DatabaseSettings.DB_PORT
            + "/" + DatabaseSettings.DB_NAME;

    private Database() {

        try {

            Class.forName(DatabaseSettings.DB_DRIVER);

        } catch (ClassNotFoundException ex) {

            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Database driver not found, exiting", ex);

        }


    }

    public static Connection getConnection() throws SQLException {

        Connection connection = DriverManager.getConnection(dbUrl, DatabaseSettings.DB_USER, DatabaseSettings.DB_PASSWORD);

        if (connection != null) {

            System.out.println("[Database] Database connection established.");

        } else {

            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Unable to access Database, exiting.");

        }

        return connection;

    }
}
