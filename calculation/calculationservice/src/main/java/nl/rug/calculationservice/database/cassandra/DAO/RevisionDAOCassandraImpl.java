package nl.rug.calculationservice.database.cassandra.DAO;

import nl.rug.calculationservice.database.DAO.RevisionDAONoSql;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.SliceQuery;
import nl.rug.calculationservice.database.cassandra.Cassandra;
import nl.rug.calculationservice.database.cassandra.CassandraSettings;
import nl.rug.calculationservice.database.model.RepositoryNoSql;
import nl.rug.calculationservice.database.model.RevisionNoSql;

/**
 *
 * @author frbl
 */
public class RevisionDAOCassandraImpl implements RevisionDAONoSql {

    private ColumnFamilyResult<String, Composite> result = null;
    private ColumnFamilyTemplate<String, Composite> template =
            new ThriftColumnFamilyTemplate<String, Composite>(
            Cassandra.getKeyspace(),
            CassandraSettings.REVISION_COLUMN_FAMILY_NAME,
            StringSerializer.get(),
            CompositeSerializer.get());

    @Override
    public void insert(RevisionNoSql revision) throws SQLException {

        RevisionNoSql cassandraRevision = (RevisionNoSql) revision;

        Composite key = new Composite();
        // The key in this case is the name of the column.
        key.addComponent(cassandraRevision.getRevision(), IntegerSerializer.get());
        key.addComponent(cassandraRevision.getFile(), StringSerializer.get());

        ColumnFamilyUpdater<String, Composite> updater = template.createUpdater(cassandraRevision.getUrl());

        updater.setInteger(key, cassandraRevision.getComplexity());

        template.update(updater);


    }

    @Override
    public void update(int revisionPK, RevisionNoSql revision) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(String url, String file, int revision) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Integer> findComplexitiesForRevision(RepositoryNoSql repository, int revision) throws SQLException {

        //The revisions is selected from the revision number and everything greaterthan,equal.
        // the reason for this is described on 
        // http://www.datastax.com/dev/blog/introduction-to-composite-columns-part-1
        Composite start = new Composite();
        start.addComponent(0, revision, Composite.ComponentEquality.EQUAL);

        Composite end = new Composite();
        end.addComponent(0, revision, Composite.ComponentEquality.GREATER_THAN_EQUAL);

        SliceQuery<String, Composite, Integer> sliceQuery = HFactory.createSliceQuery(Cassandra.getKeyspace(),
                StringSerializer.get(),
                CompositeSerializer.get(),
                IntegerSerializer.get());

        String key = repository.getUrl();

        sliceQuery.setColumnFamily(CassandraSettings.REVISION_COLUMN_FAMILY_NAME).setKey(key);

        ColumnSliceIterator<String, Composite, Integer> sliceIterator = new ColumnSliceIterator(sliceQuery, start, end, false);  //<- column count

        Iterator<HColumn<Composite, Integer>> iterator = sliceIterator;
        List<Integer> complexities = new ArrayList<Integer>();

        while (iterator.hasNext()) {
            HColumn<Composite, Integer> column = iterator.next();

            complexities.add(column.getValue());

            System.out.printf("Revision: %s File: %s Complexity: %s \n",
                    column.getName().get(0, IntegerSerializer.get()),
                    column.getName().get(1, StringSerializer.get()),
                    column.getValue());
        }

        return complexities;

    }
}
