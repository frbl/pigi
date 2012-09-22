package nl.rug.client;

import com.almworks.sqlite4java.SQLite;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import fr.iscpif.jogl.JOGLWrapper;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import nl.rug.client.gui.MainWindow;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;


/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        /*DAVRepositoryFactory.setup();
        String url = "https://subversion.assembla.com/svn/ReneZ/";
        String name = "anonymous";
        String password = "anonymous";

        try {
            SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
            repository.setAuthenticationManager(authManager);

            System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
            System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));

            SVNNodeKind nodeKind = repository.checkPath("", -1);
            if (nodeKind == SVNNodeKind.NONE) {
                System.err.println("There is no entry at '" + url + "'.");
                System.exit(1);
            } else if (nodeKind == SVNNodeKind.FILE) {
                System.err.println("The entry at '" + url + "' is a file while a directory was expected.");
                System.exit(1);
            }
            
            for(int i = 1; i <= repository.getLatestRevision(); i++){
                listEntries(repository, i, "");
            }

        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
*/
        // These need to be done to make JOGL and SQLite4Java work correctly (Maven/JNI)
        JOGLWrapper.init();
        SQLite.setLibraryPath("target/lib/");


        //TODO Move database stuff away from here, remember to let the database connection run on its own thread as described in the sqlite4java getting started page
        File database = new File("client.db");

        SQLiteConnection db = new SQLiteConnection(database);
        try {
            db.open(true);
            // for some reason creating the table from the terminal makes it unreadable: [file is encrypted or is not a database] (?)
            db.exec("CREATE TABLE orders (order_id INTEGER PRIMARY KEY, quantity INTEGER)");
        } catch (SQLiteException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Could not connect to the database", ex);
        }

        SQLiteStatement st = null;

        try {
            st = db.prepare("SELECT order_id FROM orders WHERE quantity >= ?");
            // first param replaces the '?' in the statement with the value of the second param
            st.bind(1, 1);
            while (st.step()) {
                System.out.println(st.columnLong(0));
            }

        } catch (SQLiteException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Error while executing query", ex);
        } finally {

            if (st != null) {
                st.dispose();
            }

        }

        db.dispose();

        initializeGUI();

    }

    public static void listEntries(SVNRepository repository, int revision, String path) throws Exception {
        Collection entries = repository.getDir(path, revision, null, (Collection) null);
        Iterator iterator = entries.iterator();
        String localpath = "svnfiles\\ReneZ\\" + revision + "\\" + path;
        File svnpath = new File(localpath);
        svnpath.mkdirs();
        
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            String filePath = (path.equals("") ? "" : path + "/") + entry.getName();
            System.out.println("/" + filePath
                    + " ( author: '" + entry.getAuthor() + "'; revision: " + entry.getRevision()
                    + "; date: " + entry.getDate() + ")");
            if (entry.getKind() == SVNNodeKind.DIR) {
                listEntries(repository, revision, filePath);
            } else if (entry.getKind() == SVNNodeKind.FILE) {
                File localsvnfile = new File(localpath + "\\" + entry.getName());
                localsvnfile.createNewFile();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                OutputStream outputStream = new FileOutputStream(localsvnfile);
                repository.getFile(entry.getName(), entry.getRevision(), new HashMap(), baos);
                baos.writeTo(outputStream);
            }
        }
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
