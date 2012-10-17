/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.DAO;

import java.sql.SQLException;
import java.util.List;
import nl.rug.calculationservice.database.model.RevisionRelational;

/**
 *
 * @author frbl
 */
public interface RevisionDAORelational {

    /**
     *
     * @param revision
     * @throws SQLException
     */
    public void insert(RevisionRelational revision) throws SQLException;

    /**
     *
     * @param revisionPK
     * @param revision
     * @throws SQLException
     */
    public void update(int revisionPK, RevisionRelational revision) throws SQLException;

    /**
     *
     * @param revisionPK
     * @throws SQLException
     */
    public void delete(int revisionPK) throws SQLException;

    /**
     *
     * @return
     * @throws SQLException
     */
    public List<RevisionRelational> findAll() throws SQLException;

    /**
     *
     * @param revisionPK
     * @return
     * @throws SQLException
     */
    public RevisionRelational findByPrimaryKey(int revisionPK) throws SQLException;

    /**
     *
     * @param repositoryID
     * @return
     * @throws SQLException
     */
    public List<RevisionRelational> findByRepositoryId(int repositoryID) throws SQLException;
}
