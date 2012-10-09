/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.cassandra.DAO;

import java.sql.SQLException;
import java.util.List;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import nl.rug.calculationservice.database.DAO.RepositoryDAO;
import nl.rug.calculationservice.database.cassandra.Cassandra;
import nl.rug.calculationservice.database.model.Repository;

/**
 *
 * @author frbl
 */
public class RepositoryDAOCassandra implements RepositoryDAO {

    private ColumnFamilyResult<String, String> result = null;
    
    private ColumnFamilyTemplate<String, String> template = null;
    
    
    @Override
    public void insert(Repository repository) throws SQLException {
       
        template = Cassandra.getTemplate();
        
        ColumnFamilyUpdater<String, String> updater = template.createUpdater(repository.getUrl());
        
        updater.setString("name", repository.getName());
        updater.setString("description", repository.getUrl());
        updater.setLong("time", System.currentTimeMillis()); // Add time for the cassandra management

        template.update(updater);

    }

    @Override
    public void update(int repositoryPK, Repository repository) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(int repositoryPK) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Repository> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Repository findByPrimaryKey(int repositoryPK) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Repository findByUrl(String url) throws SQLException {
        
        Repository repository = new Repository();
        
        result = Cassandra.getTemplate().queryColumns(url);
        
        repository.setUrl(result.getKey());
        repository.setName(result.getString("name"));
        repository.setDescription(result.getString("description"));
        
        return repository;
        
    }
}
