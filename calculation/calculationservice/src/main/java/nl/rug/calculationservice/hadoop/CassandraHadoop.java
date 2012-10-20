package nl.rug.calculationservice.hadoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.calculationservice.database.cassandra.CassandraSettings;
import nl.rug.calculationservice.database.cassandra.Generator;
import nl.rug.calculationservice.hadoop.mapreduce.AverageMapper;
import nl.rug.calculationservice.hadoop.mapreduce.AverageReducer;
import org.apache.cassandra.hadoop.ColumnFamilyInputFormat;
import org.apache.cassandra.hadoop.ConfigHelper;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
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

    private final String JOB_NAME = "averagecomplexity";
    private final String JOB_PATH = "/usr/frank/output";
    private final String REVISION = "1"; // The revision to perform the calculation on

    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        
        generator.populate();
        
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

            Job job = new Job(getConf(), JOB_NAME);

            job.setJarByClass(CassandraHadoop.class);

            job.setMapperClass(AverageMapper.class);
            job.setCombinerClass(AverageReducer.class);
            job.setReducerClass(AverageReducer.class);

            job.setMapOutputKeyClass(Text.class);
            job.setOutputKeyClass(ByteBuffer.class);
            
            job.setMapOutputValueClass(IntWritable.class);
            job.setOutputValueClass(List.class);
            
            Path file =  new Path(JOB_PATH);
            FileOutputFormat.setOutputPath(job, new Path(JOB_PATH));

            job.setInputFormatClass(ColumnFamilyInputFormat.class);  

            SlicePredicate predicate = new SlicePredicate();//.setColumn_names(Arrays.asList(ByteBufferUtil.bytes(REVISION)));
            SliceRange range = new SliceRange();
            
            range.setStart(ByteBufferUtil.EMPTY_BYTE_BUFFER);
            range.setFinish(ByteBufferUtil.EMPTY_BYTE_BUFFER);
            
            predicate.setSlice_range(range);
           
            
            ConfigHelper.setRpcPort(job.getConfiguration(), CassandraSettings.CLUSTER_RPCPORT);
            ConfigHelper.setInitialAddress(job.getConfiguration(), CassandraSettings.CLUSTER_ADDRESS);
            ConfigHelper.setPartitioner(job.getConfiguration(), "OrderPreservingPartitioner");
            ConfigHelper.setInputColumnFamily(job.getConfiguration(), CassandraSettings.KEYSPACE_NAME, CassandraSettings.REVISION_COLUMN_FAMILY_NAME);
            ConfigHelper.setInputSlicePredicate(job.getConfiguration(), predicate);

            job.waitForCompletion(true);
            
            //Perform the devision of total / number of files.
            calculateMean(file);
            
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
        
        System.out.println("Job started...");
        
        performCalculation();
        
        System.out.println("Job finished.");
        
        return 0;
        
    }

    private double calculateMean(Path path)
            throws IOException {
        FileSystem fs = FileSystem.get(getConf());
        Path file = new Path(path, "part-r-00000");
        double mean = 0;

        if (!fs.exists(file)) {
            
            throw new IOException("File not found!");
            
        }
        
        BufferedReader br = null;
        
        try {
            
            // Read the file from the HDFS
            br = new BufferedReader(new InputStreamReader(fs.open(file)));
            
            long count = 0;
            
            long total = 0;
            
            String line;
            
            while ((line = br.readLine()) != null) {
            
                StringTokenizer st = new StringTokenizer(line);

                // type (either total or count)
                String type = st.nextToken();

                // Get values
                if (type.equals("count")) {
                    String countLit = st.nextToken();
                    count = Long.parseLong(countLit);
                } else if (type.equals("total")) {
                    String lengthLit = st.nextToken();
                    total = Long.parseLong(lengthLit);
                } 
            }
            // average = total sum / number of elements;
            mean = (((double) total) / ((double) count));
            
            //For now just display the mean..
            System.out.println("The mean is: " + mean);
            
        } finally {
            
            br.close();
            
        }
        
        return mean;
    }
}
