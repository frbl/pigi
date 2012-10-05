/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.postgres.DAO;

import java.sql.SQLException;
import java.util.List;
import nl.rug.calculationservice.database.postgres.model.Repository;

/**
 *
 * @author frbl
 */
public interface RepositoryDAO {
 	
        public void insert(Repository repository) throws SQLException;

 	public void update(int repositoryPK, Repository repository) throws SQLException;

 	public void delete(int repositoryPK) throws SQLException;

 	public List<Repository> findAll() throws SQLException;

 	public Repository findByPrimaryKey(int repositoryPK) throws SQLException;
        
        public Repository findByUrl(String url) throws SQLException;
    
}
