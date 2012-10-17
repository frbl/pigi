/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.DAO;

import java.sql.SQLException;
import java.util.List;
import nl.rug.calculationservice.database.model.RepositoryNoSql;

/**
 *
 * @author frbl
 */
public interface RepositoryDAONoSql {

    /**
     * Insert a new repository into the noSQL database
     *
     * @param repository the repository to insert
     * @throws SQLException when something went wrong while inserting
     */
    public void insert(RepositoryNoSql repository) throws SQLException;

    /**
     * Update an existing repository in the noSQL database. For now this
     * function is the same as the insert function.
     *
     * @param repository the repository to update.
     * @param URL the url of the repository to update.
     * @throws SQLException when something went wrong while updating
     */
    public void update(String URL, RepositoryNoSql repository) throws SQLException;

    /**
     * Deletes a repository from the database. 
     * 
     * @param url the url of the repository which should be deleted
     * @throws SQLException when the deleting was unsuccessful 
     */
    public void delete(String url) throws SQLException;

    /**
     * Returns all repositories in the database. 
     * 
     * @return a list of all repositories in the database
     * @throws SQLException when something went wrong
     */
    public List<RepositoryNoSql> findAll() throws SQLException;

    /**
     * Find a repository according to its unique URL.
     * 
     * @param url the url of the repository to find
     * @return the found repository
     * @throws SQLException when something went wrong
     */
    public RepositoryNoSql findByUrl(String url) throws SQLException;
    
}
