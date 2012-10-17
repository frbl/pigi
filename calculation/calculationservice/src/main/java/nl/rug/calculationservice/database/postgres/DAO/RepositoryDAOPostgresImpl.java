/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.postgres.DAO;

import nl.rug.calculationservice.database.DAO.RevisionDAORelational;
import nl.rug.calculationservice.database.DAO.RepositoryDAORelational;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nl.rug.calculationservice.database.model.RepositoryRelational;
import nl.rug.calculationservice.database.postgres.Database;

/**
 *
 * @author frbl
 */
public class RepositoryDAOPostgresImpl implements RepositoryDAORelational {

    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Connection connection = null;

    /**
     * Opens the database connection and prepares the statement to perform a
     * query. Note that the query is not yet executed. To open the query one
     * must call executeUpdate or executeQuery
     *
     * @param query
     * @throws SQLException
     */
    private void open(String query) throws SQLException {

        connection = Database.getConnection();

        String queryString = query;

        preparedStatement = connection.prepareStatement(queryString);

    }

    /**
     * Closes the connection to the database.
     *
     * @throws SQLException
     */
    private void close() throws SQLException {

        if (!preparedStatement.isClosed()) {
            preparedStatement.close();
        }

        if (!connection.isClosed()) {
            connection.close();
        }

    }

    @Override
    public void insert(RepositoryRelational repository) throws SQLException {

        open("INSERT INTO repository (name, url, description) VALUES(?,?,?)");

        preparedStatement.setString(1, repository.getName());

        preparedStatement.setString(2, repository.getUrl());

        preparedStatement.setString(3, repository.getDescription());

        preparedStatement.executeUpdate();

        close();

    }

    @Override
    public void update(int repositoryPK, RepositoryRelational repository) throws SQLException {

        open("UPDATE repository SET name=?, url=?, description=? WHERE id=?");

        preparedStatement.setString(1, repository.getName());

        preparedStatement.setString(2, repository.getUrl());

        preparedStatement.setString(3, repository.getDescription());

        preparedStatement.setInt(4, repositoryPK);

        preparedStatement.executeUpdate();

        close();

    }

    @Override
    public void delete(String url) throws SQLException {

        open("DELETE FROM repository WHERE url=?");

        preparedStatement.setString(1, url);

        preparedStatement.executeUpdate();

        close();

    }

    @Override
    public List<RepositoryRelational> findAll() throws SQLException {

        //TODO: See if this can be put into a separate function (together with the closing that is)
        open("SELECT * FROM repository");

        resultSet = preparedStatement.executeQuery();

        List<RepositoryRelational> repositories = new ArrayList<RepositoryRelational>();

        RevisionDAORelational revisionDao = new RevisionDAOPostgresImpl();

        while (resultSet.next()) {

            RepositoryRelational repository = new RepositoryRelational();

            repository.setName(resultSet.getString(RepositoryRelational.NAME_LOC));
            repository.setId(resultSet.getInt(RepositoryRelational.ID_LOC));
            repository.setUrl(resultSet.getString(RepositoryRelational.URL_LOC));
            repository.setDescription(resultSet.getString(RepositoryRelational.DESCRIPTION_LOC));

            //TODO This is most probably not the best way to do this, as a new 
            //  connection to the database is opened in a loop..
            repository.setRevisions(revisionDao.findByRepositoryId(repository.getId()));

            repositories.add(repository);

        }

        close();

        return repositories;

    }

    @Override
    public RepositoryRelational findByUrl(String url) throws SQLException {

        //TODO: See if this can be put into a separate function (together with the closing that is)
        open("SELECT * FROM repository WHERE url = ");

        resultSet = preparedStatement.executeQuery();

        preparedStatement.setString(1, url);

        RevisionDAORelational revisionDao = new RevisionDAOPostgresImpl();

        RepositoryRelational repository = new RepositoryRelational();

        resultSet.first();

        repository.setName(resultSet.getString(RepositoryRelational.NAME_LOC));
        repository.setId(resultSet.getInt(RepositoryRelational.ID_LOC));
        repository.setUrl(resultSet.getString(RepositoryRelational.URL_LOC));
        repository.setDescription(resultSet.getString(RepositoryRelational.DESCRIPTION_LOC));

        repository.setRevisions(revisionDao.findByRepositoryId(repository.getId()));

        close();

        return repository;

    }
}
