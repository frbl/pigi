/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.postgres.DAO;

import nl.rug.calculationservice.database.DAO.RevisionDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nl.rug.calculationservice.database.postgres.Database;
import nl.rug.calculationservice.database.model.Revision;

/**
 *
 * @author frbl
 */
public class RevisionDAOPostgres implements RevisionDAO{

    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Connection connection = null;
    
    @Override
    public void insert(Revision revision) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(int revisionPK, Revision revision) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(int revisionPK) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Revision> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Revision findByPrimaryKey(int revisionPK) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Revision> findByRepositoryId(int repositoryID) throws SQLException {
        
        connection = Database.getConnection();

        String queryString = "SELECT * FROM revision WHERE repository = ?";

        preparedStatement = connection.prepareStatement(queryString);
        
        preparedStatement.setInt(1, repositoryID);

        resultSet = preparedStatement.executeQuery();

        List<Revision> revisions = new ArrayList<Revision>();

        while (resultSet.next()) {
            
            Revision revision = new Revision();
            
            revision.setId(resultSet.getInt(Revision.ID_LOC));
            revision.setRepositoryId(resultSet.getInt(Revision.REPOSITORY_ID_LOC));
            revision.setRevisionNumber(resultSet.getInt(Revision.REVISION_NUMBER_LOC));
            revision.setAverageComplexity(resultSet.getInt(Revision.AVERAGE_COMPLEXITY_LOC));
            
            revisions.add(revision);

        }

        preparedStatement.close();

        connection.close();

        return revisions;
    }
    
}
