/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.postgres.DAO;

import java.sql.SQLException;
import java.util.List;
import nl.rug.calculationservice.database.postgres.model.Revision;

/**
 *
 * @author frbl
 */
public interface RevisionDAO {

    public void insert(Revision revision) throws SQLException;

    public void update(int revisionPK, Revision revision) throws SQLException;

    public void delete(int revisionPK) throws SQLException;

    public List<Revision> findAll() throws SQLException;

    public Revision findByPrimaryKey(int revisionPK) throws SQLException;

    public List<Revision> findByRepositoryId(int repositoryID) throws SQLException;
}
