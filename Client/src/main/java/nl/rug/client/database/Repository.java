package nl.rug.client.database;

/**
 *
 * @author wesschuitema
 */
public interface Repository {

    /**
     * @return the description
     */
    String getDescription();

    /**
     * @return the location
     */
    String getLocation();

    /**
     * @return the repositoryId
     */
    int getRepositoryId();

    /**
     * @return the title
     */
    String getTitle();

}
