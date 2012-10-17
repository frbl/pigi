/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.DAO;

import java.sql.SQLException;
import java.util.List;
import nl.rug.calculationservice.database.model.RepositoryNoSql;
import nl.rug.calculationservice.database.model.RevisionNoSql;

/**
 *
 * @author frbl
 */
public interface RevisionDAONoSql {

    /**
     * Inserts a new revision into the database.
     *
     * @param revision the revision to insert
     * @throws SQLException if something goes wrong
     */
    public void insert(RevisionNoSql revision) throws SQLException;

    /**
     * This function is not implemented. It is not sure if it has to be
     * implemented, as the use of it is not really clear.
     */
    public void update(int revisionPK, RevisionNoSql revision) throws SQLException;

    /**
     * Deletes a revision from the database. Note that this might have unwanted
     * side effects. Some elegant defaulting should be done here, instead of the
     * plain removing of the revision.
     *
     * @param url the url for which a revision needs to be deleted
     * @param file the file for which a revision needs to be deleted
     * @param revision the revision to be deleted
     * @throws SQLException if something goes wrong
     */
    public void delete(String url, String file, int revision) throws SQLException;

    /**
     * Finds a list of complexities for all files in a certain repository with a
     * certain revision.
     *
     * @param repository the repository from which to use the revisions from
     * @param revision the revision to retrieve the complexities for
     * @return a list with complexities for all files in the repository in that
     * revision
     * @throws SQLException if something goes wrong
     */
    public List<Integer> findComplexitiesForRevision(RepositoryNoSql repository, int revision) throws SQLException;
}
