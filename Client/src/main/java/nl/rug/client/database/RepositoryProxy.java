package nl.rug.client.database;

/**
 *
 * @author wesschuitema
 */
public class RepositoryProxy implements Repository {
    
    private final static String EMPTY_STRING = "";
    
    private Repository current = null;

    public String getDescription() {
        
        if (current == null) {
            
            return EMPTY_STRING;
            
        } else {
            
            return current.getDescription();
            
        }
        
    }

    public String getLocation() {
        
        if (current == null) {
            
            return EMPTY_STRING;
            
        } else {
            
            return current.getLocation();
            
        }
        
    }

    public int getRepositoryId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTitle() {
        
        if (current == null) {
            
            return EMPTY_STRING;
            
        } else {
            
            return current.getTitle();
            
        }
        
    }
    
}
