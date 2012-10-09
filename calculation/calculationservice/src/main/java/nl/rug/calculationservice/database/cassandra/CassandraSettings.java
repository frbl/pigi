package nl.rug.calculationservice.database.cassandra;

/**
 * DatabaseSettings class. Should be replaced by the Maven settings
 */
public class CassandraSettings {
   
    public static final String CLUSTER_NAME ="Pigi Cluster";
    
    public static final String CLUSTER_ADDRESS = "192.168.10.210:9160";
    
    public static final int REPLICATION_FACTOR = 1;
    
    public static final String KEYSPACE_NAME = "ComplexityAnalysis";
    
    public static final String COLUMN_FAMILY_NAME = "Repositories";
    
}
