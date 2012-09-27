package nl.rug.client;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteException;
import fr.iscpif.jogl.JOGLWrapper;
import java.awt.Color;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import nl.rug.client.database.Database;
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
        
        Database database = null;
        
        try {
            
            database = new Database(new File("client.db"));
            
        } catch (SQLiteException ex) {
            
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Unable to access Database, exiting.", ex);
            
            System.exit(-1);
            
        }
        
        database.addRepository("test", "svn://test.nl/awesomerepository", "The best repository ever!!1");
        System.out.println(database.getRepository("svn://test.nl/awesomerepository").getDescription());

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
