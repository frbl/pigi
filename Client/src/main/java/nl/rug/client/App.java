package nl.rug.client;

import fr.iscpif.jogl.JOGLWrapper;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Iterator;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
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
        JOGLWrapper.init();
        //GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities();
        GLCanvas canvas = new GLCanvas(caps);

        Frame frame = new Frame("AWT Window Test");
        frame.setSize(300, 300);
        frame.add(canvas);
        frame.setVisible(true);
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
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
}
