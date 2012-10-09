/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.cassandra;

import java.util.Arrays;
import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;

/**
 *
 * @author frbl
 */
public class Cassandra {

    private static Cluster cluster = HFactory.getOrCreateCluster(CassandraSettings.CLUSTER_NAME, CassandraSettings.CLUSTER_ADDRESS);
    private static ColumnFamilyTemplate<String, String> template;

    private static Cassandra instance;
    
    private Cassandra() {

        KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(CassandraSettings.KEYSPACE_NAME);

        // If keyspace does not exist, the Column Families don't exist either.
        //  Create them.
        if (keyspaceDef == null) {

            createSchema();

        }

        Keyspace ksp = HFactory.createKeyspace(CassandraSettings.KEYSPACE_NAME, cluster);

        template = new ThriftColumnFamilyTemplate<String, String>(ksp,
                CassandraSettings.COLUMN_FAMILY_NAME,
                StringSerializer.get(),
                StringSerializer.get());

    }
    
    public static ColumnFamilyTemplate<String, String> getTemplate() {
        
        if(instance == null) {
            
           instance = new Cassandra();
           
        }
        
        return template;
        
    }

    private void createSchema() {


        // Create Keyspace
        ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(CassandraSettings.KEYSPACE_NAME,
                CassandraSettings.COLUMN_FAMILY_NAME,
                ComparatorType.BYTESTYPE);

        KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(CassandraSettings.KEYSPACE_NAME,
                ThriftKsDef.DEF_STRATEGY_CLASS,
                CassandraSettings.REPLICATION_FACTOR,
                Arrays.asList(cfDef));

        cluster.addKeyspace(newKeyspace, true);


        //Create columnfamily
        BasicColumnFamilyDefinition columnFamilyDefinition = new BasicColumnFamilyDefinition();
        columnFamilyDefinition.setKeyspaceName(CassandraSettings.KEYSPACE_NAME);
        columnFamilyDefinition.setName(CassandraSettings.COLUMN_FAMILY_NAME);
        columnFamilyDefinition.setKeyValidationClass(ComparatorType.UTF8TYPE.getClassName());
        columnFamilyDefinition.setComparatorType(ComparatorType.UTF8TYPE);

    }

    public boolean testCassandra() {

        try {
            

            ColumnFamilyResult<String, String> res;// = template.queryColumns("1");
            String value;// = res.getString("name");
            //System.out.println("Value: " + value);

            res = template.queryColumns("3");
            value = res.getString("name");
            System.out.println("Value: " + value);

            return true;

        } catch (HectorException e) {

            System.out.println(e.getMessage());

            return false;

        }

    }

}
