/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.DAO;

import java.sql.SQLException;
import java.util.List;
import nl.rug.calculationservice.database.model.RepositoryRelational;

/**
 *
 * @author frbl
 */
public interface RepositoryDAORelational {
 	
        /**
     *
     * @param repository
     * @throws SQLException
     */
    public void insert(RepositoryRelational repository) throws SQLException;

 	/**
     *
     * @param repositoryPK
     * @param repository
     * @throws SQLException
     */
    public void update(int repositoryPK, RepositoryRelational repository) throws SQLException;
        
        /**
     *
     * @param url
     * @throws SQLException
     */
    public void delete(String url) throws SQLException;

 	/**
     *
     * @return
     * @throws SQLException
     */
    public List<RepositoryRelational> findAll() throws SQLException;
        
        /**
     *
     * @param url
     * @return
     * @throws SQLException
     */
    public RepositoryRelational findByUrl(String url) throws SQLException;
    
}
