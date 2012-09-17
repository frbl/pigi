package nl.rug.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        DAVRepositoryFactory.setup();
        String url = "http://svn.svnkit.com/repos/svnkit/trunk/doc";
        String name = "anonymous";
        String password = "anonymous";

        SVNRepository repository = null;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
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

        } catch (SVNException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

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
