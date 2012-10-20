/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.hadoop;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.calculationservice.database.cassandra.DAO.RepositoryDAOCassandraImpl;
import nl.rug.calculationservice.database.DAO.RepositoryDAONoSql;
import nl.rug.calculationservice.database.cassandra.DAO.RevisionDAOCassandraImpl;
import nl.rug.calculationservice.database.DAO.RevisionDAONoSql;
import nl.rug.calculationservice.database.model.RepositoryNoSql;
import nl.rug.calculationservice.database.model.RevisionNoSql;

/**
 *
 * @author frbl
 */
public class MockHadoop implements IHadoop {

    private String url = "svn://renezRepository.org/repo";

    /**
     * Get data from Cassandra Do something with the data Push it to postgres.
     */
    public MockHadoop() {
    }

    @Override
    public void performCalculation() {

        RepositoryNoSql repository = new RepositoryNoSql();

        //Cassandra revision DAO for read/write
        RevisionNoSql cr = new RevisionNoSql();

        //Postgres DAO for writing
        RevisionDAONoSql cassandraRevisionDAO = new RevisionDAOCassandraImpl();

        //Cassandra repository DAO for read/write
        RepositoryDAONoSql cassandraRepositoryDAO = new RepositoryDAOCassandraImpl();

        try {

            repository = cassandraRepositoryDAO.findByUrl(url);

            System.out.println(repository);

            cassandraRevisionDAO.findComplexitiesForRevision(repository, 1);

            //postgresRepositoryDAO.insert(repository);

        } catch (SQLException ex) {

            Logger.getLogger(MockHadoop.class.getName()).log(Level.SEVERE, null, ex);

        }



    }

   
}
