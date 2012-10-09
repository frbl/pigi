/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.hadoop;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import nl.rug.calculationservice.database.DAO.RepositoryDAO;
import nl.rug.calculationservice.database.cassandra.Cassandra;
import nl.rug.calculationservice.database.cassandra.DAO.RepositoryDAOCassandra;
import nl.rug.calculationservice.database.postgres.DAO.RepositoryDAOPostgres;
import nl.rug.calculationservice.database.model.Repository;

/**
 *
 * @author frbl
 */
public class MockHadoop implements IHadoop {

    /**
     * Get data from Cassandra Do something with the data Push it to postgres.
     */
    public MockHadoop() {

    }

    @Override
    public void performCalculation() {
        
        String url ="svn://renezRepository.org/repo";
        
        Repository repository = new Repository();

        //Cassandra DAO for reading
        RepositoryDAO cassandraRepositoryDAO = new RepositoryDAOCassandra();
        
        //Postgres DAO for writing
        RepositoryDAO postgresRepositoryDAO = new RepositoryDAOPostgres();
        try {
            
            repository.setDescription("Repository description in cassandra");
            repository.setName("ReneZdad Repository");
            repository.setUrl(url);
            
            cassandraRepositoryDAO.update(0,repository);
            
            repository = cassandraRepositoryDAO.findByUrl(url);
            
            System.out.println(repository);
            
            
            
            
        } catch (SQLException ex) {
            
            Logger.getLogger(MockHadoop.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
        

    }
}
