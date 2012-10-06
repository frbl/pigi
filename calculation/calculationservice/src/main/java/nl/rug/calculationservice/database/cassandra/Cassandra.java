/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.database.cassandra;

import java.util.Arrays;
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
    
    Cluster cluster = HFactory.getOrCreateCluster(CassandraSettings.CLUSTER_NAME, CassandraSettings.CLUSTER_ADDRESS);
    
    private ColumnFamilyTemplate<String, String> template;

    public Cassandra() {
        
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

    private void createSchema() {

        ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(CassandraSettings.KEYSPACE_NAME,
                CassandraSettings.COLUMN_FAMILY_NAME,
                ComparatorType.BYTESTYPE);

        KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(CassandraSettings.KEYSPACE_NAME,
                ThriftKsDef.DEF_STRATEGY_CLASS,
                CassandraSettings.REPLICATION_FACTOR,
                Arrays.asList(cfDef));

        cluster.addKeyspace(newKeyspace, true);

    }
    
    
    public boolean testCassandra() {
        
         try {
            ColumnFamilyUpdater<String, String> updater = template.createUpdater("3");
            updater.setString("name", "Wes");
            updater.setLong("time", System.currentTimeMillis());

            template.update(updater);

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
    
    public ColumnFamilyTemplate<String, String> getTemplate() {
        
        return template;
        
    }
    
}
