/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.cassandra.DAO;

import nl.rug.calculationservice.database.DAO.RepositoryDAONoSql;
import java.sql.SQLException;
import java.util.List;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import nl.rug.calculationservice.database.cassandra.Cassandra;
import nl.rug.calculationservice.database.cassandra.CassandraSettings;
import nl.rug.calculationservice.database.model.RepositoryNoSql;

/**
 *
 * @author frbl
 */
public class RepositoryDAOCassandraImpl implements RepositoryDAONoSql {

    private ColumnFamilyResult<String, String> result = null;
    
    private ColumnFamilyTemplate<String, String> template = new ThriftColumnFamilyTemplate<String, String>(Cassandra.getKeyspace(),
            CassandraSettings.REPOSITORY_COLUMN_FAMILY_NAME,
            StringSerializer.get(),
            StringSerializer.get());

    @Override
    public void insert(RepositoryNoSql repository) throws SQLException {

        ColumnFamilyUpdater<String, String> updater = template.createUpdater(repository.getUrl());

        updater.setString("name", repository.getName());
        updater.setString("description", repository.getDescription());
        updater.setLong("time", System.currentTimeMillis()); // Add time for the cassandra management

        template.update(updater);

    }

    @Override
    public void update(String URL, RepositoryNoSql repository) throws SQLException {

        // Inserting for now the same as updating
        insert(repository);

    }

    @Override
    public void delete(String url) throws SQLException {

        template.deleteRow(url);

    }

    @Override
    public List<RepositoryNoSql> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RepositoryNoSql findByUrl(String url) throws SQLException {

        RepositoryNoSql repository = new RepositoryNoSql();

        result = template.queryColumns(url);

        repository.setUrl(result.getKey());
        repository.setName(result.getString("name"));
        repository.setDescription(result.getString("description"));

        return repository;
        
    }
}
