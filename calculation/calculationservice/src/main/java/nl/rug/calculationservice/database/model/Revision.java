/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.model;

/**
 *
 * @author frbl
 */
public class Revision {
    
    public static final int ID_LOC = 1;
    public static final int REPOSITORY_ID_LOC = 2;
    public static final int REVISION_NUMBER_LOC = 3;
    public static final int AVERAGE_COMPLEXITY_LOC = 4;
    
    private int id = 0;
    private int repositoryId = 0;
    private int revisionNumber = 0;
    private int averageComplexity = 0;

    @Override
    public String toString() {
        return "ID: " + getId() +
               "\tRepository id: " + getRepositoryId() +
               "\tRevision number: " + getRevisionNumber() +
               "\tAverage complexity: " + getAverageComplexity(); 
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
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
     * @return the revisionNumber
     */
    public int getRevisionNumber() {
        return revisionNumber;
    }

    /**
     * @param revisionNumber the revisionNumber to set
     */
    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    /**
     * @return the averageComplexity
     */
    public int getAverageComplexity() {
        return averageComplexity;
    }

    /**
     * @param averageComplexity the averageComplexity to set
     */
    public void setAverageComplexity(int averageComplexity) {
        this.averageComplexity = averageComplexity;
    }
    
    
}
