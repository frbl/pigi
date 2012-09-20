package nl.rug.client;

import fr.iscpif.jogl.JOGLWrapper;
import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import nl.rug.client.gui.MainWindow;
import nl.rug.client.gui.RepositoryView;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {

        // This needs to be done to make JOGL work correctly!
        JOGLWrapper.init();

        repoStuff();

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

    public static void listEntries(SVNRepository repository, String path) throws SVNException {
        Collection entries = repository.getDir(path, -1, null, (Collection) null);
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName()
                    + " ( author: '" + entry.getAuthor() + "'; revision: " + entry.getRevision()
                    + "; date: " + entry.getDate() + ")");
            if (entry.getKind() == SVNNodeKind.DIR) {
                listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
            }
        }
    }

    private static void repoStuff() {
//        DAVRepositoryFactory.setup();
//        String url = "https://subversion.assembla.com/svn/ReneZ/src/view/";
//        String name = "anonymous";
//        String password = "anonymous";
//
//        SVNRepository repository = null;
//        try {
//            repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
//            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
//            repository.setAuthenticationManager(authManager);
//
//            System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
//            System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));
//
//            SVNNodeKind nodeKind = repository.checkPath("", -1);
//            if (nodeKind == SVNNodeKind.NONE) {
//                System.err.println("There is no entry at '" + url + "'.");
//                System.exit(1);
//            } else if (nodeKind == SVNNodeKind.FILE) {
//                System.err.println("The entry at '" + url + "' is a file while a directory was expected.");
//                System.exit(1);
//            }
//            
//            listEntries(repository, "/src/view/");
//
//        } catch (SVNException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}