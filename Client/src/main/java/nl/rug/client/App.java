package nl.rug.client;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
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
        }

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
}
