/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.client.repository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.App;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 *
 * @author Rene
 */
public final class MySVNRepository implements Repository {
    
    SVNRepository repository;
    
    public MySVNRepository(String svnUrl, String path){
        this(svnUrl, path, "anonymous", "anonymous");
    }
    
    public MySVNRepository(String svnUrl, String path, String username, String password){
        initRepository(svnUrl, path, username, password);
    }

    public void initRepository(String svnUrl, String path, String username, String password){
        DAVRepositoryFactory.setup();
        //String url = "https://subversion.assembla.com/svn/ReneZ";
        //String path = "src/";
        //String url = "http://svn.wxwidgets.org/svn/wx/wxWidgets/";
        //String name = "anonymous";
        //String password = "anonymous";

        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(svnUrl));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
            repository.setAuthenticationManager(authManager);

            //System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
            //System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));
            
            
            SVNNodeKind nodeKind = repository.checkPath("", -1);
            if (nodeKind == SVNNodeKind.NONE) {
                System.err.println("There is no entry at '" + svnUrl + "'.");
                System.exit(1);
            } else if (nodeKind == SVNNodeKind.FILE) {
                System.err.println("The entry at '" + svnUrl + "' is a file while a directory was expected.");
                System.exit(1);
            }
            
            //for(int i = 1; i <= repository.getLatestRevision(); i++){
            //    retrieveFile(path, i);
            //    System.out.println("Retrieved revision " + i + " of " + path);
            //}

        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void retrieveFile(String path, int revision) {
        try {
            final String FILE_SEPARATOR = System.getProperty("file.separator");
            
            if(repository.checkPath(path, revision) == SVNNodeKind.NONE) return;
            Collection entries = repository.getDir(repository.getRepositoryPath(path), revision, null, (Collection) null);
            Iterator iterator = entries.iterator();
            String localpath = "svnfiles" + FILE_SEPARATOR + "ReneZ" + FILE_SEPARATOR + revision + FILE_SEPARATOR + path;
            File svnpath = new File(localpath);
            svnpath.mkdirs();
            while (iterator.hasNext()) {
                SVNDirEntry entry = (SVNDirEntry) iterator.next();
                String filePath = (path.equals("") || path.endsWith("/") ? path : path + "/") + entry.getName();
                if (entry.getKind() == SVNNodeKind.DIR) {
                    System.out.println("Going into " + filePath);
                    retrieveFile(filePath, revision);
                } else if (entry.getKind() == SVNNodeKind.FILE && entry.getRevision() == revision) {
                    File localsvnfile = new File(localpath + FILE_SEPARATOR + entry.getName());
                    localsvnfile.createNewFile();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    OutputStream outputStream = new FileOutputStream(localsvnfile);
                    repository.getFile(filePath, entry.getRevision(), new SVNProperties(), baos);
                    baos.writeTo(outputStream);
                    System.out.println("/" + filePath
                        + " ( author: '" + entry.getAuthor() + "'; revision: " + entry.getRevision()
                        + "; date: " + entry.getDate() + ")");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MySVNRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
