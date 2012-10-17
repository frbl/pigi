/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.postgres.DAO;

import nl.rug.calculationservice.database.DAO.RevisionDAORelational;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nl.rug.calculationservice.database.model.RevisionRelational;
import nl.rug.calculationservice.database.postgres.Database;

/**
 *
 * @author frbl
 */
public class RevisionDAOPostgresImpl implements RevisionDAORelational{

    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Connection connection = null;
    
    @Override
    public void insert(RevisionRelational revision) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(int revisionPK, RevisionRelational revision) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(int revisionPK) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<RevisionRelational> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RevisionRelational findByPrimaryKey(int revisionPK) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<RevisionRelational> findByRepositoryId(int repositoryID) throws SQLException {
        
        connection = Database.getConnection();

        String queryString = "SELECT * FROM revision WHERE repository = ?";

        preparedStatement = connection.prepareStatement(queryString);
        
        preparedStatement.setInt(1, repositoryID);

        resultSet = preparedStatement.executeQuery();

        List<RevisionRelational> revisions = new ArrayList<RevisionRelational>();

        while (resultSet.next()) {
            
            RevisionRelational revision = new RevisionRelational();
            
            revision.setId(resultSet.getInt(RevisionRelational.ID_LOC));
            revision.setRepositoryId(resultSet.getInt(RevisionRelational.REPOSITORY_ID_LOC));
            revision.setRevisionNumber(resultSet.getInt(RevisionRelational.REVISION_NUMBER_LOC));
            revision.setAverageComplexity(resultSet.getInt(RevisionRelational.AVERAGE_COMPLEXITY_LOC));
            
            revisions.add(revision);

        }

        preparedStatement.close();

        connection.close();

        return revisions;
    }
    
}
