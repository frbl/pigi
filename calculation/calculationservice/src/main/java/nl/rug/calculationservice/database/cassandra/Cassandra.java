/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.cassandra;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

/**
 *
 * @author frbl
 */
public class Cassandra {

    private static Cluster cluster = HFactory.getOrCreateCluster(
            CassandraSettings.CLUSTER_NAME,
            CassandraSettings.CLUSTER_ADDRESS);
    
    private static Cassandra instance;
    private static Keyspace keyspace;

    private Cassandra() {

        KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(CassandraSettings.KEYSPACE_NAME);

        // If keyspace does not exist, the Column Families don't exist either.
        //  Create them.
        if (keyspaceDef == null) {

            createSchema();

        }

        ConfigurableConsistencyLevel configurableConsistencyLevel = new ConfigurableConsistencyLevel();
        Map<String, HConsistencyLevel> clmap = new HashMap<String, HConsistencyLevel>();

        // Define CL.ONE for ColumnFamily "MyColumnFamily"
        clmap.put(CassandraSettings.REPOSITORY_COLUMN_FAMILY_NAME, HConsistencyLevel.ONE);
        clmap.put(CassandraSettings.REVISION_COLUMN_FAMILY_NAME, HConsistencyLevel.ONE);

        // In this we use CL.ONE for read and writes. But you can use different CLs if needed.
        configurableConsistencyLevel.setReadCfConsistencyLevels(clmap);
        configurableConsistencyLevel.setWriteCfConsistencyLevels(clmap);

        keyspace = HFactory.createKeyspace(CassandraSettings.KEYSPACE_NAME, cluster);



    }

    /**
     * Singleton-like approach to get the keyspace to the other classes.
     *
     * @return the connection to the keyspace
     */
    public static Keyspace getKeyspace() {

        if (instance == null) {

            instance = new Cassandra();

        }

        return keyspace;

    }

    /**
     * This function creates the basic schema of the cassandra database. It is
     * only called whenever there is no schema detected on boot.
     */
    private void createSchema() {


        // Create Keyspace
        ColumnFamilyDefinition repositoryCf = HFactory.createColumnFamilyDefinition(CassandraSettings.KEYSPACE_NAME,
                CassandraSettings.REPOSITORY_COLUMN_FAMILY_NAME,
                ComparatorType.BYTESTYPE);

        ColumnFamilyDefinition revisionCf = HFactory.createColumnFamilyDefinition(CassandraSettings.KEYSPACE_NAME,
                CassandraSettings.REVISION_COLUMN_FAMILY_NAME,
                ComparatorType.BYTESTYPE);

        KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(CassandraSettings.KEYSPACE_NAME,
                ThriftKsDef.DEF_STRATEGY_CLASS,
                CassandraSettings.REPLICATION_FACTOR,
                Arrays.asList(repositoryCf, revisionCf));

        cluster.addKeyspace(newKeyspace, true);



        //Create both columnfamilies
        BasicColumnFamilyDefinition repositoryColumnFamilyDefinition = new BasicColumnFamilyDefinition();
        repositoryColumnFamilyDefinition.setKeyspaceName(CassandraSettings.KEYSPACE_NAME);
        repositoryColumnFamilyDefinition.setName(CassandraSettings.REPOSITORY_COLUMN_FAMILY_NAME);
        repositoryColumnFamilyDefinition.setKeyValidationClass(ComparatorType.UTF8TYPE.getClassName());
        repositoryColumnFamilyDefinition.setComparatorType(ComparatorType.UTF8TYPE);

        BasicColumnFamilyDefinition revisionColumnFamilyDefinition = new BasicColumnFamilyDefinition();
        revisionColumnFamilyDefinition.setKeyspaceName(CassandraSettings.KEYSPACE_NAME);
        revisionColumnFamilyDefinition.setName(CassandraSettings.REVISION_COLUMN_FAMILY_NAME);
        revisionColumnFamilyDefinition.setKeyValidationClass(ComparatorType.UTF8TYPE.getClassName());
        revisionColumnFamilyDefinition.setComparatorType(ComparatorType.COMPOSITETYPE);
    }
}
