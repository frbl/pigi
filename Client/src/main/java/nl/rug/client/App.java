package nl.rug.client;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteException;
import fr.iscpif.jogl.JOGLWrapper;
import java.awt.Color;
import java.io.File;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import nl.rug.client.database.ChangedPath;
import nl.rug.client.database.Database;
import nl.rug.client.database.Repository;
import nl.rug.client.database.Revision;
import nl.rug.client.gui.MainWindow;
import nl.rug.client.repository.RepositoryImporter;
import org.tmatesoft.svn.core.SVNException;


/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
               
        // These need to be done to make JOGL and SQLite4Java work correctly (Maven/JNI)
        JOGLWrapper.init();
        SQLite.setLibraryPath("target/lib/");
        
        try {
            
            Database db = Database.getInstance();
            
            db.initialize();
            
        } catch (SQLiteException ex) {
            
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Unable to initialize Database, exiting.", ex);
            
            System.exit(-1);
            
        }
        
        String repositoryURL = "svn://devided.nl/wesenfrank";
        String username = "wes";
        String password = "xbox360";
        long startRevision = 0;
        long endRevision = -1; //HEAD
        try {
            
            /* testing if importing works */
            RepositoryImporter.importRepository(repositoryURL, username, password, startRevision, endRevision);
        
        } catch (SVNException ex) {
            
            Logger.getLogger(RepositoryImporter.class.getName()).log(Level.SEVERE, "error while creating an SVNRepository for the location '" + repositoryURL, ex);
            
            System.exit(-1);
            
        }

        initializeGUI();
        
    }

    private static void initializeGUI() {
        final JFrame frame = new MainWindow();
        frame.setSize(700, 700);
        frame.setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
    }
}
