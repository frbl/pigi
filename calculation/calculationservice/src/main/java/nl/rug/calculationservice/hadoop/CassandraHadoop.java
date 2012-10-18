package nl.rug.calculationservice.hadoop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.calculationservice.database.cassandra.CassandraSettings;
import nl.rug.calculationservice.hadoop.mapreduce.AverageMapper;
import nl.rug.calculationservice.hadoop.mapreduce.AverageReducer;
import org.apache.cassandra.hadoop.ColumnFamilyInputFormat;
import org.apache.cassandra.hadoop.ConfigHelper;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 *
 * @author frbl
 */
public class CassandraHadoop extends Configured implements IHadoop, Tool {

    public static void main(String[] args) throws Exception {
        // Let ToolRunner handle generic command-line options
        ToolRunner.run(new Configuration(), new CassandraHadoop(), args);
        System.exit(0);
    }

    /**
     * Empty constructor is needed for hadoop/jar
     */
    public CassandraHadoop() {
    }

    @Override
    public void performCalculation() {

        try {

            Job job = new Job(getConf(), "averagecomplexity");


            job.setJarByClass(CassandraHadoop.class);

            job.setMapperClass(AverageMapper.class);

            job.setCombinerClass(AverageReducer.class);
            job.setReducerClass(AverageReducer.class);


            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(IntWritable.class);
            job.setOutputKeyClass(ByteBuffer.class);
            job.setOutputValueClass(List.class);

            FileOutputFormat.setOutputPath(job, new Path("/usr/frank/output"));

            job.setInputFormatClass(ColumnFamilyInputFormat.class);

            String revision = "1";

            SlicePredicate predicate = new SlicePredicate().setColumn_names(Arrays.asList(ByteBufferUtil.bytes(revision)));

            ConfigHelper.setRpcPort(job.getConfiguration(), CassandraSettings.CLUSTER_RPCPORT);
            ConfigHelper.setInitialAddress(job.getConfiguration(), CassandraSettings.CLUSTER_ADDRESS);
            ConfigHelper.setPartitioner(job.getConfiguration(), "RandomPartitioner"); //TODO what is this?
            ConfigHelper.setInputColumnFamily(job.getConfiguration(), CassandraSettings.KEYSPACE_NAME, CassandraSettings.TEST_COLUMN_FAMILY_NAME());
            ConfigHelper.setInputSlicePredicate(job.getConfiguration(), predicate);

            job.waitForCompletion(true);
        } catch (InterruptedException ex) {
            Logger.getLogger(CassandraHadoop.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CassandraHadoop.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {

            Logger.getLogger(CassandraHadoop.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    @Override
    public int run(String[] strings) throws Exception {
        System.out.println("Started...");
        performCalculation();

        return 0;
    }
}
