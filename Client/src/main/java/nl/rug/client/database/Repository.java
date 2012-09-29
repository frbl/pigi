package nl.rug.client.database;

import java.util.List;

/**
 *
 * @author wesschuitema
 */
public class Repository {
    
    public static Repository findById(int repositoryId) {
        
        throw new UnsupportedOperationException("Not yet implemented");
        
    }
    
    public static Repository findByLocation(String location) {
        
        throw new UnsupportedOperationException("Not yet implemented");
        
    }

    private int repositoryId;
    private String location;
    private String title;
    private String description;
    
    public List<Revision> getRevisions() {
        
        throw new UnsupportedOperationException("Not yet implemented");
        
    }

    /**
     * @return the repositoryId
     */
    public int getRepositoryId() {
        return repositoryId;
    }

    /**
     * @param repositoryId the repositoryId to set
     */
    public void setRepositoryId(int repositoryId) {
        this.repositoryId = repositoryId;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
