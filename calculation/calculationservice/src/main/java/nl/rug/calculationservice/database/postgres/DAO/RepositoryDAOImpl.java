/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.postgres.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nl.rug.calculationservice.database.postgres.Database;
import nl.rug.calculationservice.database.postgres.model.Repository;

/**
 *
 * @author frbl
 */
public class RepositoryDAOImpl implements RepositoryDAO {

    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Connection connection = null;

    private void execute(String query) throws SQLException {

        connection = Database.getConnection();

        String queryString = query;

        preparedStatement = connection.prepareStatement(queryString);

    }

    private void close() throws SQLException {

        if (!preparedStatement.isClosed()) {
            preparedStatement.close();
        }

        if (!connection.isClosed()) {
            connection.close();
        }

    }

    @Override
    public void insert(Repository repository) throws SQLException {

        execute("INSERT INTO repository (name, url, description) VALUES(?,?,?)");
        
        preparedStatement.setString(1, repository.getName());
        
        preparedStatement.setString(2, repository.getUrl());
        
        preparedStatement.setString(3, repository.getDescription());
        
        preparedStatement.executeUpdate();
        
        close();

    }

    @Override
    public void update(int repositoryPK, Repository repository) throws SQLException {

        execute("UPDATE repository SET name=?, url=?, description=? WHERE id=?");

        preparedStatement.setString(1, repository.getName());

        preparedStatement.setString(2, repository.getUrl());

        preparedStatement.setString(3, repository.getDescription());

        preparedStatement.setInt(4, repository.getId());

        preparedStatement.executeUpdate();

        close();

    }

    @Override
    public void delete(int repositoryPK) throws SQLException {

        execute("DELETE FROM repository WHERE id=?");

        preparedStatement.setInt(1, repositoryPK);

        preparedStatement.executeUpdate();

        close();

    }

    @Override
    public List<Repository> findAll() throws SQLException {

        //TODO: See if this can be put into a separate function (together with the closing that is)
        execute("SELECT * FROM repository");
        
        resultSet = preparedStatement.executeQuery();

        List<Repository> repositories = new ArrayList<Repository>();

        RevisionDAO revisionDao = new RevisionDAOImpl();

        while (resultSet.next()) {

            Repository repository = new Repository();

            repository.setName(resultSet.getString(Repository.NAME_LOC));
            repository.setId(resultSet.getInt(Repository.ID_LOC));
            repository.setUrl(resultSet.getString(Repository.URL_LOC));
            repository.setDescription(resultSet.getString(Repository.DESCRIPTION_LOC));

            //TODO This is most probably not the best way to do this, as a new 
            //  connection to the database is opened in a loop..
            repository.setRevisions(revisionDao.findByRepositoryId(repository.getId()));

            repositories.add(repository);

        }

        close();

        return repositories;

    }

    @Override
    public Repository findByPrimaryKey(int repositoryPK) throws SQLException {

        execute("SELECT * FROM repository WHERE id = ");
        
        resultSet = preparedStatement.executeQuery();

        preparedStatement.setInt(1, repositoryPK);

        RevisionDAO revisionDao = new RevisionDAOImpl();

        Repository repository = new Repository();

        resultSet.first();

        repository.setName(resultSet.getString(Repository.NAME_LOC));
        repository.setId(resultSet.getInt(Repository.ID_LOC));
        repository.setUrl(resultSet.getString(Repository.URL_LOC));
        repository.setDescription(resultSet.getString(Repository.DESCRIPTION_LOC));

        repository.setRevisions(revisionDao.findByRepositoryId(repository.getId()));

        close();

        return repository;

    }

    @Override
    public Repository findByUrl(String url) throws SQLException {

        //TODO: See if this can be put into a separate function (together with the closing that is)
        execute("SELECT * FROM repository WHERE url = ");
        
        resultSet = preparedStatement.executeQuery();

        preparedStatement.setString(1, url);

        RevisionDAO revisionDao = new RevisionDAOImpl();

        Repository repository = new Repository();

        resultSet.first();

        repository.setName(resultSet.getString(Repository.NAME_LOC));
        repository.setId(resultSet.getInt(Repository.ID_LOC));
        repository.setUrl(resultSet.getString(Repository.URL_LOC));
        repository.setDescription(resultSet.getString(Repository.DESCRIPTION_LOC));

        repository.setRevisions(revisionDao.findByRepositoryId(repository.getId()));

        close();

        return repository;

    }
}
