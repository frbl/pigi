package nl.rug.client;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.database.ChangedPath;
import nl.rug.client.database.Repository;
import nl.rug.client.database.Revision;
import nl.rug.client.model.Util;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Simple Repository model that does the bare minimum we need for a working
 * example.
 *
 * @author wesschuitema
 */
public class RepositoryModel {
    
    private static final Logger logger = Logger.getLogger(RepositoryModel.class.getName());

    private final String address;
    private final String username;
    private final String password;
    
    // this represents the database entity
    private Repository repository;
    
    // this represents the connection to the actual repository
    private SVNRepository repositoryConnection;

    public RepositoryModel(String address, String username, String password) {

        this.address = address;
        this.username = username;
        this.password = password;

    }

    /**
     * Checks to see if the repository is present in the database. If it is not
     * present, the database is created in the database and all information is
     * retrieved and stored. If the repository exists, a check is done to
     * determine if there are new revisions. If there is a new revision,
     * information in the database is updated.
     */
    public void initialize() throws SVNException {

        logger.log(Level.INFO, "Initializing RepositoryModel");
        
        // this one first since we want to know as soon as possible if we can connect to the repository
        initializeRepositoryConnection();

        repository = Repository.findByURL(getAddress());

        if (repository == null) {
            
            logger.log(Level.INFO, "Repository {0} did not exist, creating", getAddress());

            repository = new Repository();
            repository.setURL(getAddress());
            /* 
             * originally this would be done somewhere in a GUI, but we don't 
             * need it, so we just put something there 
             */
            repository.setTitle("Some generic title");
            repository.setDescription("Some generic description");
            repository.save();

        }

        long lastRevisionInDatabase = determineLastRevisonInDatabase();

        long lastRevisionInRepository = determineLastRevisionInRepository();

        update(lastRevisionInDatabase, lastRevisionInRepository);

    }
    
    private void initializeRepositoryConnection() throws SVNException {
        
        if (getAddress().startsWith("svn://")) {
            
            SVNRepositoryFactoryImpl.setup();
            
        } else { // assume it is http
            
            DAVRepositoryFactory.setup();
            
        }
        
        repositoryConnection = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(getAddress()));
        
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(getUsername(), getPassword());
        
        repositoryConnection.setAuthenticationManager(authManager);
        
    }

    private long determineLastRevisonInDatabase() {

        Revision lastRevision = Revision.findLatest(repository);

        long number = lastRevision == null ? 0 : lastRevision.getNumber();

        return number;

    }

    private long determineLastRevisionInRepository() throws SVNException {

        long number = repositoryConnection.getLatestRevision();

        return number;

    }

    private void update(long startRevision, long endRevision) throws SVNException {
        
        if (startRevision == endRevision) {
            
            logger.log(Level.INFO, "Database information is up to date for {0}", repository.getURL());
            
            return;
            
        }
        
        logger.log(Level.INFO, "Updating database information for {0} from revision {1} to {2}", new Object[]{getAddress(), startRevision, endRevision});
        
        Collection logEntries = repositoryConnection.log(new String[]{""}, null, startRevision, endRevision, true, true);

        for (Iterator entries = logEntries.iterator(); entries.hasNext();) {

            SVNLogEntry logEntry = (SVNLogEntry) entries.next();
            
            long number = logEntry.getRevision();
            String author = logEntry.getAuthor();
            
            if (author == null) {
                
                logger.log(Level.INFO, "Revision {0} does not have an author, setting it to Anonymous", number);
                
                author = "Anonymous";
                
            }
            
            String message = logEntry.getMessage();
            Date date = new Date(logEntry.getDate().getTime());
            
            logger.log(Level.INFO, "Saving revision {0}", number);
            
            Revision revision = new Revision();
            revision.setNumber(number);
            revision.setAuthor(author);
            revision.setLogMessage(message);
            revision.setDate(date);
            revision.setRepository(repository);
            revision.save();
            
            if (logEntry.getChangedPaths().size() > 0) {

                Set changedPathsSet = logEntry.getChangedPaths().keySet();

                for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {

                    SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
                    
                    String path = entryPath.getPath();
                    char type = entryPath.getType();
                    String hash = Util.getHash(path + revision.getNumber());
                    
                    logger.log(Level.INFO, "Saving changed path {0} which had type {1} with hash {2}", new Object[]{path, type, hash});
                    
                    ChangedPath changedPath = new ChangedPath();
                    changedPath.setPath(path);
                    changedPath.setType(type);
                    changedPath.setRevision(revision);
                    changedPath.setComplexity(-1); // -1 is when the complexity is yet to be determined
                    changedPath.setHash(hash);
                    changedPath.save();
                    
                }
                
            }
            
        }
        
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
}
