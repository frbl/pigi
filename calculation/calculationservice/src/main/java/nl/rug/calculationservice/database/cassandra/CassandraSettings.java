package nl.rug.calculationservice.database.cassandra;

/**
 * DatabaseSettings class. Should be replaced by the Maven settings
 */
public class CassandraSettings {

    /**
     * The name of the cluster the application should connect to.
     */
    public static final String CLUSTER_NAME = "Pigi Cluster";
    /**
     * The RPC port of the cluster to connect to
     */
    public static final String CLUSTER_RPCPORT = "9160";
    /**
     * The IP of one of the nodes in this cluster.
     */
    public static final String CLUSTER_ADDRESS = "192.168.10.210";
    /**
     * The replication factor used in the cluster.
     */
    public static final int REPLICATION_FACTOR = 2;
    /**
     * The name of the keyspace used (in a relational world this is your
     * database name)
     */
    public static final String KEYSPACE_NAME = "ComplexityAnalysis";
    /**
     * The column family name of the repositories 'table'/column family
     */
    public static final String REPOSITORY_COLUMN_FAMILY_NAME = "Repositories";
    /**
     * The column family name of the revisions 'table'/column family
     */
    public static final String REVISION_COLUMN_FAMILY_NAME = "Revisions";

    /**
     * TEST column family name
     */
    public static String TEST_COLUMN_FAMILY_NAME() {
        System.out.println("[DEBUG] Using test column family..");
        return "Test";
    }
}
