package nl.rug.client;

import nl.rug.client.database.Database;
import nl.rug.client.database.Repository;
import nl.rug.client.database.Revision;

/**
 * Simple Repository model that does the bare minimum we need for a working 
 * example.
 * 
 * @author wesschuitema
 */
public class RepositoryModel {
    
    private final Database database;
    
    private final String address;
    private final String username;
    private final String password;
    
    // this represents the database entity
    private Repository repository;
    
    public RepositoryModel(String address, String username, String password) {
        
        this.database = Database.getInstance();
        
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
    public void initialize() {
        
        repository = Repository.findByURL(address); 
        
        if (repository ==  null) {
            
            repository = new Repository();
            repository.setURL(address);            
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

    private long determineLastRevisonInDatabase() {
        
        Revision lastRevision = Revision.findLatest(repository);
        
        return lastRevision == null ? 0 : lastRevision.getNumber();
        
    }

    private long determineLastRevisionInRepository() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private void update(long startRevision, long endRevision) {
        
        // for every revision between start and end, fetch changeset, inster into database
        
    }
    
}
