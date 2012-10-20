/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.cassandra;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nl.rug.calculationservice.database.DAO.RepositoryDAONoSql;
import nl.rug.calculationservice.database.DAO.RevisionDAONoSql;
import nl.rug.calculationservice.database.cassandra.DAO.RepositoryDAOCassandraImpl;
import nl.rug.calculationservice.database.cassandra.DAO.RevisionDAOCassandraImpl;
import nl.rug.calculationservice.database.model.RepositoryNoSql;
import nl.rug.calculationservice.database.model.RevisionNoSql;

/**
 *
 * @author frbl
 */
public class Generator {

    private RepositoryDAONoSql cassandraRepositoryDAO = new RepositoryDAOCassandraImpl();
    private RevisionDAONoSql cassandraRevisionDAO = new RevisionDAOCassandraImpl();

    public Generator() {
    }

    public void populate() throws SQLException {
        System.out.println("Generating data...");
        String url1 = "svn://renezRepository.org/repo";
        String url2 = "svn://wxwidgets.com/repo/trunk";

        RepositoryNoSql repository = new RepositoryNoSql();
        repository.setDescription("Repository description in cassandra");
        repository.setName("ReneZ Repository");
        repository.setUrl(url1);

        cassandraRepositoryDAO.update(repository.getUrl(), repository);

        repository.setDescription("wxwidgets description in cassandra");
        repository.setName("wxwidgets");
        repository.setUrl(url2);

        cassandraRepositoryDAO.update(repository.getUrl(), repository);
        
        

        RevisionNoSql cr = new RevisionNoSql();
        
        cr.setComplexity(1);
        cr.setFile("/file.java");
        cr.setRevision(1);
        cr.setUrl(url1);

        cassandraRevisionDAO.insert(cr);

        cr.setComplexity(8);
        cr.setFile("/tweedetest.java");
        cr.setRevision(1);
        cr.setUrl(url1);

        cassandraRevisionDAO.insert(cr);

        cr.setComplexity(3);
        cr.setFile("/filedrie.java");
        cr.setRevision(1);
        cr.setUrl(url1);

        cassandraRevisionDAO.insert(cr);
        
        cr.setComplexity(3);
        cr.setFile("/filedrie.java");
        cr.setRevision(2);
        cr.setUrl(url1);

        cassandraRevisionDAO.insert(cr);

        cr.setComplexity(321);
        cr.setFile("/file.java");
        cr.setRevision(2);
        cr.setUrl(url1);

        cassandraRevisionDAO.insert(cr);

        cr.setComplexity(321);
        cr.setFile("/file.java");
        cr.setRevision(3);
        cr.setUrl(url1);

        cassandraRevisionDAO.insert(cr);
        
        
        
        cr.setComplexity(8);
        cr.setFile("/widget.java");
        cr.setRevision(1);
        cr.setUrl(url2);

        cassandraRevisionDAO.insert(cr);

        cr.setComplexity(23);
        cr.setFile("/wx.java");
        cr.setRevision(1);
        cr.setUrl(url2);

        cassandraRevisionDAO.insert(cr);
        
        cr.setComplexity(3);
        cr.setFile("/wx.java");
        cr.setRevision(2);
        cr.setUrl(url2);

        cassandraRevisionDAO.insert(cr);

        cr.setComplexity(9);
        cr.setFile("/gui.java");
        cr.setRevision(1);
        cr.setUrl(url2);

        cassandraRevisionDAO.insert(cr);
        
        cr.setComplexity(6);
        cr.setFile("/directory.java");
        cr.setRevision(1);
        cr.setUrl(url2);

        cassandraRevisionDAO.insert(cr);

        cr.setComplexity(300);
        cr.setFile("/test.java");
        cr.setRevision(1);
        cr.setUrl(url2);

        cassandraRevisionDAO.insert(cr);
        
        cr.setComplexity(308);
        cr.setFile("/test.java");
        cr.setRevision(2);
        cr.setUrl(url2);

        cassandraRevisionDAO.insert(cr);

        cr.setComplexity(321);
        cr.setFile("/test.java");
        cr.setRevision(3);
        cr.setUrl(url2);

        cassandraRevisionDAO.insert(cr);
        
        System.out.println("Generating finished.");
        
    }
}
