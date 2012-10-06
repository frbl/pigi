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
        
        Repository repo = new Repository();
        repo.setURL("svn://devided.nl/wesenfrank");
        repo.setTitle("Devided.nl");
        repo.setDescription("The old school repository of Wes and Frank");
        repo.save();
        
        repo = Repository.findByURL("svn://devided.nl/wesenfrank");
        System.out.println("URL: " + repo.getURL());
        System.out.println("Name: " + repo.getTitle());
        System.out.println("Description: " + repo.getDescription());
        
        Revision revo = new Revision();
        revo.setRepository(repo);
        revo.setNumber(1L);
        revo.setAuthor("frbl");
        revo.setDate(new Date(System.currentTimeMillis()));
        revo.save();
        
        List<Revision> revisions = Revision.findByRepository(repo);
        System.out.println("Number of revisions: " + revisions.size());
        
        for (Revision revision : revisions) {
            
            System.out.println("Number: " + revision.getNumber());
            System.out.println("Author: " + revision.getAuthor());
            System.out.println("Data: " + revision.getDate());
            
        }
        
        ChangedPath cp = new ChangedPath();
        cp.setRevision(revo);
        cp.setPath("/x/y/awesomeclass.java");
        cp.setType(ChangedPath.Type.ADDED);
        cp.save();
        
        List<ChangedPath> cps = ChangedPath.findByRevision(revo);
        System.out.println("Number of ChangedPaths: " + cps.size());
        
        for (ChangedPath cpath :  cps) {
            
            System.out.println("Path: " + cpath.getPath());
            System.out.println("Type: " + cpath.getType());
            
        }
        
        System.out.println("Deleting created changed path");
        cp.delete();
        
        cps = ChangedPath.findByRevision(revo);
        System.out.println("Number of ChangedPaths: " + cps.size());
        
        System.out.println("Deleting created revision");
        
        revo.delete();
        
        revisions = Revision.findByRepository(repo);
        System.out.println("Number of revisions: " + revisions.size());
        
        repo.delete();

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
